# aws-kinesis-simulator

## System requirements
* java 1.8
* scala 2.13
* docker

Before running the project, start a mongo docker using command

 ``docker run -it -p 27017:27017 mongo``

Dev
===
* Copy `dev.template.conf` to `dev.conf` under `conf`
* Set `stream-name` to your kinesis instance
* Add `resident-samples.log` file into `resources` folder

Run
===
`sbt -Dconfig.file=conf/dev.conf run`
