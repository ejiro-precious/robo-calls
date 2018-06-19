package com.africastalking
package robo

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.stream.ActorMaterializer

class ServiceBoot extends Bootstrap {
  def bootup(
       implicit actorSystem: ActorSystem,
       actorMaterializer: ActorMaterializer
   ): ActorRef = {
    /** Boots up our main actor this actor should extend a Snoop trait to so it can constantly
      * send it health-check to a remote http server presently it does'nt
      */
    actorSystem.actorOf(Props(classOf[WebServiceServer], actorMaterializer))
  }
}