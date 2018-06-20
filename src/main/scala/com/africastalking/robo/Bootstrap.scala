package com.africastalking
package robo

import akka.actor.{ ActorRef, ActorSystem }
import akka.stream.ActorMaterializer

trait Bootstrap {
  def bootup(
    implicit actorSystem: ActorSystem,
    actorMaterializer: ActorMaterializer
  ): ActorRef
}