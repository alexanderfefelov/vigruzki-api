package scalaxb

import java.nio.charset.Charset

import scala.concurrent._, duration._

trait DispatchHttpClientsWithRedirectAsync extends HttpClientsAsync {
  lazy val httpClient = new DispatchHttpClientWithRedirect {}
  def requestTimeout: Duration = 60.seconds
  def connectionTimeout: Duration = 5.seconds

  trait DispatchHttpClientWithRedirect extends HttpClient {
    import dispatch._

    // Keep it lazy. See https://github.com/eed3si9n/scalaxb/pull/279
    lazy val http = Http.withConfiguration(_.
      setRequestTimeout(requestTimeout.toMillis.toInt).
      setConnectTimeout(connectionTimeout.toMillis.toInt).
      setFollowRedirect(true))

    def request(in: String, address: java.net.URI, headers: Map[String, String])(implicit ec: ExecutionContext): Future[String] = {
      val req = url(address.toString).setBodyEncoding(Charset.forName("UTF-8")) <:< headers << in
      http(req > as.String)
    }
  }
}
