package btc.wallet.models.responses

import btc.wallet.models.Transaction

final case class HistoryResponse(results: List[Transaction], errors: List[Error])
