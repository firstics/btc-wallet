package btc.wallet.services.interfaces

import btc.wallet.models.responses.{HistoryResponse, RecordResponse}

import scala.concurrent.Future

trait IRecordService {
  def saveRecord(dateTime: String, amount: Int): Future[RecordResponse]
  def getHistory(startDate: String, endDate: String): Future[HistoryResponse]
  def validateRecord(dateTime: String, amount: Int): List[Error]
  def validateHistory(startDate: String, endDate: String): List[Error]
}
