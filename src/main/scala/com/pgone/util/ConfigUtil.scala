package main.scala.com.pgone.util

import com.typesafe.config.ConfigFactory

/**
  * @author PGOne
  * @ date 2018/10/27
  */
object ConfigUtil {

  final val config =  ConfigFactory.load

  def getString(key: String, defaultValue: String = ""): String = {
    try {
      config.getString(key).trim
    } catch {
      case _: Exception => defaultValue
    }
  }

  def getInt(key: String, defaultValue: Int = 0): Int = {
    try {
      config.getString(key).trim.toInt
    } catch {
      case _: Exception => defaultValue
    }
  }

  def getLong(key: String, defaultValue: Long = 0): Long = {
    try {
      config.getString(key).trim.toLong
    } catch {
      case _: Exception => defaultValue
    }
  }

}
