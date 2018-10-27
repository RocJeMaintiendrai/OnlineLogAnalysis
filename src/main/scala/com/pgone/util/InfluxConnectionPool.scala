package main.scala.com.pgone.util

import java.util.Calendar
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}

import com.typesafe.config.ConfigFactory
import org.influxdb.{InfluxDB, InfluxDBFactory}


/**
  * @author PGOne
  * @ date 2018/10/27
  */
object InfluxConnectionPool {
  private final val MAX_POOL_SIZE = 10
  private val config =  ConfigFactory.load
  private val influxUrl = config.getString("influxdb.url").trim
  private val influxPort = config.getString("influxdb.port").trim.toLong
  private val influxUser = config.getString("influxdb.user").trim
  private val influxPassword = config.getString("influxdb.password").trim


  val queue = new LinkedBlockingQueue[InfluxDB]()

  def initialize(database: String): Unit = {
    while (!isConnectionPoolFull) {
      var connection = createNewConnection(database)
      println("new connection: " + connection)
      queue.put(connection)
    }
  }

  private def isConnectionPoolFull: Boolean = {
    if (queue.size < MAX_POOL_SIZE) false else true
  }

  def getConnectionFromPool: InfluxDB = {
    println("current connections ==> " + queue.size)
    if (queue.size > 0) {
      val connection = queue.take()
      connection
    } else {
      System.err.println("InfluxDB connection limit reached.")
      null
    }

  }

  private def createNewConnection(database: String) = {
    val influxDB = InfluxDBFactory.connect(s"http://${influxUrl}:${influxPort}", influxUser, influxPassword)
    influxDB.enableBatch(10, 100, TimeUnit.MILLISECONDS)
    influxDB.setDatabase(database)
    //influxDB.setRetentionPolicy(database + "_rp")
    influxDB
  }

  def returnConnectionToPool(connection: InfluxDB): Unit = {
    queue.put(connection)
  }
}
