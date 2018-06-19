package com.africastalking.robo
package utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, StatusCode }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

case class ClientResponse(
  status: StatusCode,
  data: String
)

trait HttpClient {

  implicit val system: ActorSystem
  final implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
  private lazy val http                = Http(system)

  def sendHttpRequest(req: HttpRequest): Future[ClientResponse] = for {
    response <- http.singleRequest(req)
    data     <- Unmarshal(response.entity).to[String]
  } yield ClientResponse(
    status = response.status,
    data   = data
  )
}
