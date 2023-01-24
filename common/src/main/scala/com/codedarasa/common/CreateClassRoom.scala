package com.codedarasa.common

import play.api.libs.json.{Format, Json}

case class CreateClassRoom(
  className: String
)

object CreateClassRoom {
  implicit val format: Format[CreateClassRoom] = Json.format[CreateClassRoom]
}
