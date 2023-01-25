package com.codedarasa.common

import play.api.libs.json.{Format, Json}

case class StudentDetails (
  studentName: String,
  isInClass: Boolean
)

object StudentDetails {
  implicit val format: Format[StudentDetails] = Json.format[StudentDetails]
}
