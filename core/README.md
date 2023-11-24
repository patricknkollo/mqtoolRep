##  Core Module 
It has the business logics which are coupled with Cli, Shell and Web Modules. 

#### application.properties configuration
Setup application.properties in root of core directory. (# WIP)
Below is the default properties example:

    spring.rabbitmq.username=guest
    spring.rabbitmq.password=guest
    spring.rabbitmq.host=localhost
    spring.rabbitmq.port=5672
    spring.rabbitmq.api.port=15672

#### Download the private and public keys
Download from [here](https://ts.accenture.com/:f:/r/sites/PracticeProjectMobileAppShoppingList/Shared%20Documents/General/RabbitMQTool%20Documents/keys?csf=1&web=1&e=mFnjBP) and place it along with the application.properties file in root folder.

#### package details
1. jwt package has the utilities for generating and refreshing the jwt keys of the RabbitMQ Authorization headers (# US - extracting Roles are in WIP )
2. pojo package has the plain java for service
3. service package has all the methods for service related logics

#### Debug
Debug through JUnit tests 