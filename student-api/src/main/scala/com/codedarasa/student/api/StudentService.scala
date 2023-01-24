package com.codedarasa.student.api

import akka.NotUsed
import com.codedarasa.common.{CreateStudent, Student}
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

import java.util.UUID

trait StudentService extends Service {

  def createStudent: ServiceCall[CreateStudent, Student]

  def deleteStudent(studentUuid: UUID): ServiceCall[NotUsed, Student]

  def listStudents: ServiceCall[NotUsed, Student]

  def checkInStudentIntoClass(studentUuid: UUID, classRoomUuid: UUID): ServiceCall[NotUsed, Student]

  def checkOutStudentFromClass(studentUuid: UUID, classRoomUuid: UUID): ServiceCall[NotUsed, Student]

  override def descriptor: Descriptor = {
    named("student")
      .withCalls(
        restCall(Method.POST, "/api/students", createStudent),
        restCall(Method.DELETE, "/api/students/:studentuuid", deleteStudent _),
        restCall(Method.GET, "/api/students", listStudents),
        restCall(Method.PATCH, "/api/students/:studentuuid/checkIn/:classroomuuid", checkInStudentIntoClass _),
        restCall(Method.PATCH, "/api/students/:studentuuid/checkOut/:classroomuuid", checkOutStudentFromClass _)
      )
      .withAutoAcl(true)
  }

}
