## RabbitMQ CLI tool
#### 1. Download RabbitMQ (refer the steps from document in Teams)
To access default rabbitMQ http://localhost:15672/

#### 2. application.properties setup to run the jar in cmd 

In the folder where your mqTool.jar is located create application.properties file

Below is the default properties example:

    spring.rabbitmq.username=guest
    spring.rabbitmq.password=guest
    spring.rabbitmq.host=localhost
    spring.rabbitmq.port=5672
    spring.rabbitmq.api.port=15672
    spring.main.web-application-type=NONE

#### 3. Download the private and public keys from https://ts.accenture.com/:f:/r/sites/PracticeProjectMobileAppShoppingList/Shared%20Documents/General/RabbitMQTool%20Documents/keys?csf=1&web=1&e=mFnjBP and place it along with the application.properties file.

In the folder where your mqTool.jar is located create application.properties file
Navigate to the mqTool.jar file folder in command promt then run

	java -jar mqTool.jar

Once program is run, shell:> will promt to write commands

	help             to access the default command library to
	mqtool --help    to access the full library with flags
