package main.scala.com.pgone.spark

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.influxdb.dto.Point


import main.scala.com.pgone.util.{CommUtils, ConfigUtil, InfluxConnectionPool, JsonUtil}
import main.scala.com.pgone.model.Log
import main.scala.com.pgone.util.MySQLUtil

/**
  * @author PGOne
  * @ date 2018/10/27
  */
object OnlineLogAnalysisApp {

  private val logLevels = Array("DEBUG", "INFO", "WARN", "ERROR", "FATAL")

  def start: Unit = {

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("OnlineLogAnalysisApp")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val topics = ConfigUtil.getString("kafka.topics").split(",").toSet
    // spark-streaming-kafka-0-8_2.11 (spark-streaming-kafka-0-10_2.11->test)
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> ConfigUtil.getString("kafka.metadata.broker.list"),
      "group.id" -> ConfigUtil.getString("kafka.group.id"),

      "auto.offset.reset" -> ConfigUtil.getString("kafka.auto.offset.reset") //spark-streaming-kafka-0-10_2.11 latest
    )

    val kafkaDirectStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc,
      kafkaParams,
      topics)

    InfluxConnectionPool.initialize(ConfigUtil.getString("influxdb.db"))

    //Convert json strings to CDHDetailLog objects and filter non-json exception data
    val logDirectStream = kafkaDirectStream.map(record => {
      val jsonObj = new JsonUtil(record._2.trim)
      if (CommUtils.hasSubStr(record._2, logLevels)) {

        new Log(
          jsonObj.getStringValue("hostname"),
          jsonObj.getStringValue("servicename"),
          jsonObj.getStringValue("time"),
          jsonObj.getStringValue("logtype"),
          jsonObj.getStringValue("loginfo")//, jsonObj.getStringValue("alertwords")
        )
      } else {
        null
      }
    }).filter(log => log != null)

    //Count the total number of log types and the total number of alert logs and output the results to InfluxDB
    logDirectStream.foreachRDD(rdd => {
      if(rdd.count() > 0) {
        //Get the latest alert keywords
        val alertWordList = AlertWordBroadcast.refresh(ssc.sparkContext)
        val logHasAlertRdd = rdd.map(log => {
          log.alertwords = CommUtils.getSubStr(log.loginfo, alertWordList.value.toArray).mkString(",")
          log
        })

        //Count the total number of each log type of rdd and the total number of alert logs
        val resultRDD = logHasAlertRdd.map(log => (
          s"${log.hostname}_${log.servicename}_ALERT", if(log.alertwords.trim.length > 0) 1 else 0))
          .reduceByKey(_+_)
          .union(logHasAlertRdd.map(log => (s"${log.hostname}_${log.servicename}_${log.logtype}", 1)).reduceByKey(_+_))

        //Output statistics to InfluxDB
        resultRDD.foreachPartition(partitionOfRecords => {

          val connection = InfluxConnectionPool.getConnectionFromPool
          println("get influxdb connection: " + connection)
          partitionOfRecords.foreach(record => {
            if (record._2 > 0) {
              println(s"write data: ${record._1} ${record._2}")
              connection.write(Point.measurement("logtype_count")
                .tag("host_service_logtype", record._1)
                .addField("count", record._2)
                .build()
              )
            }
          })
          InfluxConnectionPool.returnConnectionToPool(connection)
        })
      } else {
        println("No logs in this time interval")
      }
    })

    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate

  }


  def main(args: Array[String]): Unit = {

    MySQLUtil.init
    //MySQLUtil.initData
    start

  }

}
