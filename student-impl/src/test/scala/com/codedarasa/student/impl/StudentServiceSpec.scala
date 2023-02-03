package com.codedarasa.student.impl

import com.codedarasa.common.{CreateStudent, Student, StudentDetails}
import com.codedarasa.student.api.StudentService
import com.lightbend.lagom.scaladsl.server.{
  LagomApplicationContext,
  LocalServiceLocator
}
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

import scala.concurrent.Future

class StudentServiceSpec
    extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfter {

  private lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) {
    ctx: LagomApplicationContext =>
      new StudentApplication(ctx) with LocalServiceLocator
  }

  private val testClient: StudentService =
    server.serviceClient.implement[StudentService]

  override def beforeAll(): Unit = server

  override def afterAll(): Unit = server.stop()

  "The Student Service" should {
    "create student given unique student names" in {
      val firstName: String = "Waithira"
      val lastName: String = "Kagendo"
      val fullName: String = s"$firstName $lastName"

      val createStudentRequest = CreateStudent(
        firstName = firstName,
        lastName = lastName
      )

      testClient.createStudent.invoke(createStudentRequest).map {
        response: Student =>
          response.studentName shouldBe fullName
          response.isInClass shouldBe false
          response.isDeleted shouldBe false
          response.updatedAt shouldBe None
      }
    }

    "reject create student request given duplicate student name" in {
      val firstName: String = "Waithira"
      val lastName: String = "Kagendo"
      val fullName: String = s"$firstName $lastName"

      val createStudentRequest = CreateStudent(
        firstName = firstName,
        lastName = lastName
      )

      def createStudentCall: Future[Student] =
        testClient.createStudent.invoke(createStudentRequest)

      createStudentCall.flatMap { response1: Student =>
        response1.studentName shouldBe fullName

        createStudentCall.failed.map { response2: Throwable =>
          response2 shouldBe a[RuntimeException]
        }
      }
    }

    "delete a student given valid arguments" in {
      val firstName: String = "Waithira"
      val lastName: String = "Kagendo"
      val fullName: String = s"$firstName $lastName"

      val createStudentRequest = CreateStudent(
        firstName = firstName,
        lastName = lastName
      )

      lazy val createStudentCall: Future[Student] =
        testClient.createStudent.invoke(createStudentRequest)

      createStudentCall.flatMap { createStudentResponse: Student =>
        createStudentResponse.studentName shouldBe fullName

        testClient
          .deleteStudent(createStudentResponse.studentUuid)
          .invoke()
          .map { deleteStudentResponse: Student =>
            deleteStudentResponse.isDeleted shouldBe true
          }
      }
    }

    "list existing students" in {
      val firstName: String = "Waithira"
      val lastName: String = "Kagendo"
      val fullName: String = s"$firstName $lastName"

      val createStudentRequest = CreateStudent(
        firstName = firstName,
        lastName = lastName
      )

      lazy val createStudentCall: Future[Student] =
        testClient.createStudent.invoke(createStudentRequest)

      createStudentCall.flatMap { createStudentResponse: Student =>
        createStudentResponse.studentName shouldBe fullName

        testClient.listStudents
          .invoke()
          .map { listStudentResponse: List[StudentDetails] =>
            listStudentResponse.length shouldBe 1
            listStudentResponse.head.studentName shouldBe fullName
            listStudentResponse.head.isInClass shouldBe false
          }
      }
    }

    "return no students details when there is no student" in {
      testClient.listStudents
        .invoke()
        .map { listStudentResponse: List[StudentDetails] =>
          listStudentResponse.length shouldBe 0
        }
    }
  }
}
