package com.africasTalking.doppler.core
package util

import org.apache.commons.daemon.{Daemon, DaemonContext}

/** This trait gives an application a lifecycle **/
trait ApplicationLifecycle {
  def start(): Unit
  def stop(): Unit
}

/** This trait makes an application run as a Daemon in the background*/
abstract class AbstractApplicationDaemon extends Daemon {
  def application: ApplicationLifecycle

  def init(daemonContext: DaemonContext) {}

  def start() = application.start()

  def stop() = application.stop()

  def destroy() = application.stop()
}

/** This is the main AT application daemon that run, cleanup application when stopped**/
trait ATApplicationT[T <: AbstractApplicationDaemon] {
  
  val application = createApplication()
  def createApplication() : T

  private[this] var cleanupAlreadyRun: Boolean = false

  def cleanup(){
    val previouslyRun = cleanupAlreadyRun
    cleanupAlreadyRun = true
    if (!previouslyRun) application.stop()
  }

  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
    def run() {
      cleanup()
    }
  }))

  application.start()
}


