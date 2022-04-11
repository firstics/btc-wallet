import sbt.Keys._

lazy val akkaHttpVersion = "10.2.4"

val projectNamespace = "btc.wallet"
val projectName = "btc-wallet"
val projectVersion = "0.1.0"

lazy val commonSettings = Seq(
  organization := projectNamespace,
  scalaVersion := "2.13.8",
  version := projectVersion
)

parallelExecution in Test := true



lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    commonSettings,
    name := projectName,
    libraryDependencies ++= Seq(
      "com.typesafe.akka"             %%    "akka-http"                       %   akkaHttpVersion               ,
      "com.typesafe.akka"             %%    "akka-http-spray-json"            %   akkaHttpVersion               ,
      "com.typesafe.akka"             %%    "akka-stream"                     %   "2.6.4"                       ,
      "com.typesafe.akka"             %%    "akka-slf4j"                      %   "2.6.4"                       ,
      "ch.qos.logback"                %     "logback-classic"                 %   "1.1.3"                       ,
      "com.google.code.gson"          %     "gson"                            %   "2.8.6"                       ,
      "org.json4s"                    %%    "json4s-native"                   %   "3.7.0-M8"                    ,
      "org.json4s"                    %%    "json4s-ext"                      %   "3.6.9"                       ,
      "org.postgresql"                %     "postgresql"                      %   "42.2.5"
    )
  )
checksums in update := Nil