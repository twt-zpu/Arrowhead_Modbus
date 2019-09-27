# Arrowhead Modbus
This project is based on the library [jlibmodbus](https://sourceforge.net/projects/jlibmodbus/). It provides the possibility to integrate the modbus master and slave as consumer and provider in an Arrowhead Framework local cloud. They work in the following way:
1. The *provider-master* is connected with slaves, e.g. Remote I/O. It will register the services to the Service Registry.
2. The *consumer-provider* is connected with a master, e.g. PLC. It will inform the *provider-master* to connect with which slave. Then, it will read and wirte coils or registers to the connected slave.

NOTE! The configuration files of proviede and comsumer need to be adjusted to the current project before running them.

## How to use
### Requirements
* Java JRE/JDK 8+
* Maven 3.5+
* Arrowhead core services running, for more info goto [core-java](http://github.com/arrowhead-f)
* Client-java built, for more information goto [client-java](http://github.com/arrowhead-f/client-java)

This project uses Maven. In order to use it, first download or clone this repository. Then in the root folder of run:
```mvn install```

### Setup and run
1. Download or clone the repository.
2. Edit the */provider/conf/\** and */consumer/conf/default.conf* files to match your Arrowhead Core Services.
3. Goto the root folder and run: ```mvn clean install```
4. configurate the modbus master (PLC) with the ip address of consumer 
5. connect all the systems together with Ethernet Cabel
6. run the Arrowhead Server
7. run the provider which is connected with the Remote I/O: `java -jar provider.jar sr auth orch` under the path */provider/target/*
8. add the consumer in the table "arrowhead_system" in the databank "Arrowhead", add the consumer and the consumed services in the table "intra_cloud_authorization" in the same databank
9. run the consumer which is connected with PLC: `java -jar consumer.jar` under the path */consumer/target/*
