package btc.wallet.services

import btc.wallet.models.{Error, Transaction}
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

  override def saveRecord(dateTime: String, amount: Float): Future[RecordResponder] = Future {
    try{
      var totalAmount: Float = amount
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      println(dateFormat.format(dateFormat.parse(dateTime).getTime))
      val currentTs: Timestamp = new Timestamp(dateFormat.parse(dateTime).getTime)
      val dateSplit: Array[String] = dateTime.split('+')
      if(dateSplit.length <= 2) {
        if(dateSplit.length == 2) {
          val timezone: Array[String] = dateSplit(1).split(':')
          val hour: Int = currentTs.getHours + timezone(0).toInt
          val minutes: Int = currentTs.getMinutes + timezone(1).toInt
          currentTs.setHours(hour)
          currentTs.setMinutes(minutes)
        }
        currentTs.setMinutes(0)
        currentTs.setSeconds(0)
        val prevTx: Transaction = recordRepository.getLatestRecord
        if(prevTx != null) {
          totalAmount = prevTx.amount + amount
          if(prevTx.dateTime.compareTo(currentTs) == 0) {
            val result: (Boolean, String) = recordRepository.updateRecord(currentTs, totalAmount)
            RecordResponder(result._1, Some(Error(Some(result._2))))
          }
          else if(prevTx.dateTime.compareTo(currentTs) > 0) {
            RecordResponder(false, Some(Error(Some("Invalid date time"))))
          }
          else {
            val result: (Boolean, String) = recordRepository.saveRecord(currentTs, totalAmount)
            RecordResponder(result._1, Some(Error(Some(result._2))))
          }
        }
        else {
          val result: (Boolean, String) = recordRepository.saveRecord(currentTs, totalAmount)
          RecordResponder(result._1, Some(Error(Some(result._2))))
        }
      }
      else {
        RecordResponder(false, Some(Error(Some("Date time is in wrong format"))))
      }
    }
    catch {
      case ex: Exception =>  {
        RecordResponder(false, Some(Error(Some(ex.toString))))
      }
    }

  }

  override def getHistory(startDate: String, endDate: String): Future[HistoryResponder] = Future {
    try {
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
      val startTs: Timestamp = new Timestamp(dateFormat.parse(startDate).getTime)
      val endTs: Timestamp = new Timestamp(dateFormat.parse(endDate).getTime)
      println(s"start: $startTs end: $endTs")
      val result: (List[Transaction], String) = recordRepository.getRecords(startTs, endTs)
      if(result._2.isEmpty) {
        HistoryResponder(result._1, Some(Error(Some(result._2))))
      }
      else {
        HistoryResponder(null, Some(Error(Some(result._2))))
      }
    }
    catch {
      case ex: Exception =>  {
        HistoryResponder(null, Some(Error(Some(ex.toString))))
      }
    }
  }

  override def recordRepository: IRecordRepository = {
    new RecordRepository
  }
}
