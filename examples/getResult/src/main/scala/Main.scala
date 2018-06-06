import java.util.Date

import better.files.File
import com.github.alexanderfefelov.vigruzki.api._
import scalaxb.{DispatchHttpClientsAsync, Soap11ClientsAsync}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.control.Breaks._

object Main extends App {

  val ATTEMPTS = 5
  val DELAY_BETWEEN_ATTEMPTS = 45 * 1000

  if (args.length != 1) {
    println("You must specify a request code as a command-line argument")
  } else {
    val code = args(0)
    val service = (new OperatorRequestPortBindings with Soap11ClientsAsync with DispatchHttpClientsAsync).service
    breakable {
      for (i <- 1 to ATTEMPTS) {
        println(s"Attempt #$i")
        val response = Await.result(service.getResult(code), 10.seconds)
        println(s"${new Date()}")
        println(s"    result: ${response.result}")
        println(s"    resultCode: ${response.resultCode}")
        println(s"    resultComment: ${response.resultComment.getOrElse("")}")
        println(s"    dumpFormatVersion: ${response.dumpFormatVersion.getOrElse("")}")
        println(s"    operatorName: ${response.operatorName.getOrElse("")}")
        println(s"    inn: ${response.inn.getOrElse("")}")
        response.registerZipArchive match {
          case Some(zip) =>
            val filename = "register.zip"
            print(s"    registerZipArchive: $filename...")
            val file = File(filename)
            file.writeBytes(zip.iterator)
            println("done")
            break()
          case _ =>
        }

        println("Wait a bit...")
        Thread.sleep(DELAY_BETWEEN_ATTEMPTS)
        println("done")
      }
    }
    println("Finished. Press Ctrl+C")
  }

}
