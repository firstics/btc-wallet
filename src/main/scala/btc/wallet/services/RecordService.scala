package btc.wallet.services

import btc.wallet.models.responses.{HistoryResponse, RecordResponse}
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.services.interfaces.IRecordService
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IDatabaseWrapper}

import scala.concurrent.Future

class RecordService(implicit val configurationWrapper: IConfigurationWrapper,
                    implicit val postgresWrapper: IDatabaseWrapper,
                    implicit val recordRepository: IRecordRepository) extends IRecordService {
  override def saveRecord(dateTime: String, amount: Int): Future[RecordResponse] = ???

  override def getHistory(startDate: String, endDate: String): Future[HistoryResponse] = ???

  override def validateRecord(dateTime: String, amount: Int): List[Error] = ???

  override def validateHistory(startDate: String, endDate: String): List[Error] = ???
}
