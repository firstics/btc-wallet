package btc.wallet

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import btc.wallet.controllers.RecordController
import btc.wallet.models.requests.{HistoryRequester, RecordRequester}
import btc.wallet.services.interfaces.IRecordService
import btc.wallet.services.RecordService
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IPostgresWrapper, JsonSupport}

import scala.compat.java8.OptionConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

class ServerRouting(implicit val configurationWrapper: IConfigurationWrapper,
                    implicit val system: ActorSystem,
                    implicit val executionContextExecutor: ExecutionContextExecutor,
                    implicit val postgresWrapper: IPostgresWrapper) extends Directives with JsonSupport{

  implicit val recordService: IRecordService = new RecordService()
  implicit val recordController: RecordController = new RecordController()


  def route: Route = {
    val baseRoute = pathPrefix("services" / "wallet")
    baseRoute {
      concat(
        post(path("record") {
          entity(as[String]) { recordJson => {
            val record: RecordRequester = parse(recordJson).extract[RecordRequester]
            recordController.saveRecord(record)
           }
          }
        }),
        get(pathPrefix("records") {
          entity(as[String]) { recordJson => {
            val record: HistoryRequester = parse(recordJson).extract[HistoryRequester]
            recordController.getHistory(record)
          }
          }
        })
      )
    }
  }
}
