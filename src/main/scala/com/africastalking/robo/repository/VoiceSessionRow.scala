package com.africastalking.robo
package repository

import com.africastalking.robo.domain.{ VoiceSessionT, VoiceSessionHops }
import com.africastalking.robo.persistence.MysqlDriver.api._

/** Not using this yet **/
case class VoiceSessionId(value: Int) extends AnyVal
object VoiceSessionId {
  /** This is a Type Mapper for scala class with parameter i.e the VoiceSessionId case class **/
  implicit val mapping: BaseColumnType[VoiceSessionId] = MappedColumnType.base(_.value, VoiceSessionId.apply)
}

case class VoiceSessionRow(
     id: Option[VoiceSessionId] = None,
     amount : String,
     sessionId: String,
     direction: String,
     destinationNumber: String,
     status: String,
     durationInSeconds: String,
     callSessionState: String,
     callerNumber: String) extends VoiceSessionT {

  def update(voiceSession: VoiceSessionHops): VoiceSessionRow = this.copy(
    ??? // Do some updtaing on the existing data
  )
}

object VoiceSessionRow {

  def fromVoiceSessionRequest(voiceSession: VoiceSessionHops): VoiceSessionRow = VoiceSessionRow(
    amount = voiceSession.amount,
    sessionId = voiceSession.sessionId,
    direction = voiceSession.direction,
    destinationNumber = voiceSession.destinationNumber,
    status = voiceSession.status,
    durationInSeconds = voiceSession.durationInSeconds,
    callSessionState = voiceSession.callSessionState,
    callerNumber = voiceSession.callerNumber
  )

  val tupled = (apply _).tupled
}
