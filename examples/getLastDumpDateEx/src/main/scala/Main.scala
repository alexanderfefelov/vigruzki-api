import java.util.Date

import ru.gov.rkn.vigruzki._

import scalaxb._

object Main extends App {

  val service = (new OperatorRequestPortBindings with Soap11ClientsAsync with DispatchHttpClientsAsync).service
  while (true) {
    val responseFuture = service.getLastDumpDateEx()
    responseFuture.onSuccess {
      case response =>
        println(s"${new Date()}")
        println(s"    lastDumpDate: ${new Date(response.lastDumpDate)}")
        println(s"    lastDumpDateUrgently: ${new Date(response.lastDumpDateUrgently)}")
        println(s"    webServiceVersion: ${response.webServiceVersion}")
        println(s"    dumpFormatVersion: ${response.dumpFormatVersion}")
        println(s"    docVersion: ${response.docVersion}")
    }
    responseFuture.onFailure {
      case error => println(error)
    }
    Thread.sleep(60000)
  }

}
