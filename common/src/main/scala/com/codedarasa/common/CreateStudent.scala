package com.codedarasa.common

import play.api.libs.json.{Format, Json}


case class CreateStudent(
  firstName: String,
  lastName: String
)

object CreateStudent {
  implicit val format: Format[CreateStudent] = Json.format[CreateStudent]
}
