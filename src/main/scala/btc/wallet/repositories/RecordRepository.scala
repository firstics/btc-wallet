package btc.wallet.repositories

import btc.wallet.models.Transaction
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IDatabaseWrapper}

import java.sql.Timestamp
import scala.concurrent.Future

class RecordRepository(implicit val configurationWrapper: IConfigurationWrapper,
                       implicit val postgresWrapper: IDatabaseWrapper) extends IRecordRepository {
  override def saveRecord(date: Timestamp, amount: Int): Future[Boolean] = {
    val result = postgresWrapper.write(date, amount)
    result
  }

  override def getRecord(startDate: Timestamp, endDate: Timestamp): Future[List[Transaction]] = ???
}
