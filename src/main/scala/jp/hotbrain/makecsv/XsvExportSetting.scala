package jp.hotbrain.makecsv

import java.io.OutputStream
import java.nio.charset.{Charset, StandardCharsets}
import java.sql.{DriverManager, ResultSet, Timestamp}
import java.util.logging.Logger

import org.joda.time.DateTimeZone
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * XsvExportSetting
  * Created by hideki takada on 2016/09/10.
  *
  * @param constr            DbConnectionString
  * @param sqlstr            Select Sql String
  * @param optColSep         Columns Separator (default:",")
  * @param optRowSep         CR/LF (default:"\r\n")
  * @param optCharset        export file charset (default:"UTF-8")
  * @param optTimeZone       export DateTime String's TimeZone (JST|UTC|Asia/Tokyo|+09:00 etc)  (default:"UTC)
  * @param optDatetimeFormat export DateTImeString Format (default:"yyyy/MM/dd HH:mm:ss")
  */
case class XsvExportSetting(
                             constr: String,
                             sqlstr: String,
                             optColSep: Option[String] = None,
                             optRowSep: Option[String] = None,
                             optCharset: Option[String] = None,
                             optTimeZone: Option[String] = None,
                             optDatetimeFormat: Option[String] = None
                           ) extends EncoderIf {

  lazy val charset: Charset = optCharset.map(Charset.forName).getOrElse(StandardCharsets.UTF_8)

  lazy val colSepStr: String = optColSep.getOrElse(",")
  lazy val rowSepStr: String = optRowSep.getOrElse("\r\n")
  lazy val colSep: Array[Byte] = colSepStr.getBytes(charset)
  lazy val rowSep: Array[Byte] = rowSepStr.getBytes(charset)


  lazy val timeZone: DateTimeZone = DateTimeZoneDic.getDateTimeZone(optTimeZone)
  lazy val datetimeFormat: DateTimeFormatter = DateTimeFormat.forPattern(optDatetimeFormat.getOrElse("yyyy/MM/dd HH:mm:ss")).withZone(timeZone)

  def export(os: OutputStream): Unit = {
    XsvExportSetting.export(os, this)
  }

  XsvExportSetting.checkDriver(constr)
}


object XsvExportSetting {

  private[this] final val drivers = Map(
    "jdbc:mysql://" -> "com.mysql.jdbc.Driver"
  )

  private def checkDriver(constr: String): Unit = {
    drivers.find(d => constr.startsWith(d._1)).map(x => Class.forName(x._2)).orElse(throw new Exception(s""""$constr" is not supported """))
  }

  private final val _logger = Logger.getLogger(getClass.getName)

  def export(os: OutputStream, conf: XsvExportSetting): Unit = {
    val con = DriverManager.getConnection(conf.constr)
    if (null == con) {
      _logger.severe("no db config or driver")
      return
    }
    try {
      val stmt = con.createStatement()
      try {
        val rs = stmt.executeQuery(conf.sqlstr)
        try {
          if (!rs.next()) {
            _logger.severe("no data")
            return
          }
          val headers: Array[(String, Int)] = getHeaders(rs).zipWithIndex
          writeHeader(os, headers, conf)
          do {
            writeData(os, headers, rs.getObject, conf)
          } while (rs.next)
          _logger.info("ok")
        } finally {
          rs.close()
        }
      } finally {
        stmt.close()
      }
    } finally {
      con.close()
    }
  }

  private[this] def getHeaders(rs: ResultSet): Array[String] = {
    val meta = rs.getMetaData
    val count = meta.getColumnCount
    val result = new Array[String](count)
    var i = 0
    while (i < count) {
      result(i) = meta.getColumnName(i + 1) // getColumnName's parameter is from 1
      i = i + 1
    }
    result
  }

  def writeHeader(os: OutputStream, headers: Array[(String, Int)], conf: XsvExportSetting): Unit = {
    headers.foreach {
      zwi =>
        if (0 != zwi._2) {
          os.write(conf.colSep)
        }
        if (0 <= zwi._1.indexOf(conf.colSepStr) || 0 <= zwi._1.indexOf(conf.rowSepStr)) {
          outputStr(os, zwi._1, conf)
        } else {
          os.write(zwi._1.getBytes(conf.charset))
        }
    }
    os.write(conf.rowSep)
  }

  def writeData(os: OutputStream, headers: Array[(String, Int)], callback: String => Any, conf: XsvExportSetting): Unit = {
    headers.foreach {
      header =>
        if (0 < header._2) {
          os.write(conf.colSep)
        }
        callback(header._1) match {
          case str: String =>
            outputStr(os, str, conf)
          case ts: Timestamp =>
            outputStr(os, conf.datetimeFormat.print(ts.getTime), conf)
          case x: Any =>
            os.write(x.toString.getBytes(conf.charset))
        }
    }
    os.write(conf.rowSep)
  }

  private[this] def outputStr(os: OutputStream, str: String, conf: XsvExportSetting): Unit = {
    os.write('"')
    os.write(str.replaceAllLiterally("\\","\\\\").replaceAllLiterally("\"", "\"\"").replaceAllLiterally("\r","\\r").replaceAllLiterally("\n","\\n").replaceAllLiterally("\t","\\t").getBytes(conf.charset))
    os.write('"')
  }
}
