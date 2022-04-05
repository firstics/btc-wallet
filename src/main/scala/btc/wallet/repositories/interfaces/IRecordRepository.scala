package btc.wallet.repositories.interfaces

import btc.wallet.models.Transaction

import java.sql.Timestamp
import scala.concurrent.Future

trait IRecordRepository {
  def saveRecord(date: Timestamp, amount: Int): Future[Boolean]
  def getRecord(startDate: Timestamp, endDate: Timestamp): Future[List[Transaction]]
}
