import java.security.Security
import java.util

import better.files.File
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder
import org.bouncycastle.cms.{CMSProcessableByteArray, CMSSignedData}
import org.bouncycastle.jce.provider.BouncyCastleProvider

import scala.collection.JavaConversions._

object Main extends App {

  if (args.length != 1) {
    println("You must specify the path to register.zip as a command-line argument")
    System.exit(1)
  }

  val registerZip = args(0)

  Security.addProvider(new BouncyCastleProvider)

  print(".zip file ")
  val zip = File(registerZip)
  val dir = zip.unzipTo(File("."))
  println("Ok")

  print("dump.xml ")
  val xml = dir / "dump.xml"
  val xmlBytes = xml.loadBytes
  println("Ok")

  print("dump.xml.sig ")
  val sig = dir / "dump.xml.sig"
  val sigBytes = sig.loadBytes
  println("Ok")

  print("signature")
  val providerName = BouncyCastleProvider.PROVIDER_NAME
  val signedData = new CMSSignedData(new CMSProcessableByteArray(xmlBytes), sigBytes)
  val certificates = signedData.getCertificates
  val signerInfos = signedData.getSignerInfos
  val signers = signerInfos.getSigners()
  for (signer <- signers) {
    val certs = certificates.getMatches(null).asInstanceOf[util.Collection[X509CertificateHolder]] // TODO null
    for (x509CertificateHolder <- certs) {
      val verifierBuilder = new JcaSimpleSignerInfoVerifierBuilder()
      val verifier = verifierBuilder.setProvider(providerName).build(x509CertificateHolder)
      val result = signer.verify(verifier)
      if (result) {
        println(" Ok")
      } else {
        println(" Fail")
      }
    }
  }

}
