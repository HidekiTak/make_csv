package jp.hotbrain.makecsv

import java.io._
import java.util.zip.GZIPInputStream
import javax.crypto.{Cipher, CipherInputStream}


trait DecoderIf {
  def exec(callback: OutputStream => Unit): Unit
}

class Decoder(val exportFileName: String) extends DecoderIf {

  def exec(callback: OutputStream => Unit): Unit = {
    val fos = new BufferedOutputStream(new FileOutputStream(exportFileName))
    try {
      callback(fos)
    } finally {
      fos.close()
    }
  }
}

case class DecodeSetting(
                          importFileName: String,
                          aesParam: Option[AesParam],
                          gzip: Boolean
                        ) extends FileConfig {

  def getInputStream: InputStream = {
    new BufferedInputStream(new FileInputStream(importFileName))
  }

  def decodeTo(decoder: DecoderIf): Unit = {
    Decoder.decodeTo(decoder, this)
  }
}

/**
  * Created by hidek on 2016/09/10.
  */
object Decoder {

  def decodeTo(decoder: DecoderIf, setting: DecodeSetting): Unit = {
    decode(decoder, setting.getInputStream, setting)
  }

  def decode(decoder: DecoderIf, is: InputStream, file: FileConfig): Unit = {
    val fis = getFis(file, is)
    decoder.exec {
      os =>
        val buf = new Array[Byte](4096)
        var count = fis.read(buf)
        while (0 < count) {
          if (count == buf.length) {
            os.write(buf)
          } else {
            os.write(buf, 0, count)
          }
          count = fis.read(buf)
        }
    }
  }

  private[this] def getFis(file: FileConfig, is: InputStream): InputStream = {
    getGzip(file, getAes(file, is))
  }

  private[this] def getGzip(file: FileConfig, is: InputStream): InputStream = {
    if (file.gzip) {
      new GZIPInputStream(is)
    } else {
      is
    }
  }

  private[this] def getAes(file: FileConfig, is: InputStream): InputStream = {
    file.aesParam.map {
      aes =>
        val ver = new Array[Byte](2)
        is.read(ver)
        if (0 != ver(0) || 1 != ver(1)) {
          throw new Exception("File version is not match")
        }

        val serial = new Array[Byte](8)
        is.read(serial)
        val serialL = java.nio.ByteBuffer.wrap(serial).getLong
        if (serialL != aes.longSerial) {
          throw new Exception(s"""serial not match. on config "${aes.longSerial}", but on file "$serialL"""")
        }
        new CipherInputStream(is, aes.getCipherOf(Cipher.DECRYPT_MODE))
    }.getOrElse(is)
  }
}
