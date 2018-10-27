package main.scala.com.pgone.util

import org.json.JSONObject

/**
  * @author PGOne
  * @ date 2018/10/27
  */
class JsonUtil(jsonStr: String) {

  val jsonObject = new JSONObject(jsonStr)

  def getStringValue(key: String, default: String = "") = {
    try {
      jsonObject.get(key).asInstanceOf[String]
    } catch {
      case _: Exception => default
    }
  }

  def put(key: String, value: Boolean): Unit = {
    jsonObject.put(key, value)
  }

  def put(key: String, value: String): Unit = {
    jsonObject.put(key, value)
  }
}


object JsonUtil {
  def apply(jsonStr: String): JsonUtil = new JsonUtil(jsonStr: String)
}

