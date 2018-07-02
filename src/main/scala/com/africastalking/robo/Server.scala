package com.africasTalking.doppler.web
package service

import com.africasTalking.doppler._

import core.util.{AbstractApplicationDaemon, ATApplicationT}

class ApplicationDaemon extends AbstractApplicationDaemon {
  def application = new Application
}

object ServiceApplication extends App with ATApplicationT[ApplicationDaemon] {
  def createApplication = new ApplicationDaemon
}
