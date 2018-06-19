package com.africastalking.robo
package state

object ServiceMessage {

  sealed trait CallState
  case object WaitingForRequest extends CallState
  case object HandlingStageZeroDigits extends CallState
  case object HandlingStageOneDigits extends CallState
  case object HandlingStageTwoDigits extends CallState
  case object HandlingStageThreeDigits extends CallState

  case object EndingCall extends CallState

  sealed trait CallData
  case object NoData extends CallData
  case class VoiceData(
        sessionId: String,
        isActive: Int,
        callerNumber: String) extends CallData

  case class StageZeroData(
        voiceData: VoiceData,
        language: Language) extends CallData

  object StageZeroData {
    def apply(voiceData: VoiceData, language: Option[String]): StageZeroData =
      language match {
        case Some("1") ⇒ StageZeroData(voiceData, English)
        case Some("2") ⇒ StageZeroData(voiceData, Hausa)
        case None        ⇒ ???
      }
  }

  case class StageOneData(
        stageZeroData: StageZeroData,
        wantToHear: Want) extends CallData

  object StageOneData {
    def apply(stageZeroData: StageZeroData, wantToHear: Option[String]): StageOneData =
      wantToHear match {
        case Some("1") ⇒ StageOneData(stageZeroData, Prayer)
        case Some("2") ⇒ StageOneData(stageZeroData, Names)
        case Some("3") ⇒ StageOneData(stageZeroData, Food)
        case Some("4") ⇒ StageOneData(stageZeroData, Health)
        case _         ⇒ ???
      }
  }

  case class StageTwoData(
        stageOneData: StageOneData,
        stageTwoOption: Option[String]) extends CallData

  case class StageThreeData(
          stageTwoData: StageTwoData,
          stageThreeOption: Option[String]) extends CallData

  sealed trait Language
  case object English extends Language
  case object Hausa extends Language

  sealed trait Want
  case object Prayer extends Want
  case object Names extends Want
  case object Food extends Want
  case object Health extends Want

}
