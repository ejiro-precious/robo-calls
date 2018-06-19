package com.africastalking.robo
package persistence

import cats.data.Validated.{Invalid, Valid}
import com.africastalking.robo.utils.Configs
import .database.PersistenceConnectionInfo

case class DatabaseConfiguration(
    default: DatabaseDefaultConfiguration,
    hikari: DatabaseHikariConfiguration)


object DatabaseConfiguration {
  def apply(config: Configs, parentPrefix: String): DatabaseConfiguration = {
    val prefix = s"$parentPrefix.db"

    DatabaseConfiguration(
      DatabaseDefaultConfiguration(config, prefix),
      DatabaseHikariConfiguration(config, prefix)
    )
  }
}

case class DatabaseDefaultConfiguration(
     driver: String,
     url: String,
     user: String,
     password: String)


object DatabaseDefaultConfiguration {
  def apply(config: Configs, parentPrefix: String): DatabaseDefaultConfiguration = {
    val prefix = s"$parentPrefix.default"

    ParseUtils.database.parseConnectionString(config.getString(s"$prefix.url")) match {
      case Valid(PersistenceConnectionInfo(user, password, url)) ⇒
        DatabaseDefaultConfiguration(
          driver   = config.getString(s"$prefix.driver"),
          url      = s"${config.getString(s"$prefix.urlPrefix")}$url",
          user     = user,
          password = password
        )
      case Invalid(errors) ⇒
        throw new RuntimeException(s"Database configuration not valid:\n${errors.toList.mkString("\n")}")
    }
  }
}

case class DatabaseHikariConfiguration(
     maximumPoolSize: Int,
     maxLifetime: Int)

object DatabaseHikariConfiguration {
  def apply(config: Configs, parentPrefix: String): DatabaseHikariConfiguration = {
    val prefix = s"$parentPrefix.hikari"

    DatabaseHikariConfiguration(
      config.getInt(s"$prefix.maximumPoolSize"),
      config.getInt(s"$prefix.maxLifetime")
    )
  }
}
