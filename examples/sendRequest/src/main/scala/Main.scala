import java.io.{BufferedReader, InputStreamReader}
import java.security.Security
import java.util.Date

import better.files.File
import com.typesafe.config.ConfigFactory
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder
import org.bouncycastle.cms.{CMSProcessableByteArray, CMSSignedDataGenerator}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.operator.jcajce.{JcaContentSignerBuilder, JcaDigestCalculatorProviderBuilder}
import org.joda.time.{DateTime, Days}
import com.github.alexanderfefelov.vigruzki.api._

import scalaxb.{Base64Binary, DispatchHttpClientsWithRedirectAsync, Soap11ClientsAsync}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Codec
import scala.util.{Failure, Success}

object Main extends App {

  Security.addProvider(new BouncyCastleProvider)

  readPemFile("provider.pem") match {
    case (Some(privateKeyInfo), Some(x509CertificateHolder)) =>
      val conf = ConfigFactory.load()
      val operatorName = conf.getString("provider.operatorName")
      val inn = conf.getString("provider.inn")
      val ogrn = conf.getString("provider.ogrn")
      val email = conf.getString("provider.email")
      val dumpFormatVersion = conf.getString("register.dumpFormatVersion")
      val certificateWillExpire = conf.getInt("warnings.certificateWillExpire")

      val expire = new DateTime(x509CertificateHolder.getNotAfter)
      val now = DateTime.now()
      val daysLeft = Days.daysBetween(now.toLocalDate, expire.toLocalDate).getDays
      if (daysLeft < certificateWillExpire) {
        println(s"Attention! Your certificate will expire at $expire, $daysLeft day(s) left")
      }

      implicit val codec = Codec("windows-1251")
      val request = xml.request(DateTime.now, operatorName, inn, ogrn, email).body.lines.filter(_.nonEmpty).mkString("\n")
      File("request.txt").write(request)
      val requestBytes = request.getBytes("windows-1251")

      val signatureData = sign(privateKeyInfo, x509CertificateHolder, requestBytes)
      val signatureData64 = new sun.misc.BASE64Encoder().encode(signatureData)
      val signature = s"-----BEGIN PKCS7-----\n$signatureData64\n-----END PKCS7-----"
      File("request.txt.sig").write(signature)
      val signatureBytes = signature.getBytes

      val service = (new OperatorRequestPortBindings with Soap11ClientsAsync with DispatchHttpClientsWithRedirectAsync).service
      service.sendRequest(new Base64Binary(requestBytes.toVector), new Base64Binary(signatureBytes.toVector), Some(dumpFormatVersion)).onComplete {
        case Success(response) =>
          println(s"${new Date()}")
          println(s"    result: ${response.result}")
          println(s"    resultComment: ${response.resultComment.getOrElse("")}")
          println(s"    code: ${response.code.getOrElse("")}")
          println("Finished. Press Ctrl+C")
        case Failure(error) => println(error)
          println("Finished. Press Ctrl+C")
      }

    case _ => println("Invalid PEM file")
  }

  private def readPemFile(filename: String): (Option[PrivateKeyInfo], Option[X509CertificateHolder]) = {
    val inputStream = this.getClass.getResourceAsStream(filename)
    val reader = new BufferedReader(new InputStreamReader(inputStream))
    val pemParser = new PEMParser(reader)
    var pemEntries = ArrayBuffer.empty[Any]
    Stream.continually(pemParser.readObject()).takeWhile(_ ne null) foreach { entry =>
      pemEntries += entry
    }
    pemParser.close()
    reader.close()
    inputStream.close()

    val privateKeyInfoOption = pemEntries.find(_.isInstanceOf[PrivateKeyInfo]).asInstanceOf[Option[PrivateKeyInfo]]
    val x509CertificateHolderOption = pemEntries.find(_.isInstanceOf[X509CertificateHolder]).asInstanceOf[Option[X509CertificateHolder]]
    (privateKeyInfoOption, x509CertificateHolderOption)
  }

  private def sign(privateKeyInfo: PrivateKeyInfo, certHolder: X509CertificateHolder, data: Array[Byte]) = {
    val providerName = BouncyCastleProvider.PROVIDER_NAME
    val signatureAlgorithm = "GOST3411withECGOST3410"
    val keyConverter = new JcaPEMKeyConverter()
    val privateKey = keyConverter.getPrivateKey(privateKeyInfo)
    val signer = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(providerName).build(privateKey)
    val signerInfo = new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(providerName).build()).build(signer, certHolder)
    val generator = new CMSSignedDataGenerator()
    generator.addSignerInfoGenerator(signerInfo)
    generator.addCertificate(certHolder)
    val content = new CMSProcessableByteArray(data)
    val signedData = generator.generate(content, false)
    signedData.getEncoded
  }

}
