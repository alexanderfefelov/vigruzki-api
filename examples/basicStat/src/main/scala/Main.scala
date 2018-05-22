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
  val content = register.content

  val urls = content.flatMap(_.url)
  val domains = content.flatMap(_.domain)
  val ips = content.flatMap(_.ip)
  val ipSubnets = content.flatMap(_.ipSubnet)

  val urlsUnique = urls.map(_.value).distinct
  val domainsUnique = domains.filterNot(_.value.contains("*")).map(_.value).distinct
  val domainMasksUnique = domains.filter(_.value.contains("*")).map(_.value).distinct
  val ipsUnique = ips.map(_.value).distinct
  val ipSubnetsUnique = ipSubnets.map(_.value).distinct
  implicit val uriConfig = UriConfig(decoder = NoopDecoder)
  val schemesUnique = urlsUnique.map(Uri.parse(_)).groupBy(_.scheme).map(x => x._1 -> x._2.size)
  val portsUnique = urlsUnique.map(Uri.parse(_)).groupBy(_.port).map(x => x._1 -> x._2.size)
  val domainsFromUrlsUnique = urlsUnique.flatMap(Uri.parse(_).host).distinct
  val domainsUniqueTotal = (domainsUnique ++ domainsFromUrlsUnique).distinct

  println(s"Format version: ${register.formatVersion}")
  println(s"Update time: ${register.updateTime}")
  println(s"Update time urgently: ${register.updateTimeUrgently}")
  println(s"Content records: ${content.size}")
  println(s"URLs/unique: ${content.map(_.url.size).sum}/${urlsUnique.size}")
  println(s"Schemes unique: ${prettyPrint(schemesUnique)}")
  println(s"Ports unique: ${prettyPrint(portsUnique)}")
  println(s"Domains/unique: ${content.map(_.domain.filterNot(_.value.contains("*")).size).sum}/${domainsUnique.size}")
  println(s"Domain masks/unique: ${content.map(_.domain.count(_.value.contains("*"))).sum}/${domainMasksUnique.size}")
  println(s"Unique domains from URLs: ${domainsFromUrlsUnique.size}")
  println(s"Total unique domains: ${domainsUniqueTotal.size}")
  println(s"IPs/unique: ${content.map(_.ip.size).sum}/${ipsUnique.size}")
  println(s"IP subnets/unique: ${content.map(_.ipSubnet.size).sum}/${ipSubnetsUnique.size}")
  println(s"Urgency type: ${prettyPrint(content.groupBy(_.urgencyType).map(x => x._1 -> x._2.size))}")
  println(s"Entry type: ${prettyPrint(content.groupBy(_.entryType).map(x => Option(x._1) -> x._2.size))}")
  println(s"Block type: ${prettyPrint(content.groupBy(_.blockType).map(x => x._1 -> x._2.size))}")
  println(s"Org: ${prettyPrint(content.groupBy(_.decision.org).map(x => Option(x._1) -> x._2.size))}")

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
