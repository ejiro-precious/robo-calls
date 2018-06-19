package com.africastalking.robo
package repository

import scala.concurrent.ExecutionContext

import slick.dbio.DBIO

import com.africastalking.robo.persistence.MysqlDriver.api._

final class VoiceSessionRepository {

  /** Represents a database table. Profiles add extension methods to TableQuery
    * for operations that can be performed on tables but not on arbitrary
    * queries, e.g. getting the table DDL.
    * we are using the table class VoiceSessions
    */
  val voiceSession: TableQuery[VoiceSessions] = TableQuery[VoiceSessions]

  private val voiceSessionReturningId = voiceSession returning (voiceSession map (_.id))

  private val findQuery = Compiled((id: Rep[VoiceSessionId]) ⇒ voiceSession filter (_.id === id))

  def findExistingSession(id: VoiceSessionId): DBIO[VoiceSessionRow] = findQuery(id).result.head

  /** Here we are returning VoiceSessionId  **/
  def save(row: VoiceSessionRow)(implicit ec: ExecutionContext): DBIO[VoiceSessionId] =
    voiceSessionReturningId.insertOrUpdate(row) map {
      case Some(newId) ⇒ newId
      case None        ⇒ row.id.getOrElse(sys.error(s"ReturningInsertActionComposer updated entity ($row) with no id"))
    }
}
