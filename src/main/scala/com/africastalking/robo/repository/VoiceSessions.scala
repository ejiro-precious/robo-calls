package com.africastalking.robo
package repository

import slick.lifted.ProvenShape

import com.africastalking.robo.persistence.MysqlDriver.api._

/** This class represent the VOICE_SESSION table in the database **/
class VoiceSessions(tag: Tag) extends Table[VoiceSessionRow](tag, "VOICE_SESSIONS") {

  def id: Rep[VoiceSessionId]         = column[VoiceSessionId]("ID", O.PrimaryKey, O.AutoInc)
  def amount: Rep[String]             = column[String]("AMOUNT", O.Length(512))
  def sessionId: Rep[String]          = column[String]("SESSION_ID", O.Length(512))
  def direction: Rep[String]          = column[String]("DIRECTION", O.Length(512))
  def destinationNumber: Rep[String]  = column[String]("DESTINATION_NUMBER", O.Length(512))
  def status: Rep[String]             = column[String]("CALL_STATUS", O.Length(512))
  def durationInSeconds: Rep[String]  = column[String]("DURATION_SECONDS", O.Length(512))
  def callSessionState: Rep[String]   = column[String]("CALL_SESSION_STATE", O.Length(512))
  def callerNumber: Rep[String]       = column[String]("CALLER_NUMBER", O.Length(512))

  override def * : ProvenShape[VoiceSessionRow] =
    (id.?, amount, sessionId, direction, destinationNumber,status, durationInSeconds, callSessionState, callerNumber) <>
      (VoiceSessionRow.tupled, VoiceSessionRow.unapply)
}
