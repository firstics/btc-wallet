package btc.wallet.models.enums

import btc.wallet.models.enums

object ErrorCode extends Enumeration {
  type ErrorCode = Value

  val E_0101: enums.ErrorCode.Value = Value("E_0101")
  val E_0102: enums.ErrorCode.Value = Value("E_0102")
  val E_0103: enums.ErrorCode.Value = Value("E_0103")
  val E_0104: enums.ErrorCode.Value = Value("E_0104")


  def getDefinition(errorCode: Value): String = {
    errorCode match {
      case E_0101 => "invalid token."
      case E_0102 => "Record service exception."
      case E_0103 => "Record repository exception."
    }
  }
}

