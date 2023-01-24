package com.codedarasa.common

import play.api.libs.json.{Format, Json}

case class ClassRoomDetails(
  className: String,
  isInSession: Boolean,
  numberOfStudents: Int
)

object ClassRoomDetails {
  implicit val format: Format[ClassRoomDetails] = Json.format[ClassRoomDetails]
}
