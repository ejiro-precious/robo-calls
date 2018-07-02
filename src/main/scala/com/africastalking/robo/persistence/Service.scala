package com.africastalking.robo
package persistence

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.higherKinds

import cats.free.Free
import cats.{ Monad, ~> }

/**
  * @tparam DSL This is our domain commands GADT
  * @tparam M This represent what we transform our GADT to (DSL ~> M)
  */
trait ServiceT[DSL[_], M[_]] { self ⇒
  final type Program[Result] = Free[DSL, Result]

  def execute[A](program: Program[A])(implicit M: Monad[M]): M[A]

}

object ServiceT {
  def apply[DSL[_], M[_]](f: DSL ~> M): FreeService[DSL, M] = new FreeService[DSL, M] {
    override def apply[A](fa: DSL[A]): M[A] = f(fa)
  }

  /** This is a pimp class from Service[DSL, Future] => Future[A] **/
  implicit class FutureServiceOps[DSL[_]](val self: ServiceT[DSL, Future]) extends AnyVal {
    import cats.instances.future._
    def run[A](action: self.Program[A])(implicit ec: ExecutionContext): Future[A] = self.execute(action)
    def runWithResultHandler[A, U](action: self.Program[A])(handler: PartialFunction[A, U])(implicit ec: ExecutionContext): Future[A] = {
      val fut = run(action)
      fut.onSuccess(handler)
      fut
    }
  }
}

abstract class FreeService[DSL[_], M[_]] extends (DSL ~> M) with ServiceT[DSL, M] { self ⇒
  final val nat: DSL ~> M                                                        = this
  override final def execute[A](program: Program[A])(implicit M: Monad[M]): M[A] = program foldMap this
}
