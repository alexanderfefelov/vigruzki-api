import java.net.InetAddress

import akka.actor.ActorSystem
import akka.io.{Dns, IO}
import akka.pattern._
import akka.util.Timeout
import better.files.File
import com.netaporter.uri._
import com.netaporter.uri.config.UriConfig
import com.netaporter.uri.decoding.NoopDecoder
import ru.gov.rkn.vigruzki._

import scala.concurrent.Await
import scala.concurrent.duration._
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

  implicit val actorSystem = ActorSystem()
  implicit val timeout = Timeout(5.seconds)
  var errorCount = 0
  val dnsResolved = domainsUniqueTotal.par.map { domain =>
    try {
      val lookup = IO(Dns) ? Dns.Resolve(domain)
      Await.result(lookup, 5.seconds).asInstanceOf[Dns.Resolved]
    } catch {
      case e: Exception =>
        errorCount += 1
        System.err.println(s"Error while resolving $domain: $e")
        Dns.Resolved(domain, Seq(InetAddress.getLoopbackAddress))
    }
  }
  if (errorCount > 0) {
    System.err.println(s"DNS lookup errors: $errorCount")
  }
  val ipsResolvedUnique = dnsResolved.flatMap(_.ipv4).filterNot(_.isLoopbackAddress).distinct.map(_.toString.drop(1))
  File("ips-resolved-unique.txt").write(ipsResolvedUnique.mkString("\n"))
  actorSystem.terminate()

}
