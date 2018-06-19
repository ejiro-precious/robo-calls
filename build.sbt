name := "robo-callback"

version := "0.1"

scalaVersion := "2.11.8"

val scalaTestV  = "3.0.1"
val akkaVersion = "2.4.17"
val catsV         = "0.9.0"
val h2V           = "1.4.192"
val slickV        = "3.1.1"

libraryDependencies ++= {
  val akkaHttpV = "10.0.10"
  //val akkaV = "2.4.8"
  Seq(
    "com.typesafe.akka"    %%  "akka-actor"             % akkaVersion,
    "com.typesafe.akka"    %%  "akka-slf4j"             % akkaVersion,
    "com.typesafe.akka"    %% "akka-http"               % akkaHttpV,
    "io.spray"             % "spray-json_2.12"          % "1.3.4",
    "ch.qos.logback"       % "logback-classic"          % "1.1.3",
    "org.slf4j"            % "slf4j-api"                % "1.7.5",
    "com.typesafe.akka"    %% "akka-http-xml"           % "10.0.9",
    "org.json"             % "json"                     % "20090211",
    "com.github.mauricio"  %  "mysql-async_2.12"       % "0.2.21",

    "org.typelevel"        %% "cats"                    % catsV,
    "com.typesafe.slick"   %% "slick"                   % slickV,
    "com.typesafe.slick"   %% "slick-hikaricp"          % slickV,
    "mysql"                % "mysql-connector-java"     % "5.1.6",

    "org.scalaz"           %% "scalaz-core"             % "7.2.23",
    "org.scalaz"           %% "scalaz-effect"           % "7.2.21",
    "org.scalaz"           %% "scalaz-concurrent"       % "7.2.21",
    "org.typelevel"        %% "cats"                    % "0.9.0",
    "org.tpolecat"         %% "doobie-contrib-hikari"   % "0.3.0" exclude("org.scalaz", "*")
  )
}