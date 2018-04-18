import com.netaporter.uri._
import com.netaporter.uri.config.UriConfig
import com.netaporter.uri.decoding.NoopDecoder
import com.github.alexanderfefelov.vigruzki.api._

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
  val schemesUnique = urlsUnique.map(Uri.parse(_)).groupBy(_.scheme).map(x => x._1 -> x._2.size)
  val portsUnique = urlsUnique.map(Uri.parse(_)).groupBy(_.port).map(x => x._1 -> x._2.size)
  val domainsFromUrlsUnique = urlsUnique.flatMap(Uri.parse(_).host).distinct
  val domainsUniqueTotal = (domainsUnique ++ domainsFromUrlsUnique).distinct

  println(s"Update time: ${register.updateTime}")
  println(s"Update time urgently: ${register.updateTimeUrgently}")
  println(s"Content records: ${register.content.size}")
  println(s"URLs/unique: ${register.content.map(_.url.size).sum}/${urlsUnique.size}")
  println(s"Schemes unique: ${prettyPrint(schemesUnique)}")
  println(s"Ports unique: ${prettyPrint(portsUnique)}")
  println(s"Domains/unique: ${register.content.map(_.domain.filterNot(_.contains("*")).size).sum}/${domainsUnique.size}")
  println(s"Domain masks/unique: ${register.content.map(_.domain.count(_.contains("*"))).sum}/${domainMasksUnique.size}")
  println(s"Unique domains from URLs: ${domainsFromUrlsUnique.size}")
  println(s"Total unique domains: ${domainsUniqueTotal.size}")
  println(s"IPs/unique: ${register.content.map(_.ip.size).sum}/${ipsUnique.size}")
  println(s"IP subnets/unique: ${register.content.map(_.ipSubnet.size).sum}/${ipSubnetsUnique.size}")
  println(s"Urgency type: ${prettyPrint(register.content.groupBy(_.urgencyType).map(x => x._1 -> x._2.size))}")
  println(s"Entry type: ${prettyPrint(register.content.groupBy(_.entryType).map(x => Option(x._1) -> x._2.size))}")
  println(s"Block type: ${prettyPrint(register.content.groupBy(_.blockType).map(x => x._1 -> x._2.size))}")
  println(s"Org: ${prettyPrint(register.content.groupBy(_.decision.org).map(x => Option(x._1) -> x._2.size))}")

  def prettyPrint[T](m: Map[Option[T], Int]): String = {
    val list = m.map { x =>
      val key = x._1 match {
        case Some(value) => value
        case _ => "Не указано"
      }
      s"$key -> ${x._2}"
    }
    list.mkString(", ")
  }

}
