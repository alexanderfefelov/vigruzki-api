# vigruzki-api

## How to run examples

Install vigruzki-api into the local repository:

    git clone https://github.com/alexanderfefelov/vigruzki-api.git
    cd vigruzki-api
    sbt publishLocal

Then:

    cd examples/getLastDumpDateEx
    sbt run

Or:

    cd examples/sendRequest
    cp /PATH/TO/YOUR/provider.pem src/main/resources
    vi src/main/resources/application.conf
    sbt run

Or:

    cd examples/getResult
    sbt "run REQUEST_CODE"

Or:

    cd examples/verifyZip
    sbt "run /PATH/TO/YOUR/register.zip"

Or:

    cd examples/basicStat
    sbt "run /PATH/TO/YOUR/dump.xml"

Or:

    cd /tmp
    git clone https://github.com/alexanderfefelov/scala-ipv4.git
    cd scala-ipv4
    sbt publishLocal
    cd --
    cd examples/extractData
    sbt "run /PATH/TO/YOUR/dump.xml"
    python ip-subnets-unique-whois.py
