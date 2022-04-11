package btc.wallet.services

import btc.wallet.models.Error
import btc.wallet.models.enums.ErrorCode
import btc.wallet.models.responses.{HistoryResponder, RecordResponder}
import btc.wallet.repositories.RecordRepository
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.services.interfaces.IRecordService
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IPostgresWrapper}

import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat
import scala.concurrent.{ExecutionContextExecutor, Future}

class RecordService(implicit val executionContext: ExecutionContextExecutor,
                    implicit val configurationWrapper: IConfigurationWrapper,
                    implicit val postgresWrapper: IPostgresWrapper) extends IRecordService {

  override def saveRecord(dateTime: String, amount: Int): Future[RecordResponder] = {
    try{
      val errors: List[Error] = List.empty
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSSX")
      println(dateFormat.format(dateFormat.parse(dateTime).getTime))
      val ts: Timestamp = new Timestamp(dateFormat.parse(dateTime).getTime)
      println(ts)
      recordRepository.saveRecord(ts, amount)
        .map(value =>{
          if(value._1) RecordResponder("success", Some(errors))
          else RecordResponder("failed", Some(List(Error(Some(ErrorCode.E_0103), Some(value._2)))))
        })
        .recover {
          case ex =>
            RecordResponder("failed", Some(List(Error(Some(ErrorCode.E_0102), Some(ex.toString)))))
        }
    }
    catch {
      case ex: Exception => Future {
        RecordResponder("failed", Some(List(Error(Some(ErrorCode.E_0102), Some(ex.toString)))))
      }
    }

  }

  override def getHistory(startDate: String, endDate: String): Future[HistoryResponder] = ???

  override def recordRepository: IRecordRepository = {
    new RecordRepository
  }
}
