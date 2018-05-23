import java.net.InetAddress

import akka.actor.{ActorRef, ActorSystem}
import akka.io.{Dns, IO}
import akka.pattern._
import akka.util.Timeout
import be.jvb.iptypes.IpNetwork
import better.files.File
import com.netaporter.uri._
import com.netaporter.uri.config.UriConfig
import com.netaporter.uri.decoding.NoopDecoder
import com.github.alexanderfefelov.vigruzki.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.xml._

object Main extends App {

  if (args.length < 1) {
    println("You must specify the path to dump.xml as a command-line argument")
    System.exit(1)
  }

  val dumpXml = args(0)

  val rawXml = XML.loadFile(dumpXml)
  val register = scalaxb.fromXML[RegisterType](rawXml)
  val content = if (args.length == 2) {
    register.content.filter(x => x.decision.number == args(1))
  } else {
    register.content
  }

  val urlsUnique = content.flatMap(_.url).map(_.value).sorted.distinct
  val domainsUnique = content.flatMap(_.domain).filterNot(_.value.contains("*")).map(_.value).sorted.distinct
  val domainMasksUnique = content.flatMap(_.domain).filter(_.value.contains("*")).map(_.value).sorted.distinct
  val ipsUnique = content.flatMap(_.ip).map(_.value).sorted.distinct
  val ipSubnetsUnique = content.flatMap(_.ipSubnet).map(_.value).sorted.distinct
  val ipsFromIpSubnetsUnique = ipSubnetsUnique.map(new IpNetwork(_)).flatMap(_.addresses.toList).map(_.toString).distinct
  implicit val uriConfig = UriConfig(decoder = NoopDecoder)
  val domainsFromUrlsUnique = urlsUnique.flatMap(Uri.parse(_).host).sorted.distinct
  val domainsUniqueTotal = (domainsUnique ++ domainsFromUrlsUnique).sorted.distinct

  File("urls-unique.txt").write(urlsUnique.mkString("\n"))
  File("domains-unique.txt").write(domainsUnique.mkString("\n"))
  File("domain-masks-unique.txt").write(domainMasksUnique.mkString("\n"))
  File("ips-unique.txt").write(ipsUnique.mkString("\n"))
  File("ip-subnets-unique.txt").write(ipSubnetsUnique.mkString("\n"))
  File("ips-from-ip-subnets-unique.txt").write(ipsFromIpSubnetsUnique.mkString("\n"))
  File("domains-from-urls-unique.txt").write(domainsFromUrlsUnique.mkString("\n"))
  File("domains-unique-total.txt").write(domainsUniqueTotal.mkString("\n"))

  implicit val actorSystem = ActorSystem()
  def askWithRetry(actorRef: ActorRef, message: Any, timeout: Timeout, maxAttempts: Int, delay: Duration, attempt: Int = 0): Any = {
    try {
      val result = actorRef.ask(message)(timeout)
      Await.result(result, timeout.duration)
    } catch {
      case e: Exception if attempt < maxAttempts =>
        System.err.println(s"Retrying $message")
        Thread.sleep(delay.length) // Very naive
        askWithRetry(actorRef, message, timeout, maxAttempts, delay, attempt + 1)
    }
  }
  var errorCount = 0
  val dnsResolved = domainsUniqueTotal.par.map { domain =>
    try {
      askWithRetry(IO(Dns), Dns.Resolve(domain), 5.seconds, 3, 3.seconds).asInstanceOf[Dns.Resolved]
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
  val ipsResolvedUnique = dnsResolved.flatMap(_.ipv4).filterNot(_.isLoopbackAddress).map(_.toString.drop(1)).toList.sorted.distinct
  val ipsUniqueTotal = (ipsUnique ++ ipsFromIpSubnetsUnique ++ ipsResolvedUnique).sorted.distinct

  File("ips-resolved-unique.txt").write(ipsResolvedUnique.mkString("\n"))
  File("ips-unique-total.txt").write(ipsUniqueTotal.mkString("\n"))

  actorSystem.terminate()

}
