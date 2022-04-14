package btc.wallet.repositories.interfaces

import btc.wallet.models.{DisplayTransaction, Transaction}

import java.sql.Timestamp

trait IRecordRepository {
  def saveRecord(date: Timestamp, amount: Float): (Boolean, String)
  def updateRecord(date: Timestamp, amount: Float): (Boolean, String)
  def getLatestRecord: Transaction
  def getRecord(date: Timestamp): Transaction
  def getRecords(startDate: Timestamp, endDate: Timestamp): (List[DisplayTransaction], String)
}
