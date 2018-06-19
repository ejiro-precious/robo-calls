package com.africastalking.robo
package state

object ServiceProtocol {

  trait Command
  case class VoiceCall(sessionId: String, isActive: Int, callNumber: String) extends Command
  case class ZeroStageDigits(sessionId: String, isActive: Int, callerNumber: String, dtmfdigits: Option[String]) extends Command
  case class FirstStageDigits(sessionId: String, isActive: Int, callerNumber: String, dtmfdigits: Option[String]) extends Command
  case class SecondStageDigits(sessionId: String, isActive: Int, callerNumber: String, dtmfdigits: Option[String]) extends Command
  case class ThirdStageDigits(sessionId: String, isActive: Int, callerNumber: String, dtmfdigits: Option[String]) extends Command

  //case object Terminated extends Command

}
