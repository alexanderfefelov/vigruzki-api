import better.files.File

import xml._
import ru.gov.rkn.vigruzki._

object Main extends App {

  if (args.length != 1) {
    println("You must specify the path to dump.xml as a command-line argument")
    System.exit(1)
  }

  val dumpXml = args(0)

  val rawXml = XML.loadFile(dumpXml)
  val register = scalaxb.fromXML[RegisterType](rawXml)
  File("urls-unique.txt").write(register.content.flatMap(_.url).distinct.mkString("\n"))
  File("domains- unique.txt").write(register.content.flatMap(_.domain).filterNot(_.contains("*")).distinct.mkString("\n"))
  File("domain-masks-unique.txt").write(register.content.flatMap(_.domain).filter(_.contains("*")).distinct.mkString("\n"))
  File("ips-unique.txt").write(register.content.flatMap(_.ip).distinct.mkString("\n"))
  File("ip-subnets-unique.txt").write(register.content.flatMap(_.ipSubnet).distinct.mkString("\n"))

}
