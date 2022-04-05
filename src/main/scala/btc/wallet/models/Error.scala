package btc.wallet.models

import btc.wallet.models.enums.ErrorCode.ErrorCode

final case class Error
(
  errorCode: Option[ErrorCode],
  message: Option[String]
)
