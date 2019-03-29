package jp.hotbrain.makecsv

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, OutputStream}
import java.nio.charset.StandardCharsets

import org.junit.Assert._
import org.junit.Test

/**
  * Created by hidek on 2016/09/10.
  */
class XsvConvertTest {

  class XsvConv(val body: String) extends EncoderIf {
    def export(os: OutputStream): Unit = {
      os.write(body.getBytes(StandardCharsets.UTF_8))
    }
  }

  case class XsvEnc(aesParam: Option[AesParam], gzip: Boolean) extends FileConfig

  class XsvDecoder() extends DecoderIf {

    private[this] lazy val _buf = new ByteArrayOutputStream()

    override def exec(callback: OutputStream => Unit): Unit = callback(_buf)

    def buf(): Array[Byte] = _buf.toByteArray
  }

  @Test
  def aesTest(): Unit = {
    val body =
      """group,name,from_date
        |"00001","グループ1","2016/09/03 22:37:59"
        |"00002","ぐる～ぷ２","2016/09/03 22:37:59"
        |"00003","Group3","2016/09/03 22:37:59"
        |"00009","LastGroup","2016/09/03 22:37:59"
        | """.stripMargin
    val conf = new XsvConv(body)
    Seq(
      None,
      Option(AesParam("32", "1234", "7056")),
      Option(AesParam("24", "0123456789abcdef0123456789ABCDEF", "9876543210fedcba9876543210FEDCBA"))
    ).flatMap(x => Seq(XsvEnc(x, false), XsvEnc(x, true))).foreach(z => aesTestSub(conf, z))
  }

  private[this] def aesTestSub(conf: XsvConv, enc: XsvEnc): Unit = {
    val buf = new ByteArrayOutputStream()
    Encoder.encode(conf, enc, buf)
    buf.close()
    val arr = buf.toByteArray
    println(s"""${enc.toString}: ${arr.length}""")
    val inpBuf = new ByteArrayInputStream(arr)
    val dec = new XsvDecoder()
    Decoder.decode(dec, inpBuf, enc)
    val result = new String(dec.buf(), StandardCharsets.UTF_8)
    assertEquals(enc.toString, conf.body, result)
  }
}
