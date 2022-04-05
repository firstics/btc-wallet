package btc.wallet.wrappers.interfaces

import btc.wallet.models.enums.ErrorCode
import org.json4s.ext.EnumNameSerializer
import org.json4s.native.JsonMethods.{parse => jParser}
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write => jWrite}
import org.json4s.{DefaultFormats, _}

trait JsonSupport extends {

  val EMPTY_STRING = ""

  implicit val serialization: Serialization.type = Serialization

  implicit val formats: Formats = DefaultFormats +
    new EnumNameSerializer(ErrorCode)

  def write[T <: AnyRef](value: T): String = jWrite(value)

  protected def parse(value: String): JValue = jParser(value)

  implicit protected def extractOrEmptyString(json: JValue): String = {
    json match {
      case JNothing => EMPTY_STRING
      case data => data.extract[String]
    }
  }

}
