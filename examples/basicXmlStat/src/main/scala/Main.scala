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
  println("Content records: " + register.content.size)
  println(s"URLs/unique: ${register.content.map(_.url.size).sum}/${register.content.flatMap(_.url).distinct.size}")
  println(s"Domains/unique: ${register.content.map(_.domain.size).sum}/${register.content.flatMap(_.domain).distinct.size}")
  println(s"IPs/unique: ${register.content.map(_.ip.size).sum}/${register.content.flatMap(_.ip).distinct.size}")
  println(s"IP subnets/unique: ${register.content.map(_.ipSubnet.size).sum}/${register.content.flatMap(_.ipSubnet).distinct.size}")

}
