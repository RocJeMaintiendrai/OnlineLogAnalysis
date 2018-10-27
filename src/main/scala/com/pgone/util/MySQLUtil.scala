package main.scala.com.pgone.util

import main.scala.com.pgone.model.AlertInfoConfig
import scalikejdbc._
import scalikejdbc.config.DBs


/**
  * @author PGOne
  * @ date 2018/10/27
  */
object MySQLUtil {

  def init = {
    DBs.setupAll
  }

  def close = {
    DBs.closeAll
  }

  /**
    * Initialize table structure and default alert type
    * @return
    */
  def initData = {
    initTable
    AlertInfoConfig.create("oom")
    AlertInfoConfig.create("timeout")
    AlertInfoConfig.create("exception")
  }

  /**
    * Initialize table structure
    *
    */
  def initTable() {
    DB readOnly { implicit s =>
      try {
        sql"select 1 from alert_info_config limit 1".map(_.long(1)).single.apply()
      } catch {
        case e: java.sql.SQLException =>
          DB autoCommit { implicit s =>
            sql"""
              create table alert_info_config (
                id bigint auto_increment primary key,
                keyword varchar(512) DEFAULT ""
              );
            """.execute.apply()
          }
      }
    }
  }


}
