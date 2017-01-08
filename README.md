# vigruzki-api

## How to run examples

Install vigruzki-api into the local repository:

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
    sbt "run PATH_TO_YOUR_REGISTER_ZIP"

Or:

    cd examples/basicXmlStat
    sbt "run PATH_TO_YOUR_DUMP_XML"
