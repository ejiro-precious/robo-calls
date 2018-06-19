package com.africastalking.robo
package persistence

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success }

import slick.dbio.DBIO

import com.africastalking.robo.domain.{ VoiceSessionHops, VoiceSessionT }
import com.africastalking.robo.free.DBService.Action
import com.africastalking.robo.free.DBService._
import com.africastalking.robo.free.DBService
import com.africastalking.robo.repository.{ VoiceSessionRepository, VoiceSessionRow }
import com.africastalking.robo.domain.Error


/** This is our main MSQL interpreter for our domain, class name suppose to be MSQLDatabaseService **/
/** Here we are transforming (DBService.Action ~> DBIO) so DBIO must have a Monad **/
abstract class DatabaseService (voiceSessionRepository: VoiceSessionRepository)
         (implicit executionContext: ExecutionContext) extends FreeService[DBService.Action, DBIO] {

  def saveVoiceSession(session: VoiceSessionHops): DBIO[Either[Error, VoiceSessionT]] = {
    val voiceSessionRow = VoiceSessionRow.fromVoiceSessionRequest(session)

    val insertAction = for {
      id          ← voiceSessionRepository.save(voiceSessionRow)
      voiceSessionRow ← voiceSessionRepository.findExistingSession(id)
    } yield voiceSessionRow

    insertAction.asTry.flatMap {
      case Success(right)           ⇒ DBIO.successful(Right(right))
      case Failure(other)           ⇒ DBIO.failed(other)
    }
  }
}

object DatabaseService {
  /** This is where we create an instance of the interpreter for the free application **/
  def apply(voiceSessionRepository: VoiceSessionRepository)(implicit executionContext: ExecutionContext): DatabaseService =
    new DatabaseService(voiceSessionRepository) {
      override def apply[A](fa: Action[A]): DBIO[A] = fa match {
        case SaveVoiceSession(session)              ⇒ saveVoiceSession(session)
      }
    }
}
