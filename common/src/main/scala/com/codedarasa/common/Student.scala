package com.codedarasa.common

import play.api.libs.json.{Format, Json}

import java.util.UUID

case class Student (
  studentUuid: UUID,
  studentName: String,
  isInClass: Boolean = false,
  checkedInTime: Option[Timestamp] = None,
  isDeleted: Boolean = false,
  createdAt: Timestamp,
  updatedAt: Option[Timestamp] = None
)

object Student {
  implicit val format: Format[Student] = Json.format[Student]
}
