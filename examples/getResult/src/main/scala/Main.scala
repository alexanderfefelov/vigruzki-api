import java.util.Date

import better.files.File
import com.github.alexanderfefelov.vigruzki.api._

import scalaxb.{DispatchHttpClientsAsync, Soap11ClientsAsync}
import scala.util.{Failure, Success}

object Main extends App {

  if (args.length != 1) {
    println("You must specify a request code as a command-line argument")
  } else {
    val code = args(0)
    val service = (new OperatorRequestPortBindings with Soap11ClientsAsync with DispatchHttpClientsAsync).service
    service.getResult(code).onComplete {
      case Success(response) =>
        println(s"${new Date()}")
        println(s"    result: ${response.result}")
        println(s"    resultCode: ${response.resultCode}")
        println(s"    resultComment: ${response.resultComment.getOrElse("")}")
        println(s"    dumpFormatVersion: ${response.dumpFormatVersion.getOrElse("")}")
        println(s"    operatorName: ${response.operatorName.getOrElse("")}")
        println(s"    inn: ${response.inn.getOrElse("")}")
        response.registerZipArchive map { zip =>
          val filename = "register.zip"
          print(s"    registerZipArchive: $filename...")
          val file = File(filename)
          file.writeBytes(zip.iterator)
          println("done")
        }
      case Failure(error) => println(error)
    }
  }

}
