package main.scala.com.pgone.model

import scalikejdbc._

/**
  * @author PGOne
  * @ date 2018/10/27
  *
  * Description:
  *
  * Custom alert keywords (stored in MySQL)
  *
  * CRUD method that provides LogTypeCount
  *

  */

case class AlertInfoConfig(id: Long, keyword: String) {
  def save()(implicit session: DBSession = AlertInfoConfig.autoSession): AlertInfoConfig = AlertInfoConfig.save(this)(session)
  def destroy()(implicit session: DBSession = AlertInfoConfig.autoSession): Unit = AlertInfoConfig.destroy(id)(session)
}


object AlertInfoConfig extends SQLSyntaxSupport[AlertInfoConfig] {

  def apply(c: SyntaxProvider[AlertInfoConfig])(rs: WrappedResultSet): AlertInfoConfig = apply(c.resultName)(rs)
  def apply(c: ResultName[AlertInfoConfig])(rs: WrappedResultSet): AlertInfoConfig = new AlertInfoConfig(
    id = rs.get(c.id),
    keyword = rs.get(c.keyword))

  val c = AlertInfoConfig.syntax("c")

  def find(id: Long)(implicit session: DBSession = autoSession): Option[AlertInfoConfig] = withSQL {
    select.from(AlertInfoConfig as c).where.eq(c.id, id)
  }.map(AlertInfoConfig(c)).single.apply()

  def findAll()(implicit session: DBSession = autoSession): List[AlertInfoConfig] = withSQL {
    select.from(AlertInfoConfig as c).orderBy(c.id)
  }.map(AlertInfoConfig(c)).list.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(AlertInfoConfig as c)
  }.map(rs => rs.long(1)).single.apply().get

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[AlertInfoConfig] = withSQL {
    select.from(AlertInfoConfig as c)
      .where.append(sqls"${where}")
      .orderBy(c.id)
  }.map(AlertInfoConfig(c)).list.apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(AlertInfoConfig as c).where.append(sqls"${where}")
  }.map(_.long(1)).single.apply().get

  def create(keyword: String)(implicit session: DBSession = autoSession): AlertInfoConfig = {

    val id = withSQL {
      insert.into(AlertInfoConfig).namedValues(column.keyword -> keyword)
    }.updateAndReturnGeneratedKey.apply()
    AlertInfoConfig(id = id, keyword = keyword)
  }

  def save(m: AlertInfoConfig)(implicit session: DBSession = autoSession): AlertInfoConfig = {
    withSQL {
      update(AlertInfoConfig).set(
        column.keyword -> m.keyword).where.eq(column.id, m.id)
    }.update.apply()
    m
  }

  def destroy(id: Long)(implicit session: DBSession = autoSession): Unit = withSQL {
    delete.from(AlertInfoConfig).where.eq(column.id, id)
  }.update.apply()



}
