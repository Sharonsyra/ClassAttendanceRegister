package com.codedarasa.student.impl

import com.codedarasa.student.api.StudentService
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

abstract class StudentApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents {
  override lazy val lagomServer: LagomServer = serverFor[StudentService](wire[StudentServiceImpl])
}

class StudentApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new StudentApplication(context) {
      override def serviceLocator: ServiceLocator = ServiceLocator.NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new StudentApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[StudentService])
}