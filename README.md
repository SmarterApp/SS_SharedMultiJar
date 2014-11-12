# Welcome to the SharedMultiJarDev Application

The SharedMultiJarDev is group of shared modules that can be used by other projects for common functionality.

## License ##
This project is licensed under the [AIR Open Source License v1.0](http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf).

## Getting Involved ##
We would be happy to receive feedback on its capabilities, problems, or future enhancements:

* For general questions or discussions, please use the [Forum](http://forum.opentestsystem.org/viewforum.php?f=16).
* Use the **Issues** link to file bugs or enhancement requests.
* Feel free to **Fork** this project and develop your changes!

## Module Overview

### shared-common

shared-common module contains an implementation of the common and utility classes like MultiValueDictionary, Helper classes, Date and Time utility classes, String utility classes which can be re-used by the other projects.

### shared-config

shared-config module contains all the common configuration classes that can be used by other projects.

### shared-db

The shared-db module contains all the common database layer classes which can be used by tds-dll and other project which requires implementation of the Data Access layer.

### shared-db-test

shared-db-test contains common classes related to data access layer tests.

### shared-json

shared-json contains common JSON conversion classes like HashMapDataSerializer, BooolToString etc. These utility classes can be used by other project for JSON manipulation.

### shared-log4j

shared-log4j contains common log4j configuration classes and custom appenders for log4j.

### shared-logging

shared-logging contains ITDSLogger interface which can be implemented by other projects for certain logging functions like javascriptError and sqlWarn.

### shared-mq

shared-mq contains implementation for MQReceiver and MQSender.

### shared-security

shared-security contains common security classes for Encryption and Authentication.

### shared-test

shared-test contains testing framework interfaces and utility classes.

### shared-test-api

shared-test-api contains testing framework API classes.

### shared-test-jetty-container

shared-test-jetty-container contains implementation of jetty container for running integration testing.

### shared-threading

shared-threading contains a common interface and implementation of the ThreadPool.

### shared-tr-api

shared-tr-api contains interfaces and implementation classes for integration with "Administration and Registration Tools" (ART) application, formerly known as "Test Registration" (TR).

### shared-web

shared-web is the main web module which will be used by most of the web projects. It contains an implementation related to browser validation, web configuration, exception handling, web security, cookies, taglib etc.

### shared-xml 

shared-xml contains an XML parser and Reader implementation.


## Setup
In general, build the code and deploy the JAR file.