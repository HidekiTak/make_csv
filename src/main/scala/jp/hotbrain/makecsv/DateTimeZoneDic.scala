package jp.hotbrain.makecsv

import org.joda.time.DateTimeZone

/**
  * Created by hidek on 2016/09/10.
  */
object DateTimeZoneDic {

  final val UTC = DateTimeZone.forID("UTC")

  final val tz_id_dic = Map[String, String](
    "JST" -> "Asia/Tokyo")

  def getDateTimeZone(str: Option[String]): DateTimeZone = {
    str.map {
      s =>
        val hm = s.split(':')
        if (1 == hm.length) {
          DateTimeZone.forID(tz_id_dic.getOrElse(s, s))
        } else {
          val hour = java.lang.Integer.parseInt(hm(0))
          val min = java.lang.Integer.parseInt(hm(1)) * {
            if (hour < 0) -1 else 1
          }
          DateTimeZone.forOffsetHoursMinutes(hour, min)
        }
    }.getOrElse(UTC)
  }
}
