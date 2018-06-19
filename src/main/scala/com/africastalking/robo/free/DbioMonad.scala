package com.africastalking.robo
package free

import scala.concurrent.ExecutionContext

import cats.Monad

import slick.dbio.DBIO

trait DbioMonad {
  /** This is a monad instance for DBIO **/
  implicit def DBIOMonad(implicit executionContext: ExecutionContext): Monad[DBIO] = new Monad[DBIO] {
    override def pure[A](x: A): DBIO[A]                                 = DBIO.successful(x)
    override def flatMap[A, B](fa: DBIO[A])(f: (A) ⇒ DBIO[B]): DBIO[B] = fa flatMap f
    override def tailRecM[A, B](a: A)(f: (A) ⇒ DBIO[Either[A, B]]): DBIO[B] = f(a) flatMap {
      case Left(a1) ⇒ tailRecM(a1)(f)
      case Right(b) ⇒ DBIO.successful(b)
    }
  }
}

object DbioMonad extends DbioMonad