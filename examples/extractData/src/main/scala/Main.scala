import better.files.File
import com.netaporter.uri._
import com.netaporter.uri.config.UriConfig
import com.netaporter.uri.decoding.NoopDecoder
import ru.gov.rkn.vigruzki._

import scala.xml._

object Main extends App {

  if (args.length != 1) {
    println("You must specify the path to dump.xml as a command-line argument")
    System.exit(1)
  }

  val dumpXml = args(0)

  val rawXml = XML.loadFile(dumpXml)
  val register = scalaxb.fromXML[RegisterType](rawXml)

  val urlsUnique = register.content.flatMap(_.url).distinct
  val domainsUnique = register.content.flatMap(_.domain).filterNot(_.contains("*")).distinct
  val domainMasksUnique = register.content.flatMap(_.domain).filter(_.contains("*")).distinct
  val ipsUnique = register.content.flatMap(_.ip).distinct
  val ipSubnetsUnique = register.content.flatMap(_.ipSubnet).distinct
  implicit val uriConfig = UriConfig(decoder = NoopDecoder)
  val domainsFromUrlsUnique = urlsUnique.flatMap(Uri.parse(_).host).distinct
  val domainsUniqueTotal = (domainsUnique ++ domainsFromUrlsUnique).distinct

  File("urls-unique.txt").write(urlsUnique.mkString("\n"))
  File("domains-unique.txt").write(domainsUnique.mkString("\n"))
  File("domain-masks-unique.txt").write(domainMasksUnique.mkString("\n"))
  File("ips-unique.txt").write(ipsUnique.mkString("\n"))
  File("ip-subnets-unique.txt").write(ipSubnetsUnique.mkString("\n"))
  File("domains-from-urls-unique.txt").write(domainsFromUrlsUnique.mkString("\n"))
  File("domains-unique-total.txt").write(domainsUniqueTotal.mkString("\n"))

}
