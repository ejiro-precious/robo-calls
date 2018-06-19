package com.africastalking
package robo

import scala.concurrent.ExecutionContext

import akka.actor._
import akka.event.Logging
import akka.stream.ActorMaterializer

import com.typesafe.config.ConfigFactory

object Main extends App {

  val conf = ConfigFactory.load
  
  implicit val system = ActorSystem("RoboCallSystem", conf)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val log = Logging(system.eventStream, "Server")
  val boot = new ServiceBoot
  boot.bootup
}
