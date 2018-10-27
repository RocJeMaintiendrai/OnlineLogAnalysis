package main.scala.com.pgone.spark

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import java.util.Calendar

import main.scala.com.pgone.util.ConfigUtil
import main.scala.com.pgone.model.AlertInfoConfig
import java.util.Date
import scala.collection.mutable.ArrayBuffer


/**
  * @author PGOne
  * @ date 2018/10/27
  *
  * Read custom warning keywords from mysql and use them for the spark app via broadcast variables
  */
object AlertWordBroadcast {

  private var lastUpdateTime = Calendar.getInstance.getTime
  private val refreshInterval = ConfigUtil.getLong("log.alertword.refresh.interval")

  @volatile private var instance: Broadcast[Seq[String]] = null


  /**
    * 每隔 log.alertword.refresh.interval 时间刷新广播变量值
    * @param sparkContext
    * @return
    */
  def refresh(sparkContext: SparkContext): Broadcast[Seq[String]] = {

    val currentTime = Calendar.getInstance.getTime
    val diffTime = currentTime.getTime - lastUpdateTime.getTime

    if (instance == null || diffTime >= refreshInterval) {
      synchronized {
        if (instance != null) {
          instance.unpersist
        }
        lastUpdateTime = new Date(System.currentTimeMillis)
        val alertWordArray = ArrayBuffer[String]()
        AlertInfoConfig.findAll.foreach(record => {
          println(s"add keyword: ${record.keyword} to alertWordArray")
          alertWordArray.append(record.keyword)
        })
        instance = sparkContext.broadcast(alertWordArray)
      }
    }
    instance
  }

}

