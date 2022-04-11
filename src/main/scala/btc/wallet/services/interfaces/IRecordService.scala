package btc.wallet.services.interfaces

import btc.wallet.models.responses.{HistoryResponder, RecordResponder}
import btc.wallet.repositories.interfaces.IRecordRepository

import scala.concurrent.Future

trait IRecordService {
  def saveRecord(dateTime: String, amount: Int): Future[RecordResponder]
  def getHistory(startDate: String, endDate: String): Future[HistoryResponder]
  def recordRepository: IRecordRepository
}
