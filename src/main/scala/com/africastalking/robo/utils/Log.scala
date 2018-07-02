package com.africasTalking.doppler.core
package util

import org.slf4j.LoggerFactory

/** This trait provides logging to a class that extends it */
trait ATLog {
  def log = LoggerFactory.getLogger(this.getClass)
}

/** This class enables a case class to be able to print print it self **/
trait ATCCPrinter {
  override def toString = ATUtil.getCaseClassString(this)
}
