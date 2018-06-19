package com.africastalking.robo
package free

import cats.data.EitherT
import cats.free.Free

import com.africastalking.robo.domain.{ Error, VoiceSessionHops, VoiceSessionT }

/** algebra and free defined here **/
object DBService {

  /** GADT DSL for saving into database **/
  sealed trait Action[R]
  /** It returns VoiceSessionT after saving into the database, if it fails we return Error **/
  /** We can add more DSL to our program form here **/
  case class SaveVoiceSession(req: VoiceSessionHops) extends Action[Either[Error, VoiceSessionT]]

  type Program[A]   = Free[Action, A]
  /** for validation we are using cat.data.EitherT **/
  type ProgramEx[A] = EitherT[Program, Error, A]

  private def execute[A](action: Action[A]): Program[A]  = Free.liftF(action)
  private def returns[A](value: A): Program[A]                 = Free.pure(value)
  private def fail[A](error: Error): Program[Either[Error, A]] = returns(Left(error))

  def saveVoiceSession(user: VoiceSessionHops): Program[Either[Error, VoiceSessionT]] = execute(SaveVoiceSession(user))
}