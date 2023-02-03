package com.codedarasa.classroom.impl

import com.codedarasa.classroom.api.ClassRoomService
import com.codedarasa.common.{ClassRoom, ClassRoomDetails, CreateClassRoom}
import com.lightbend.lagom.scaladsl.server.{
  LagomApplicationContext,
  LocalServiceLocator
}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

import scala.concurrent.Future

class ClassRoomServiceSpec
    extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfter {

  private lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) {
    ctx: LagomApplicationContext =>
      new ClassRoomApplication(ctx) with LocalServiceLocator
  }

  private val testClient: ClassRoomService =
    server.serviceClient.implement[ClassRoomService]

  override def beforeAll(): Unit = server

  override def afterAll(): Unit = server.stop()

  "The ClassRoom Service" should {
    "create class room given unique classname" in {
      val className: String = "Jupiter"

      val createClassRoomRequest = CreateClassRoom(
        className = className
      )

      testClient.createClassRoom.invoke(createClassRoomRequest).map {
        response: ClassRoom =>
          response.className shouldBe className
          response.students.length shouldBe 0
          response.isInSession shouldBe false
          response.isDeleted shouldBe false
          response.updatedAt shouldBe None
      }
    }

    "reject create class room request given duplicate class name" in {
      val className: String = "Jupiter"

      val createClassRoomRequest = CreateClassRoom(
        className = className
      )

      def createClassRoomCall: Future[ClassRoom] =
        testClient.createClassRoom.invoke(createClassRoomRequest)

      createClassRoomCall.flatMap { response1: ClassRoom =>
        response1.className shouldBe className

        createClassRoomCall.failed.map { response2: Throwable =>
          response2 shouldBe a[RuntimeException]
        }
      }
    }

    "delete a class room given valid arguments" in {
      val className: String = "Jupiter"

      val createClassRoomRequest = CreateClassRoom(
        className = className
      )

      lazy val createClassRoomCall: Future[ClassRoom] =
        testClient.createClassRoom.invoke(createClassRoomRequest)

      createClassRoomCall.flatMap { createClassRoomResponse: ClassRoom =>
        createClassRoomResponse.className shouldBe className

        testClient
          .deleteClassRoom(createClassRoomResponse.classUuid)
          .invoke()
          .map { deleteClassRoomResponse: ClassRoom =>
            deleteClassRoomResponse.isDeleted shouldBe true
          }
      }
    }

    "list existing classrooms" in {
      val className: String = "Jupiter"

      val createClassRoomRequest = CreateClassRoom(
        className = className
      )

      lazy val createClassRoomCall: Future[ClassRoom] =
        testClient.createClassRoom.invoke(createClassRoomRequest)

      createClassRoomCall.flatMap { createClassRoomResponse: ClassRoom =>
        createClassRoomResponse.className shouldBe className

        testClient.listClassRooms
          .invoke()
          .map { listClassRoomsResponse: List[ClassRoomDetails] =>
            listClassRoomsResponse.length shouldBe 1
            listClassRoomsResponse.head.className shouldBe className
            listClassRoomsResponse.head.isInSession shouldBe false
            listClassRoomsResponse.head.numberOfStudents shouldBe 0
          }
      }
    }

    "return no classroom details when there is no classroom" in {
      testClient.listClassRooms
        .invoke()
        .map { listClassRoomsResponse: List[ClassRoomDetails] =>
          listClassRoomsResponse.length shouldBe 0
        }
    }
  }
}
