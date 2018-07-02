package com.africastalking.robo
package utils

import scala.concurrent.{ ExecutionContext, Future }

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import com.africastalking.robo.free.DBService
import com.africastalking.robo.persistence.{ DatabaseService, DbioServiceInstances, Service }
import com.africastalking.robo.repository.VoiceSessionRepository

trait DBSetup extends DbioServiceInstances {

  implicit def executionContext: ExecutionContext

  lazy val config = DatabaseConfig.forConfig[JdbcProfile]("robo.db")
  val db: JdbcProfile#Backend#Database = config.db

  /** This is where we define our Database, it is actually our interpreter for our DSL  **/
  implicit lazy val dbService: Service[DBService.Action, Future] = DatabaseService(new VoiceSessionRepository)
}
