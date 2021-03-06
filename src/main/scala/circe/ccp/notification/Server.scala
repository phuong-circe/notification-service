package circe.ccp.notification


import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.thrift.ThriftServer
import com.twitter.finatra.thrift.routing.ThriftRouter

import circe.ccp.notification.module.DependencyModule
import circe.ccp.notification.controller.http.exception.CommonExceptionMapper
import circe.ccp.notification.controller.http.filter.CORSFilter
import circe.ccp.notification.controller.http._
import circe.ccp.notification.util.ZConfig
import circe.ccp.notification.controller.thrift

/**
  * Created by phg on 3/12/18.
  **/
object MainApp extends Server
class Server extends HttpServer with ThriftServer {

  override protected def defaultFinatraHttpPort: String = ZConfig.getString("server.http.port", ":8080")

  override protected def defaultFinatraThriftPort: String = ZConfig.getString("server.thrift.port", ":8082")

  override protected def disableAdminHttpServer: Boolean = ZConfig.getBoolean("server.admin.disable", default = true)

  override val modules = Seq(DependencyModule)

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CORSFilter](beforeRouting = true)
      .filter[CommonFilters]
      .exceptionMapper[CommonExceptionMapper]
      .add[PingController]
      .add[NotificationController]
  }

  override protected def configureThrift(router: ThriftRouter): Unit = {
    router
      .add[thrift.NotificationController]
  }
}
