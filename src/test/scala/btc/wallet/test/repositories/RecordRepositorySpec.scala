package btc.wallet.test.repositories

import btc.wallet.models.{DisplayTransaction, Transaction}
import btc.wallet.repositories.RecordRepository
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.test.InitializeSpec

import java.sql.{PreparedStatement, ResultSet, Timestamp}
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS


class RecordRepositorySpec extends InitializeSpec {

  val mockP: PreparedStatement = Mockito.mock(classOf[PreparedStatement], RETURNS_MOCKS)
  val mockRs: ResultSet = Mockito.mock(classOf[ResultSet], RETURNS_MOCKS)

  test("Save record success") {
    val dateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:52:05")
    val amount: Float = 1000
    val query: String = s"INSERT INTO record (amount, date_time) VALUES(?, ?) RETURNING id"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: (Boolean, String) = recordRepository.saveRecord(dateTime, amount)
    assert(result._1)
    assert(result._2.isEmpty)
  }

  test("Save record failed") {
    val dateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:52:05")
    val amount: Float = 1000
    val query: String = s"INSERT INTO record (amount, date_time) VALUES(?, ?) RETURNING id"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "Some error"))
    when(mockRs.next()).thenReturn(false)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: (Boolean, String) = recordRepository.saveRecord(dateTime, amount)
    assert(!result._1)
    assert(result._2.nonEmpty)
  }

  test("Update record success") {
    val dateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:52:05")
    val amount: Float = 1000
    val query: String = s"UPDATE record SET amount = ? WHERE date_time = ? RETURNING id"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: (Boolean, String) = recordRepository.updateRecord(dateTime, amount)
    assert(result._1)
    assert(result._2.isEmpty)
  }

  test("Update record failed") {
    val dateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:52:05")
    val amount: Float = 1000
    val query: String = s"UPDATE record SET amount = ? WHERE date_time = ? RETURNING id"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "Some error"))
    when(mockRs.next()).thenReturn(false)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: (Boolean, String) = recordRepository.updateRecord(dateTime, amount)
    assert(!result._1)
    assert(result._2.nonEmpty)
  }

  test("Get latest record success") {
    val dateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:00:00")
    val amount: Float = 1000
    val query: String = s"SELECT amount, date_time from record ORDER BY date_time DESC LIMIT 1"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getTimestamp("date_time")).thenReturn(dateTime)
    when(mockRs.getFloat("amount")).thenReturn(amount)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: Transaction = recordRepository.getLatestRecord
    assert(result.dateTime == dateTime)
    assert(result.amount == amount)
  }

  test("Get latest record not found") {
    val query: String = s"SELECT amount, date_time from record ORDER BY date_time DESC LIMIT 1"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(false)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: Transaction = recordRepository.getLatestRecord
    assert(result == null)
  }

  test("Get record success") {
    val dateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:00:00")
    val amount: Float = 1000
    val query: String = s"SELECT amount, date_time from record WHERE date_time = ?"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getTimestamp("date_time")).thenReturn(dateTime)
    when(mockRs.getFloat("amount")).thenReturn(amount)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: Transaction = recordRepository.getRecord(dateTime)
    assert(result.dateTime == dateTime)
    assert(result.amount == amount)
  }

  test("Get record not found") {
    val dateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:00:00")
    val query: String = s"SELECT amount, date_time from record WHERE date_time = ?"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(false)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: Transaction = recordRepository.getRecord(dateTime)
    assert(result == null)
  }

  test("Get records history success") {
    val startDateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:00:00")
    val endDateTime: Timestamp = Timestamp.valueOf("2019-11-05 18:00:00")
    val amount: Float = 1000
    val query: String = s"SELECT amount, date_time from record WHERE date_time BETWEEN '$startDateTime' and '$endDateTime'"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true).andThen(false)
    when(mockRs.getTimestamp("date_time")).thenReturn(startDateTime)
    when(mockRs.getFloat("amount")).thenReturn(amount)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: (List[DisplayTransaction], String) = recordRepository.getRecords(startDateTime, endDateTime)
    assert(result._1.size == 1)
    assert(result._2.isEmpty)
  }

  test("Get records history not found") {
    val startDateTime: Timestamp = Timestamp.valueOf("2019-11-05 17:00:00")
    val endDateTime: Timestamp = Timestamp.valueOf("2019-11-05 18:00:00")
    val amount: Float = 1000
    val query: String = s"SELECT amount, date_time from record WHERE date_time BETWEEN '$startDateTime' and '$endDateTime'"
    when(configurationWrapper.getDBConfig("recordTable")).thenReturn("record")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(false)
    val recordRepository:IRecordRepository = new RecordRepository()
    val result: (List[DisplayTransaction], String) = recordRepository.getRecords(startDateTime, endDateTime)
    assert(result._1.isEmpty)
    assert(result._2.isEmpty)
  }

}
