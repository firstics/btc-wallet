package btc.wallet.test.services

import btc.wallet.models.{DisplayTransaction, Transaction}
import btc.wallet.repositories.RecordRepository
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.services.RecordService
import btc.wallet.services.interfaces.IRecordService
import btc.wallet.test.InitializeSpec
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS

import java.sql.Timestamp
import java.text.SimpleDateFormat
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

class RecordServiceSpec extends InitializeSpec {

  test("save record succeed") {
    val mockDateTime: String = "2019-11-05T17:52:05"
    val mockAmount: Float = 1000
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val currentTs: Timestamp = new Timestamp(dateFormat.parse(mockDateTime).getTime)
    currentTs.setMinutes(0)
    currentTs.setSeconds(0)
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.getLatestRecord).thenReturn(null)
    when(mockRepo.saveRecord(currentTs, mockAmount)).thenReturn((true, ""))
    val futureResult = Await.result(service.saveRecord(mockDateTime, mockAmount), 5000 millis)
    assert(futureResult.result)
    assert(futureResult.errors.get.message.get.isEmpty)
  }

  test("save record with same hour") {
    val mockDateTime1: String = "2019-11-05T17:00:00"
    val mockDateTime2: String = "2019-11-05T17:53:05"
    val mockPrevAmount: Float = 100
    val mockAmount: Float = 1000
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val ts1: Timestamp = new Timestamp(dateFormat.parse(mockDateTime1).getTime)
    val ts2: Timestamp = new Timestamp(dateFormat.parse(mockDateTime2).getTime)
    ts2.setMinutes(0)
    ts2.setSeconds(0)
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.getLatestRecord).thenReturn(Transaction(ts1, mockPrevAmount))
    when(mockRepo.updateRecord(ts2, mockPrevAmount + mockAmount)).thenReturn((true, ""))
    val futureResult = Await.result(service.saveRecord(mockDateTime2, mockAmount), 5000 millis)
    assert(futureResult.result)
    assert(futureResult.errors.get.message.get.isEmpty)
  }

  test("save record with timezone") {
    val prevDateTime: String = "2019-11-05T16:00:00+00:00"
    val mockDateTime: String = "2019-11-05T17:52:05+01:00"
    val mockDateTimeZone: String = "2019-11-05T18:00:00+00:00"
    val mockPrevAmount: Float = 100
    val mockAmount: Float = 1000
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val tsTimeZone: Timestamp = new Timestamp(dateFormat.parse(mockDateTimeZone).getTime)
    val currentTs: Timestamp = new Timestamp(dateFormat.parse(mockDateTime).getTime)
    currentTs.setMinutes(0)
    currentTs.setSeconds(0)
    val prevTs: Timestamp = new Timestamp(dateFormat.parse(prevDateTime).getTime)
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.getLatestRecord).thenReturn(Transaction(prevTs, mockPrevAmount))
    when(mockRepo.saveRecord(tsTimeZone, mockPrevAmount + mockAmount)).thenReturn((true, ""))
    val futureResult = Await.result(service.saveRecord(mockDateTime, mockAmount), 5000 millis)
    assert(futureResult.result)
    assert(futureResult.errors.get.message.get.isEmpty)
  }

  test("Failed when save past transaction") {
    val prevDateTime: String = "2019-11-05T18:00:00+00:00"
    val mockDateTime: String = "2019-11-05T16:52:05+01:00"
    val mockAmount: Float = 1000
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val currentTs: Timestamp = new Timestamp(dateFormat.parse(mockDateTime).getTime)
    val prevTs: Timestamp = new Timestamp(dateFormat.parse(prevDateTime).getTime)
    currentTs.setMinutes(0)
    currentTs.setSeconds(0)

    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.getLatestRecord).thenReturn(Transaction(prevTs, 100))
    when(mockRepo.saveRecord(currentTs, mockAmount)).thenReturn((true, ""))
    val futureResult = Await.result(service.saveRecord(mockDateTime, mockAmount), 5000 millis)
    assert(!futureResult.result)
    assert(futureResult.errors.get.message.get == "Invalid date time")
  }


  test("Failed when got invalid format of timestamp") {
    val mockDateTime: String = "2019-11-05T17:52:05+01:00+01:00"
    val mockAmount: Float = 1000
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val currentTs: Timestamp = new Timestamp(dateFormat.parse(mockDateTime).getTime)
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    val futureResult = Await.result(service.saveRecord(mockDateTime, mockAmount), 5000 millis)
    assert(!futureResult.result)
    assert(futureResult.errors.get.message.get == "Date time is in wrong format")
  }

  test("Save record throw exception") {
    val mockDateTime: String = "2019-11-05T17:52:05+01:00+01:00"
    val mockAmount: Float = 1000
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val currentTs: Timestamp = new Timestamp(dateFormat.parse(mockDateTime).getTime)
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.saveRecord(currentTs, mockAmount)).thenThrow(new RuntimeException)
    val futureResult = Await.result(service.saveRecord(mockDateTime, mockAmount), 5000 millis)
    assert(!futureResult.result)
    assert(futureResult.errors.get.message.get.nonEmpty)
  }

  test("Get history and found records") {
    val mockStartDateTime: String = "2019-11-05T17:00:00"
    val mockEndDateTime: String = "2019-11-05T18:00:00"
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val startTs: Timestamp = new Timestamp(dateFormat.parse(mockStartDateTime).getTime)
    val endTs: Timestamp = new Timestamp(dateFormat.parse(mockEndDateTime).getTime)
    val mockTx: List[DisplayTransaction] = List(DisplayTransaction("2019-11-05T17:00:00+00:00", 100),
      DisplayTransaction("2019-11-05T18:00:00+00:00", 1100))
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.getRecords(startTs, endTs)).thenReturn((mockTx, ""))
    val futureResult = Await.result(service.getHistory(mockStartDateTime, mockEndDateTime), 5000 millis)
    assert(futureResult.results.size == 2)
    assert(futureResult.errors.get.message.get.isEmpty)
  }

  test("Get history: Got errors from repository") {
    val mockStartDateTime: String = "2019-11-05T17:00:00"
    val mockEndDateTime: String = "2019-11-05T18:00:00"
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val startTs: Timestamp = new Timestamp(dateFormat.parse(mockStartDateTime).getTime)
    val endTs: Timestamp = new Timestamp(dateFormat.parse(mockEndDateTime).getTime)
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.getRecords(startTs, endTs)).thenReturn((List.empty, "Some errors"))
    val futureResult = Await.result(service.getHistory(mockStartDateTime, mockEndDateTime), 5000 millis)
    assert(futureResult.results == null)
    assert(futureResult.errors.get.message.get.nonEmpty)
  }

  test("Get history throw exception") {
    val mockStartDateTime: String = "2019-11-05T17:00:00"
    val mockEndDateTime: String = "2019-11-05T18:00:00"
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val startTs: Timestamp = new Timestamp(dateFormat.parse(mockStartDateTime).getTime)
    val endTs: Timestamp = new Timestamp(dateFormat.parse(mockEndDateTime).getTime)
    val mockRepo: IRecordRepository = Mockito.mock(classOf[RecordRepository], RETURNS_MOCKS)
    val service: IRecordService = new RecordService() {
      override def recordRepository: IRecordRepository = mockRepo
    }
    when(mockRepo.getRecords(startTs, endTs)).thenThrow(new RuntimeException)
    val futureResult = Await.result(service.getHistory(mockStartDateTime, mockEndDateTime), 5000 millis)
    assert(futureResult.results == null)
    assert(futureResult.errors.get.message.get.nonEmpty)
  }

}
