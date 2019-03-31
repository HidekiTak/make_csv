package jp.hotbrain.makecsv

/**
  * Created by hideki takada on 2016/09/10.
  */
object Main {
  def main(args: Array[String]): Unit = {
    try {
      if (null != args && 0 <= args.length) {
        args(0).toLowerCase() match {
          case "export" if 3 == args.length =>
            Environment.setConfigFile(args(1))
            encode(args(2))
            println("ok")
            System.exit(0)
          case "decode" if 4 == args.length =>
            Environment.setConfigFile(args(1))
            decode(args(2), args(3), ungzip = true)
            println("ok")
            System.exit(0)
          case "decrypt" if 4 == args.length =>
            Environment.setConfigFile(args(1))
            decode(args(2), args(3), ungzip = false)
            println("ok")
            System.exit(0)
          case _ =>
        }
      }
      println(
        """Usage:
          |  java -jar make_csv_assembly_1.0.0.jar export [encode.cnf] [export_file]
          |  java -jar make_csv_assembly_1.0.0.jar decode [decode.cnf] [import_file] [export_file]
          |  java -jar make_csv_assembly_1.0.0.jar decrypt [decode.cnf] [import_file] [export_file]
          |          |
          |[encode.cnf]
          |serial=201609101600
          |
          |file.aes.key=[KeyStr]
          |file.aes.iv=[IVStr]
          |
          |file.gzip=false
          |file.col_sep=,
          |file.row_sep=\n
          |file.charset=UTF-8
          |file.timezone=UTC
          |file.dtformat=yyyy/MM/dd HH:mm:ss
          |
          |db.con=jdbc:mysql://localhost/bspark?user=root&password=root&useSSL=false
          |db.sql=SELECT * FROM `master`
          |
          |[decode.cnf|decrypt.cnf]
          |file.serial=201609101700
          |
          |file.aes.key=[KeyStr]
          |file.aes.iv=[IVStr]
          |
          |file.gzip=false
          |
          | """.stripMargin)
    } catch {
      case ex: Throwable => println(ex); println
    }
  }

  private[this] def getXsvExportSetting: XsvExportSetting = {
    val constr = Environment.getValue("db.con").getOrElse(throw new Exception("no Sql Connection String"))
    val sqlstr = Environment.getValue("db.sql").getOrElse(throw new Exception("no Select Sql String"))
    XsvExportSetting(
      constr = constr,
      sqlstr = sqlstr,
      optColSep = Environment.getValue("file.col_sep"),
      optRowSep = Environment.getValue("file.row_sep"),
      optCharset = Environment.getValue("file.charset"),
      optTimeZone = Environment.getValue("file.timezone"),
      optDatetimeFormat = Environment.getValue("file.dtformat")
    )
  }


  private[this] def encode(exportFileName: String): Unit = {
    println(s"Encode: to $exportFileName")
    println
    val conf = getXsvExportSetting
    Encoder(
      fileName = exportFileName,
      aesParam = aesParam(),
      gzip = Environment.getValue("file.gzip", v => "true".equalsIgnoreCase(v)).getOrElse(false)
    ).encodeOf(conf)
  }

  private[this] def decode(importFileName: String, exportFileName: String, ungzip: Boolean): Unit = {
    println(s"Decode: $importFileName to $exportFileName")
    println
    val aes = aesParam()
    val gzip = ungzip && Environment.getValue("file.gzip", v => "true".equalsIgnoreCase(v)).getOrElse(false)
    if (aes.isEmpty && !gzip) {
      println("nothing to do")
    } else {
      DecodeSetting(
        importFileName = importFileName,
        aesParam = aes,
        gzip = gzip
      ).decodeTo(new Decoder(exportFileName))
    }
  }

  private[this] def aesParam(): Option[AesParam] = {
    val key = Environment.getValue("file.aes.key").map(_.trim).getOrElse("")
    val iv = Environment.getValue("file.aes.iv").map(_.trim).getOrElse("")
    if (key.isEmpty || iv.isEmpty) {
      None
    } else {
      Option(AesParam(Environment.getValue("serial").getOrElse("0"), key, iv))
    }
  }
}
