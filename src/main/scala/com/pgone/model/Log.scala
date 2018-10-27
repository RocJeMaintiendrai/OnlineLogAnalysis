package main.scala.com.pgone.model

/**
  * @author PGOne
  * @ date 2018/10/27
  *
  * The field name is consistent with json because json needs to rotate the object.
  */
case class Log(hostname: String, servicename: String, time: String, logtype: String, loginfo: String) {
  var alertwords = ""
}
