package btc.wallet

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import btc.wallet.actors.ServerActor
import btc.wallet.models.ServerRequest
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IDatabaseWrapper}
import btc.wallet.wrappers.{ConfigurationWrapper, PostgresWrapper}

import java.util.UUID
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

object Server extends Directives {
  def main(args: Array[String]): Unit = {
    implicit val configurationWrapper: IConfigurationWrapper = new ConfigurationWrapper()
    lazy val appName: String = configurationWrapper.getSettingConfig("name")
    lazy val version: String =  configurationWrapper.getSettingConfig("version")

    implicit val system: ActorSystem = ActorSystem(appName, configurationWrapper.getConfig)


    val systemActor = system.actorOf(Props[ServerActor], appName)
    systemActor ! ServerRequest("corelationId", UUID.randomUUID().toString)

    implicit val executionContextExecutor: ExecutionContextExecutor = system.dispatcher
    implicit val rocksDBWrapper: IDatabaseWrapper = new PostgresWrapper()

    try {
      val serverRoute = new ServerRouting()
      val serverVersionRoute = serverRoute.route
      val routes: Route = serverVersionRoute
      Http().bindAndHandle(routes, "0.0.0.0", Integer.parseInt(configurationWrapper.getSettingConfig("port")))
      Await.result(system.whenTerminated, Duration.Inf)
    }
    catch {
      case e: Exception =>
        println(e.getMessage)
        println(e.getStackTrace.mkString("Array(", ", ", ")"))
        e.printStackTrace()
    }
  }
}
