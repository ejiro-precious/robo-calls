package com.africastalking.robo
package domain

/** This is the error we get when we try to input the session hops in the database **/
sealed abstract class Error {
  val errorCode: String = this.getClass.getSimpleName.stripSuffix("$")
}

case object DuplicateFound extends Error