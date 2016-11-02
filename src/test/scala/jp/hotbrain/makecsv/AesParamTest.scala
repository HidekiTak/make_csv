package jp.hotbrain.makecsv

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import org.junit.Test
import org.junit.Assert._

/**
  * Created by hidek on 2016/09/11.
  */
class AesParamTest {

  @Test
  def constructorTest(): Unit = {
    val base16 = "0123456789abcdef0123456789abcdef"
    val base1x = "0123456789abcdef0123456789abcde"
    val expected = Base16.toByteArray(base16)
    val md5 = MessageDigest.getInstance("MD5").digest(base1x.getBytes(StandardCharsets.US_ASCII))
    assertArrayEquals(expected, AesParam("1", base16, base1x).key)
    assertArrayEquals(md5, AesParam("1", base16, base1x).iv)
    assertArrayEquals(expected, AesParam("1", base1x, base16).iv)
    assertArrayEquals(md5, AesParam("1", base1x, base16).key)
  }
}
