package com.codedarasa.common

import play.api.libs.json.{Format, Json}

case class Timestamp (
  seconds: Long,
  nanos: Int
)

object Timestamp {
  implicit val format: Format[Timestamp] = Json.format[Timestamp]
}
