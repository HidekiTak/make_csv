package jp.hotbrain.makecsv

import java.io.FileInputStream
import java.util.Properties

/**
  * Created by hidek on 2016/09/11.
  */
object Environment {

  private[this] var _configFile = Map[String, String]()

  private[this] def toLowerPeriod(str: String) = str.toLowerCase.replaceAllLiterally("_", ".")

  private[this] final val _properties: Map[String, String] = {
    propsToMap(System.getProperties)
  }

  private[this] final val _envs: Map[String, String] = {
    val envs = System.getenv()
    envs.keySet().toArray(Array[String]()).map(
      key => (toLowerPeriod(key), envs.get(key))
    ).toMap
  }

  def setConfigFile(fileName: String): Unit = {
    val props = new Properties
    props.load(new FileInputStream(fileName))
    _configFile = propsToMap(props)
  }

  def allConfigs(): Array[(String, String)] = {
    Seq(_properties, _configFile).foldLeft(scala.collection.mutable.HashMap[String, String]())(
      (result, map) =>
        map.foldLeft(result)(
          (result, kvp) => {
            result.put(kvp._1, kvp._2)
            result
          })).toSeq.sortWith((a, b) => 0 > a._1.compareToIgnoreCase(b._1)).toArray
  }

  private[this] def propsToMap(props: Properties): Map[String, String] = {
    props.stringPropertyNames().toArray(Array[String]()).map(
      name => (toLowerPeriod(name), props.getProperty(name))
    ).toMap
  }

  def getValue[T](key: String, callback: String => T): Option[T] = {
    getValue(key).map(callback)
  }

  def getValue(key: String, default: String = null): Option[String] = {
    val k = toLowerPeriod(key)
    _configFile.get(k).orElse(
      _properties.get(k)
    ).orElse(
      _envs.get(k)
    ).orElse(
      Option(default)
    )
  }
}
