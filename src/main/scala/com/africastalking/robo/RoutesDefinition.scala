package com.africastalking
package robo

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import akka.http.scaladsl.server.Route
import akka.actor.{ ActorRef, ActorSystem }
import akka.stream.Materializer
import akka.util.Timeout
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.model.StatusCodes._
import akka.pattern.ask

import org.slf4j.LoggerFactory

/** Http Routes extend this trait to provide some functionality **/
trait RoutesDefinition {
  import concurrent.duration._

  /** very dependant on use case - note**/
  implicit val endpointTimeout: Timeout = Timeout(60 seconds)

  private val logger = LoggerFactory.getLogger(classOf[RoutesDefinition])

  def routes(implicit system: ActorSystem, ec: ExecutionContext, mater: Materializer): Route

  def service(msg: Any, ref: ActorRef): Future[Any] = {
    ref ? msg
  }

  def serviceAndComplete(msg: Any, ref: ActorRef): Route = {
    val fut = service(msg, ref)
    onComplete(fut){
      case util.Success(f) ⇒
        val resp = f.toString
        complete(OK, HttpEntity(ContentTypes.`text/plain(UTF-8)`, resp))

      case util.Failure(ex) ⇒
        complete((InternalServerError, "Sorry Failure when processing your request ---" + ex))

      case _ ⇒
        complete((BadRequest, "Error, Sorry I couldn't understand your request\n "))
    }
  }
}
