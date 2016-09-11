package jp.hotbrain.makecsv

/**
  * Created by hidek on 2016/09/10.
  */
trait FileConfig {

  def aesParam: Option[AesParam]

  def gzip: Boolean
}
