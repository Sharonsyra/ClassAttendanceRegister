package com.codedarasa.classroom.api

import akka.NotUsed
import com.codedarasa.common.{ClassRoom, CreateClassRoom}
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

import java.util.UUID

trait ClassRoomService extends Service {

  def createClassRoom: ServiceCall[CreateClassRoom, ClassRoom]

  def deleteClassRoom(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom]

  def listClassRooms: ServiceCall[NotUsed, List[ClassRoom]]

  def startClassRoomSession(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom]

  def endClassRoomSession(classRoomUuid: UUID): ServiceCall[NotUsed, ClassRoom]

  override def descriptor: Descriptor = {
    named("classroom")
      .withCalls(
        restCall(Method.POST, "/api/classrooms", createClassRoom),
        restCall(Method.DELETE, "/api/classrooms/:classroomuuid", deleteClassRoom _),
        restCall(Method.GET, "/api/classrooms", listClassRooms),
        restCall(Method.PATCH, "/api/classrooms/:classroomuuid/startSession", startClassRoomSession _),
        restCall(Method.PATCH, "/api/classrooms/:classroomuuid/endSession", endClassRoomSession _)
      )
      .withAutoAcl(true)
  }
}
