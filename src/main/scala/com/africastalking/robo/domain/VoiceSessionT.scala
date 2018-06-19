package com.africastalking.robo
package domain

trait VoiceSessionT {
  def amount: String
  def sessionId: String
  def direction: String
  def destinationNumber: String
  def status: String
  def durationInSeconds: String
  def callSessionState: String
  def callerNumber: String
}
