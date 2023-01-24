package com.codedarasa.common

import play.api.libs.json.{Format, Json}

import java.util.UUID

case class ClassRoom (
  classUuid: UUID,
  className: String,
  isInSession: Boolean,
  sessionStart: Option[Timestamp],
  students: List[UUID],
  isDeleted: Boolean,
  createdAt: Timestamp,
  updatedAt: Option[Timestamp]
)

object ClassRoom {
  implicit val format: Format[ClassRoom] = Json.format[ClassRoom]
}
