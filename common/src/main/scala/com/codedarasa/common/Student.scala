package com.codedarasa.common

import play.api.libs.json.{Format, Json}

import java.util.UUID

case class Student (
  studentUuid: UUID,
  studentName: String,
  isInClass: Boolean,
  checkedInTime: Option[Timestamp],
  isDeleted: Boolean,
  createdAt: Timestamp,
  updatedAt: Option[Timestamp]
)

object Student {
  implicit val format: Format[Student] = Json.format[Student]
}
