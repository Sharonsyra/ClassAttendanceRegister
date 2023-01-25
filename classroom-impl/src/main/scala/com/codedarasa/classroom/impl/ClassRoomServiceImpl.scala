package com.codedarasa.classroom.impl

import akka.NotUsed
import com.codedarasa.classroom.api.ClassRoomService
import com.codedarasa.common.{ClassRoom, ClassRoomDetails, CreateClassRoom, Timestamp}
import com.lightbend.lagom.scaladsl.api.ServiceCall

import java.time.Instant
import java.util.UUID
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClassRoomServiceImpl extends ClassRoomService {

  lazy val instantTimeNow: Instant = Instant.now()
  lazy val timeNow: Timestamp = Timestamp(
    seconds = instantTimeNow.getEpochSecond,
    nanos = instantTimeNow.getNano
  )
  val classRooms: ArrayBuffer[ClassRoom] = ArrayBuffer.empty[ClassRoom]

  def classRoomNotFoundException(classRoomUuid: UUID) =
    throw new Exception(s"classRoom uuid $classRoomUuid does not exist")

  override def createClassRoom: ServiceCall[CreateClassRoom, ClassRoom] = ServiceCall { request: CreateClassRoom =>
    val classRoomUuid: UUID = UUID.randomUUID()

    classRooms
      .find(_.className.toLowerCase.equals(request.className.toLowerCase))
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
          require(!classRoom.isInSession, "class room is in session")
          require(classRoom.students.isEmpty, "class room contains students")

          classRoom
        case None => classRoomNotFoundException(classRoomUuid)
      }

    val deletedClassRoom = classRoomToDelete.copy(
      isDeleted = true,
      updatedAt = Some(timeNow)
    )

    classRooms.-=(classRoomToDelete)

    classRooms.+=(deletedClassRoom)

    Future(deletedClassRoom)
  }

  override def listClassRooms: ServiceCall[NotUsed, List[ClassRoomDetails]] = ServiceCall { _ =>
    Future {
      classRooms
        .map { classRoom: ClassRoom =>
          ClassRoomDetails(
            className = classRoom.className,
            isInSession = classRoom.isInSession,
            numberOfStudents = classRoom.students.size
          )
        }
        .toList
    }
  }

  override def startClassRoomSession(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom] = ServiceCall { _ =>
    val classRoomToStartSession = classRooms
      .find(_.classUuid.equals(classRoomUuid))
      .match {
        case Some(classRoom) =>
          require(!classRoom.isDeleted, "class room is deleted")
          require(!classRoom.isInSession, "class room is in session")
          require(classRoom.students.isEmpty, "class room contains students")

          classRoom
        case None => classRoomNotFoundException(classRoomUuid)
      }

    val classRoomWithStartedSession = classRoomToStartSession.copy(
      isInSession = true,
      sessionStart = Some(timeNow)
    )

    classRooms.-=(classRoomToStartSession)

    classRooms.+=(classRoomWithStartedSession)

    Future(classRoomWithStartedSession)

  }

  override def endClassRoomSession(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom] = ServiceCall { _ =>
    val classRoomToEndSession: ClassRoom = classRooms
      .find(_.classUuid.equals(classRoomUuid))
      .match {
        case Some(classRoom) =>
          require(!classRoom.isDeleted, "class room is deleted")
          require(classRoom.isInSession, "class room is not in session")

          classRoom
        case None => classRoomNotFoundException(classRoomUuid)
      }

    val classRoomWithEndedSession = classRoomToEndSession.copy(
      isInSession = false,
      sessionStart = None
    )

    classRooms.-=(classRoomToEndSession)

    classRooms.+=(classRoomWithEndedSession)

    Future(classRoomWithEndedSession)
  }
}
