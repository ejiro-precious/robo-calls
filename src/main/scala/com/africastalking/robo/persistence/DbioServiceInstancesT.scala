package com.africastalking.robo
package persistence

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.{ higherKinds, implicitConversions }

import slick.driver.JdbcProfile

import cats.Monad

import com.africastalking.robo.free.DbioMonadT
import com.africastalking.robo.persistence.MysqlDriver.api._

/** This class extends DbioMonad to have an instance of DBIO monad **/
trait DbioServiceInstances extends DbioMonadT {

  /** This is the main db used for the sql queries **/
  def db: JdbcProfile#Backend#Database

  implicit def executionContext: ExecutionContext

  /** This convert a Service[DSL, DBIO] to Service[DSL, Future] implicitly **/
  implicit def toFuture[DSL[_]](dbioService: Service[DSL, DBIO]): Service[DSL, Future] = new Service[DSL, Future] {
    override def execute[A](program: Program[A])(implicit M: Monad[Future]): Future[A] = db.run(dbioService.execute(program))
  }
}
