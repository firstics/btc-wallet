package btc.wallet.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import btc.wallet.models.requests.{HistoryRequester, RecordRequester}
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.services.interfaces.IRecordService
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IPostgresWrapper, JsonSupport}

import scala.util.{Failure, Success}

class RecordController(implicit val configurationWrapper: IConfigurationWrapper,
                       implicit val postgresWrapper: IPostgresWrapper,
                       implicit val recordService: IRecordService) extends Directives with JsonSupport {

  def saveRecord(recordRequest: RecordRequester): Route = {
    onComplete(recordService.saveRecord(recordRequest.datetime, recordRequest.amount)) {
      case Success(value) => {
        val code = if (value.results != null) {
          StatusCodes.OK
        }
        else {
          if (value.errors.nonEmpty) {
            StatusCodes.BadRequest
          } else {
            StatusCodes.InternalServerError
          }
        }
        complete {
          HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, write(value)), status = code)
        }
      }
      case Failure(ex) => {
        ex.printStackTrace()
        throw ex
      }
    }
  }

  def getHistory(historyRequest: HistoryRequester): Route = {
    onComplete(recordService.getHistory(historyRequest.startDate, historyRequest.endDate)) {
      case Success(value) => {
        val code = if (value.results != null) {
          StatusCodes.OK
        }
        else {
          if (value.errors.nonEmpty) {
            StatusCodes.BadRequest
          } else {
            StatusCodes.InternalServerError
          }
        }
        complete {
          HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, write(value)), status = code)
        }
      }
      case Failure(ex) => {
        ex.printStackTrace()
        throw ex
      }
    }
  }
}
