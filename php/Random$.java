package com.africastalking.robo;

object Random {

  trait DbConfiguration {
    lazy val config = DatabaseConfig.forConfig[JdbcProfile]("db")
  }

  trait Db {
    val config: DatabaseConfig[JdbcProfile]
    val db: JdbcProfile#Backend#Database = config.db
  }


  case class User(id: Option[Int], email: String,
                  firstName: Option[String], lastName: Option[String])

  case class Address(id: Option[Int], userId: Int,
                     addressLine: String, city: String, postalCode: String)

  trait UsersTable { this: Db =>
    import config.driver.api._

    private class Users(tag: Tag) extends Table[User](tag, "USERS") {
      // Columns
      def id = column[Int]("USER_ID", O.PrimaryKey, O.AutoInc)
      def email = column[String]("USER_EMAIL", O.Length(512))
      def firstName = column[Option[String]]("USER_FIRST_NAME", O.Length(64))
      def lastName = column[Option[String]]("USER_LAST_NAME", O.Length(64))

      // Indexes
      def emailIndex = index("USER_EMAIL_IDX", email, true)

      // Select
      def * = (id.?, email, firstName, lastName) <> (User.tupled, User.unapply)
    }

    val users = lifted.TableQuery[Users]
  }


  trait AddressesTable extends UsersTable { this: Db =>
    import config.driver.api._

    private class Addresses(tag: Tag) extends Table[Address](tag, "ADDRESSES") {
      // Columns
      def id = column[Int]("ADDRESS_ID", O.PrimaryKey, O.AutoInc)
      def addressLine = column[String]("ADDRESS_LINE")
      def city = column[String]("CITY")
      def postalCode = column[String]("POSTAL_CODE")

      // ForeignKey
      def userId = column[Int]("USER_ID")
      def userFk = foreignKey("USER_FK", userId, users)
      (_.id, ForeignKeyAction.Restrict, ForeignKeyAction.Cascade)

      // Select
      def * = (id.?, userId, addressLine, city, postalCode) <>
        (Address.tupled, Address.unapply)
    }

    val addresses = lifted.TableQuery[Addresses]
  }

  class UsersRepository(val config: DatabaseConfig[JdbcProfile])
    extends Db with UsersTable {

    import config.driver.api._
    import scala.concurrent.ExecutionContext.Implicits.global

    def insert(user: User) = db.run(users += user)

    def _insert(user: User) = db
      .run(users returning users.map(_.id) += user)
      .map(id => user.copy(id = Some(id)))


    def find(id: Int) =
      db.run((for (user <- users if user.id === id) yield user).result.headOption)
  }




}
