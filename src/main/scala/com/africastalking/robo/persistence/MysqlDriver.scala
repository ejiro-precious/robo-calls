package com.africastalking.robo
package persistence

object MysqlDriver extends slick.driver.MySQLDriver {

  object MsqlAPI extends API

  override val api = MsqlAPI
}
