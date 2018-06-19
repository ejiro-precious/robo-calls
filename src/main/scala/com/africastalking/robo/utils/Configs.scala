package com.africastalking.robo
package utils

import com.africastalking.robo.utils.Enum.Environment
import com.typesafe.config.ConfigFactory

object Configs extends ConfigT {
  protected def getEnvironmentImpl = config.getString("environment")
}

private[utils] trait ConfigT {

  val config = ConfigFactory.load
  config.checkValid(ConfigFactory.defaultReference)

  protected def getEnvironmentImpl: String

  protected lazy val environment = getEnvironmentImpl

  def getEnvironment: Environment.Value = environment match {
    case "dev" ⇒ Environment.Development
    case "prod" ⇒ Environment.Production
    case x ⇒ throw new Exception("Unexpected environment value: " + x)
  }

  val htttpInterface = config.getString(s"robo.web.$environment.http-interface")
  val httpPort = config.getInt(s"robo.web.$environment.http-port")

  val zeroStage = config.getString(s"robo.callback.$environment.zero-stage")
  val fistStage = config.getString(s"robo.callback.$environment.first-stage")
  val secondStage = config.getString(s"robo.callback.$environment.second-stage")
  val thirdStage = config.getString(s"robo.callback.$environment.third-stage")

  val redirect = config.getString(s"robo.callback.$environment.redirect")

  val apiKey = config.getString(s"robo.at.$environment.api-key")
  val username = config.getString(s"robo.at.$environment.username")
  val from = config.getString(s"robo.at.$environment.from")

}