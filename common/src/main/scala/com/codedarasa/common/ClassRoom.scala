package com.codedarasa.common

import play.api.libs.json.{Format, Json}

import java.util.UUID

case class ClassRoom (
  classUuid: UUID,
  className: String,
  isInSession: Boolean = false,
  sessionStart: Option[Timestamp] = None,
  students: List[UUID] = List.empty[UUID],
  isDeleted: Boolean = false,
  createdAt: Timestamp,
  updatedAt: Option[Timestamp] = None
)

object ClassRoom {
  implicit val format: Format[ClassRoom] = Json.format[ClassRoom]
}
