package com.africastalking
package robo

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps

import akka.actor.{ ActorContext, ActorRef, ActorSystem, Props }
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{ path, _}
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.directives.DebuggingDirectives.logRequestResult

import org.slf4j.LoggerFactory

import com.africastalking.robo.state.ServiceProtocol._
import com.africastalking.robo.state.ServiceActor
import com.africastalking.robo.utils.XMLResponses._
import com.africastalking.robo.utils.{ClientResponse, Configs, HttpClient}
import com.africastalking.robo.api.call.TestCalling
import com.africastalking.robo.domain.VoiceSessionHops
import com.africastalking.robo.free.DBService
import com.africastalking.robo.persistence.Service

trait ServiceRoutes extends RoutesDefinition with HttpClient {

  def actorContext: ActorContext

  implicit val databaseService: Service[DBService.Action, Future]

  private val logger = LoggerFactory.getLogger(classOf[ServiceRoutes])

  private def getOrCreateActor(id: String): ActorRef = actorContext.child(id) getOrElse {
    actorContext.actorOf(Props(classOf[ServiceActor], databaseService), name = id)
  }

  def routes(implicit system: ActorSystem, ec: ExecutionContext, mater: Materializer): Route =
    path("voice") {
      logRequestResult("voice", Logging.DebugLevel) {
        extractRequest { _ ⇒
          formFields('sessionId, 'isActive.as[Int], 'callerNumber, 'direction /*'status 'callSessionState*/) {
            (sessionId, active, callerNumber, direction /*status, callSessionStatus */) ⇒
            if (direction == "Inbound") {
              val str = new StringBuilder()
              val response = str.append(rejectElem)
              complete(OK, HttpEntity(ContentTypes.`text/plain(UTF-8)`, response.toString()))
            }
            else {
              val ref = getOrCreateActor(sessionId)
              val msg = VoiceCall(sessionId, active, callerNumber)
              serviceAndComplete(msg, ref)
            }

          }
        }
      }
    } ~
    path("zerostage") {
      logRequestResult("zerostage", Logging.DebugLevel) {
        extractRequest { _: HttpRequest ⇒
          formFields('sessionId, 'isActive.as[Int], 'callerNumber, 'dtmfDigits.?) { (sessionId, isActive, callerNumber, dtmfDigits) ⇒
            val ref = getOrCreateActor(sessionId)
            val msg = ZeroStageDigits(sessionId, isActive, callerNumber, dtmfDigits)
            serviceAndComplete(msg, ref)
          }
        }
      }
    } ~
    path("firststage") {
      logRequestResult("firststage", Logging.DebugLevel) {
        extractRequest { _: HttpRequest ⇒
          formFields('sessionId, 'isActive.as[Int], 'callerNumber, 'dtmfDigits.?) { (sessionId, isActive, callerNumber, dtmfDigits) ⇒
            val ref = getOrCreateActor(sessionId)
            val msg = FirstStageDigits(sessionId, isActive, callerNumber, dtmfDigits)
            serviceAndComplete(msg, ref)
          }
        }
      }
    } ~
    path("secondstage") {
      logRequestResult("secondstage", Logging.DebugLevel) {
        extractRequest { _: HttpRequest ⇒
          formFields('sessionId, 'isActive.as[Int], 'callerNumber, 'dtmfDigits.?) { (sessionId, isActive, callerNumber, dtmfDigits) ⇒
            val ref = getOrCreateActor(sessionId)
            val msg = SecondStageDigits(sessionId, isActive, callerNumber, dtmfDigits)
            serviceAndComplete(msg, ref)
          }
        }
      }
    } ~
    path("thirdstage") {
      logRequestResult("thirdstage", Logging.DebugLevel) {
        extractRequest { _: HttpRequest ⇒
          formFields('sessionId, 'isActive.as[Int], 'callerNumber, 'dtmfDigits.?) { (sessionId, isActive, callerNumber, dtmfDigits) ⇒
            val ref = getOrCreateActor(sessionId)
            val msg = ThirdStageDigits(sessionId, isActive, callerNumber, dtmfDigits)
            serviceAndComplete(msg, ref)
          }
        }
      }
    } ~
    path("redirect") {
      logRequestResult("redirect", Logging.DebugLevel) {
        extractRequest { _: HttpRequest ⇒
          formFields('phoneNumber) { phoneNumber ⇒
            println("calling now")
            Future(TestCalling.call(phoneNumber))
            complete(OK, HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Good"))
          }
        }
      }
    } ~
    path("event") {
      logRequestResult("event", Logging.DebugLevel) {
        extractRequest { _: HttpRequest ⇒
          formFields('amount, 'sessionId, 'direction, 'destinationNumber,
            'status, 'durationInSeconds, 'callSessionState, 'callerNumber) {
            (amount, sessionId, direction, destinationNumber, status, durationInSeconds, callSessionState, callerNumber) ⇒
            if (direction == "Inbound") {
              send {
                HttpRequest(
                  method = HttpMethods.POST,
                  uri = Configs.redirect,
                  entity = FormData(Map(
                    "phoneNumber" -> callerNumber
                  )).toEntity
                )
              }
            }
            else {
              val sessionHops = VoiceSessionHops(
                amount,
                sessionId,
                direction,
                destinationNumber,
                status,
                durationInSeconds,
                callSessionState,
                callerNumber
              )
              databaseService.run(DBService.saveVoiceSession(sessionHops))
                .onComplete(_ ⇒ logger.info("Save voice session to database."))
            }
            complete(OK, HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Good"))
          }
        }
      }
    } ~
    pathEndOrSingleSlash {
      complete(OK, HttpEntity(ContentTypes.`text/plain(UTF-8)`, home))
    }

  private def send(req: HttpRequest): Future[ClientResponse] = sendHttpRequest(req)
}
