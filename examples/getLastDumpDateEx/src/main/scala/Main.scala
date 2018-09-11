import java.util.Date

import com.github.alexanderfefelov.vigruzki.api._

import scalaxb._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main extends App {

  val service = (new OperatorRequestPortBindings with Soap11ClientsAsync with DispatchHttpClientsWithRedirectAsync).service
  while (true) {
    service.getLastDumpDateEx().onComplete {
      case Success(response) =>
        println(s"${new Date()}")
        println(s"    lastDumpDate: ${new Date(response.lastDumpDate)}")
        println(s"    lastDumpDateUrgently: ${new Date(response.lastDumpDateUrgently)}")
        println(s"    webServiceVersion: ${response.webServiceVersion}")
        println(s"    dumpFormatVersion: ${response.dumpFormatVersion}")
        println(s"    docVersion: ${response.docVersion}")
      case Failure(error) => println(error)
    }
    Thread.sleep(60000)
  }

}
