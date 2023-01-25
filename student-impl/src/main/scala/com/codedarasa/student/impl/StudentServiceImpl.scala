package com.codedarasa.student.impl

import akka.NotUsed
import com.codedarasa.common.{CreateStudent, Student, StudentDetails, Timestamp}
import com.codedarasa.student.api.StudentService
import com.lightbend.lagom.scaladsl.api.ServiceCall

import java.time.Instant
import java.util.UUID
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudentServiceImpl extends StudentService {

  lazy val instantTimeNow: Instant = Instant.now()
  lazy val timeNow: Timestamp = Timestamp(
    seconds = instantTimeNow.getEpochSecond,
    nanos = instantTimeNow.getNano
  )

  val students: ArrayBuffer[Student] = ArrayBuffer.empty[Student]

  def studentNotFoundException(studentUuid: UUID) =
    throw new Exception(s"student uuid $studentUuid does not exist")


  override def createStudent: ServiceCall[CreateStudent, Student] = ServiceCall { request: CreateStudent =>
    val studentUuid: UUID = UUID.randomUUID()

    students
      .find(_.studentName.toLowerCase.equals(s"${request.firstName} ${request.lastName}".toLowerCase))
      .match {
        case Some(_) =>
          throw new Exception(s"student with name: ${request.firstName} ${request.lastName} already exists")
        case None =>
         val newStudent = Student(
           studentUuid = studentUuid,
           studentName = s"${request.firstName} ${request.lastName}",
           createdAt = timeNow
         )

        students.+=(newStudent)

        Future(newStudent)
      }
  }

  override def deleteStudent(studentUuid: UUID): ServiceCall[NotUsed, Student] = ServiceCall { _ =>
    val studentToDelete = students
      .find(_.studentUuid.equals(studentUuid))
      .match {
        case Some(student) =>
          require(!student.isDeleted, "student is deleted")
          require(!student.isInClass, "student is in a class")

          student
        case None => studentNotFoundException(studentUuid)
      }

    val deletedStudent = studentToDelete.copy(
      isDeleted = true,
      updatedAt = Some(timeNow)
    )

    students.-=(studentToDelete)

    students.+=(deletedStudent)

    Future(deletedStudent)
  }

  override def listStudents: ServiceCall[NotUsed, List[StudentDetails]] = ServiceCall { _ =>
    Future {
      students
        .map { student: Student =>
          StudentDetails(
            studentName = student.studentName,
            isInClass = student.isInClass
          )

        }
        .toList
    }
  }

  override def checkInStudentIntoClass(studentUuid: UUID, classRoomUuid: UUID): ServiceCall[NotUsed, Student] = ServiceCall { _ =>
    val studentToCheckIn = students
      .find(_.studentUuid.equals(studentUuid))
      .match {
        case Some(student) =>
          require(!student.isDeleted, "student is deleted")
          require(!student.isInClass, "student is in a class")

          student
        case None => studentNotFoundException(studentUuid)
      }

    val checkedInStudent = studentToCheckIn.copy(
      isInClass = true,
      checkedInTime = Some(timeNow),
      updatedAt = Some(timeNow)
    )

    students.-=(studentToCheckIn)

    students.+=(checkedInStudent)

    Future(checkedInStudent)
  }

  override def checkOutStudentFromClass(studentUuid: UUID, classRoomUuid: UUID): ServiceCall[NotUsed, Student] =
    ServiceCall { _ =>
      val studentToCheckOut = students
        .find(_.studentUuid.equals(studentUuid))
      .match {
        case Some(student) =>
          require(!student.isDeleted, "student is deleted")
          require(student.isInClass, "student is not in a class")

          student
        case None => studentNotFoundException(studentUuid)
      }

      val checkedOutStudent = studentToCheckOut.copy(
        isInClass = false,
        checkedInTime = None,
        updatedAt = Some(timeNow)
      )

      students.-=(studentToCheckOut)

      students.+=(checkedOutStudent)

      Future(checkedOutStudent)
    }
}
