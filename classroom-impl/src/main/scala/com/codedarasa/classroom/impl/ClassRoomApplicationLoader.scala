package com.codedarasa.classroom.impl

import com.codedarasa.classroom.api.ClassRoomService
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

abstract class ClassRoomApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents {
    override lazy val lagomServer: LagomServer = serverFor[ClassRoomService](wire[ClassRoomServiceImpl])
}

class ClassRoomApplicationLoader extends LagomApplicationLoader {
    override def load(context: LagomApplicationContext): LagomApplication =
        new ClassRoomApplication(context) {
            override def serviceLocator: ServiceLocator = ServiceLocator.NoServiceLocator
        }

    override def loadDevMode(context: LagomApplicationContext): LagomApplication =
        new ClassRoomApplication(context) with LagomDevModeComponents

    override def describeService: Option[Descriptor] = Some(readDescriptor[ClassRoomService])
}