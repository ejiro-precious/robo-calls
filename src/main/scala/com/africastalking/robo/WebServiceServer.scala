package com.africastalking
package robo

import java.util.concurrent.TimeoutException

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

import akka.actor.SupervisorStrategy.{ Restart, Stop }
import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import com.africastalking.robo.free.DBService
import com.africastalking.robo.persistence.Service
import com.africastalking.robo.utils.{ Configs, DBSetup, HttpClient }

class WebServiceServer(implicit materializer: ActorMaterializer) extends Actor with ActorLogging with DBSetup {

  implicit val system = context.system
  override implicit def executionContext: ExecutionContext = system.dispatcher

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
      case _: ActorInitializationException ⇒ Stop
      case _: IllegalArgumentException ⇒ Stop
      case _: IllegalStateException    ⇒ Restart
      case _: TimeoutException         ⇒ Stop
      case _: Exception                ⇒ Stop
    }

  Http().bindAndHandle(
    new ServiceRoutes with HttpClient {
      def actorContext: ActorContext = context
      implicit val system: ActorSystem = context.system
      override implicit val databaseService: Service[DBService.Action, Future] = dbService
    }.routes,
    Configs.htttpInterface,
    Configs.httpPort
  )

  println("Server started at 127.0.0.1:8080")

  def receive: Receive = {
    case Terminated ⇒
      println("Received Terminated from " + sender().toString())
      sender() ! PoisonPill
  }

}
