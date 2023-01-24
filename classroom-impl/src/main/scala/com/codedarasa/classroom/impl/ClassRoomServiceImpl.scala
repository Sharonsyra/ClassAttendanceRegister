package com.codedarasa.classroom.impl

import akka.NotUsed
import com.codedarasa.classroom.api.ClassRoomService
import com.codedarasa.common.{ClassRoom, CreateClassRoom, Timestamp}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import java.time.Instant
import java.util.UUID
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClassRoomServiceImpl extends ClassRoomService {

  val classRooms: ArrayBuffer[ClassRoom] = ArrayBuffer.empty[ClassRoom]

  lazy val instantTimeNow: Instant = Instant.now()

  lazy val timeNow: Timestamp = Timestamp(
    seconds = instantTimeNow.getEpochSecond,
    nanos = instantTimeNow.getNano
  )

  def classRoomNotFoundException(classRoomUuid: UUID) =
    throw new Exception(s"classRoom uuid $classRoomUuid does not exist")

  override def createClassRoom: ServiceCall[CreateClassRoom, ClassRoom] = ServiceCall { request: CreateClassRoom =>
    val classRoomUuid: UUID = UUID.randomUUID()

    classRooms
      .find(_.className.equals(request.className))
      .match {
        case Some(_) => throw new Exception(s"classroom ${request.className} already exists")
        case None =>
          val newClassRoom = ClassRoom(
            classUuid = classRoomUuid,
            className = request.className,
            createdAt = timeNow
          )

          classRooms.+=(newClassRoom)

          Future(newClassRoom)
      }

  }

  override def deleteClassRoom(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom] = ServiceCall { _ =>
    val classRoomToDelete = classRooms
      .find(_.classUuid.equals(classRoomUuid))
      .match {
        case Some(classRoom) =>
          require(!classRoom.isDeleted, "class room is deleted")
          require(!classRoom.isInSession, "class room is currently in session")
          require(classRoom.students.isEmpty, "class room contains students")

          classRoom
        case None => classRoomNotFoundException(classRoomUuid)
      }

    val deletedClassRoom = classRoomToDelete.copy(
      isDeleted = true
    )

    classRooms.-=(classRoomToDelete)

    classRooms.+=(deletedClassRoom)

    Future(deletedClassRoom)
  }

  override def listClassRooms: ServiceCall[NotUsed, List[ClassRoom]] = ???

  override def startClassRoomSession(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom] = ???

  override def endClassRoomSession(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom] = ???
}
