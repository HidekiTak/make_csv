package jp.hotbrain.makecsv

import java.io.{BufferedOutputStream, File, FileOutputStream, OutputStream}
import java.util.logging.Logger
import java.util.zip.GZIPOutputStream
import javax.crypto.{Cipher, CipherOutputStream}

/**
  * Created by hidek on 2016/09/10.
  */

trait EncoderIf {
  def export(os: OutputStream): Unit
}

case class Encoder(
                    fileName: String,
                    aesParam: Option[AesParam],
                    gzip: Boolean
                  ) extends FileConfig {

  def encodeOf(conf: EncoderIf): Unit = {
    Encoder.encodeOf(conf, this)
  }


}

/**
  * Created by hidek on 2016/09/10.
  */
object Encoder {


  def encodeOf(conf: EncoderIf, file: Encoder): Unit = {
    val f = new File(file.fileName)
    Logger.getLogger(getClass.getName).info(s"""save to ${f.getAbsolutePath}""")
    encode(conf, file, new BufferedOutputStream(new FileOutputStream(f)))
  }

  def encode(conf: EncoderIf, file: FileConfig, os: OutputStream): Unit = {
    val osb = new BufferedOutputStream(getFos(file, os))
    try {
      conf.export(osb)
    } finally {
      osb.flush()
      osb.close()
    }
  }

  private[this] def getFos(file: FileConfig, os: OutputStream): OutputStream = {

    getGzip(file, getAes(file, os))
  }

  private[this] def getGzip(file: FileConfig, os: OutputStream): OutputStream = {
    if (file.gzip) {
      new GZIPOutputStream(os)
    } else {
      os
    }
  }


  private[this] def getAes(file: FileConfig, os: OutputStream): OutputStream = {
    file.aesParam.map {
      aes =>
        os.write(Array[Byte](0, 1))
        val buf = java.nio.ByteBuffer.allocate(8)
        buf.putLong(aes.longSerial)
        os.write(buf.array())
        new CipherOutputStream(os, aes.getCipherOf(Cipher.ENCRYPT_MODE))
    }.getOrElse(os)
  }

}
