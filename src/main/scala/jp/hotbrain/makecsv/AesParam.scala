package jp.hotbrain.makecsv

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto._
import javax.crypto.spec._

/**
  * Created by hideki takada on 2016/09/10.
  */
case class AesParam(
                     serial: String,
                     keyStr: String,
                     ivStr: String
                   ) {

  lazy val key: Array[Byte] = AesParam.getArray(keyStr)
  
  lazy val iv: Array[Byte] = AesParam.getArray(ivStr)

  val longSerial: Long = java.lang.Long.parseLong(serial)

  def getCipherOf(mode: Int): Cipher = {
    val keySpec = new SecretKeySpec(key, "AES")
    val cipher = Cipher.getInstance("AES/PCBC/PKCS5Padding")
    val ivspec = new IvParameterSpec(iv)
    cipher.init(mode, keySpec, ivspec)
    cipher
  }

  override def toString: String = {
    s"""serial:$serial,key:"$keyStr",iv:"$ivStr""""
  }
}

/**
  * 128Bit->16Byte ArrayByte のみ対応
  */
object AesParam {

  private[this] lazy val regex16 = "([0-9a-fA-F]{32})".r

  def getArray(str: String): Array[Byte] = {
    str match {
      case regex16(base16) => Base16.toByteArray(base16)
      case str => MessageDigest.getInstance("MD5").digest(str.getBytes(StandardCharsets.US_ASCII))
    }
  }
}
