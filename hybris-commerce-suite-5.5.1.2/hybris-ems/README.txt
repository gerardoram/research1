hybris Entitlement & Metering Service (EMS) Version 5.3 14/08/2014 - Installation Guide
=================================================================

The Entitlement & Metering Service is based on the Core+ technology, hence it requires several configuration options before it can be first used. This document shows the steps you have to follow in order to install the Entitlement & Metering Service on Windows.

Note:
	This document uses the following terms:
    {HYBRIS_HOME} to refer to the directory where you unzipped the hybris Commerce Suite package
    {E&M_HOME} to refer to the {HYBRIS_HOME}\hybris-ems directory that holds the following folders: binary, documentation and source
	{TOMCAT_INSTALLATION_DIRECTORY} refers to {HYBRIS_HOME}\hybris\bin\platform\tomcat 
	
Prerequisites:
--------------
The Entitlement & Metering Service requires several pieces of software to fully function:

--> Oracle Java JDK 7
--> Apache Tomcat 7 (embedded in platform already)
--> MongoDB 2.4.6 or higher



Build the Entitlement & Metering Server: Using an Out-of-the-Box .war File
---------------------------------------------------------------------------
Customers can use an out-of-the-box .war file provided with the Entitlement & Metering Service package. This approach does not involve installing and configuring Maven, and generating a .war file. Hence, this approach is aimed at customers who do not want to extend the E&M Service. In order to use the .war file delivered with the E&M Service package, perform the following steps:
1. Make sure that you have installed software listed in prerequisites.
2. Download the latest version of hybris Commerce Suite from the Download page and unzip the file. (https://wiki.hybris.com/display/downloads/Download)
3. Navigate to the {HYBRIS_HOME}\hybris\sampleconfigurations\ems_properties directory and copy the com.hybris.services.entitlements_entitlements-web.properties and com.hybris.services.entitlements_entitlements-web-logback.xml files to the {TOMCAT_INSTALLATION_DIRECTORY}\lib folder.
4. Navigate to the {E&M_HOME}\binary directory and copy the .war file to the {TOMCAT_INSTALLATION_DIRECTORY}\webapps folder.
5. Follow the instructions in the Starting the Entitlement & Metering Service section below for:
5.1. Start the Mongo Database
5.2. Start Apache Tomcat  
5.3. Start the Entitlement & Metering Server
5.4. Initialize the Entitlement & Metering Service



Embed the Entitlement & Metering Server .war File in Tomcat Shipped with hybris Commerce Suite
----------------------------------------------------------------------------------------------
With the 5.3 release, the Entitlement & Metering Server can run on the instance of Apache Tomcat shipped with the hybris Commerce Suite and start up with the platform simultaneously. In order to embed the .war file in your platform, follow the steps below:
1. Make sure that you have installed software listed in prerequisites.
2. Download the latest version of hybris Commerce Suite from the Download page and unzip the file. (https://wiki.hybris.com/display/downloads/Download)
3. Navigate to the {HYBRIS_HOME}\hybris\sampleconfigurations directory and copy the content of the telco_acc+sbg+ems_extensions.xml or telco_acc_atdd+sbg+ems_extensions.xml file to the {HYBRIS_CONFIG_DIR}\localextensions.xml file. 
4. Navigate to the {HYBRIS_HOME}\hybris\sampleconfigurations and copy the content of the telco_acc+sbg+ems.properties or telco_acc_atdd+sbg+ems.properties file to the {HYBRIS_CONFIG_DIR}\local.properties file. Adjust the properties if needed.
5. Navigate to the {HYBRIS_HOME}\hybris\sampleconfigurations\ems_properties directory and copy the com.hybris.services.entitlements_entitlements-web.properties and com.hybris.services.entitlements_entitlements-web-logback.xml files to the {TOMCAT_INSTALLATION_DIRECTORY}\lib directory.
6. Follow the instructions in the Starting the Entitlement & Metering Service section below for:
6.1. Start the Mongo Database
6.2. Start Platform
6.3. Initialize the Entitlement & Metering Service



Build the Entitlement & Metering Server: Using a Maven Built .war File
----------------------------------------------------------------------
If you plan on extending the Entitlement & Metering Service, you can build the E&M Server by generating a .war file required to install the server. In order to generate the file, follow the steps below:
1. Make sure that you have installed software listed in prerequisites.
2. Download the latest version of hybris Commerce Suite from the Download page and unzip the file. (https://wiki.hybris.com/display/downloads/Download)
3. Download the latest Apache Maven from the http://maven.apache.org/download.cgi page and install the software. 
4. After you installed Apache Maven, set the environment parameters according to the Installation Instructions section on the http://maven.apache.org/download.cgi.
5. Open the command prompt window and navigate to the following directory: {E&M_HOME}\source.
6. Run the mvn install -DskipTests -Dmaven.repo.local=..\..\hybris-dependencies -o command from within the directory. Running this command results in creating a .war file.
NOTE: If you receive an out of memory error during build, add -XX:MaxPermSize=512M to the MAVEN_OPTS environment variable.
7. You can now find the .war file in the {E&M_HOME}\source\entitlements\entitlements-web\target directory.
8. Copy the .war file and copy the file to the {TOMCAT_INSTALLATION_DIRECTORY}\webapps folder .
NOTE: If you deploy a new Entitlement & Metering Service .war file to an instance of Apache Tomcat that have previously had Entitlement & Metering Service deployed, make sure to delete the old Entitlement & Metering Service folders from the {TOMCAT_INSTALLATION_DIRECTORY}\work\Catalina\localhost and {TOMCAT_INSTALLATION_DIRECTORY}\webapps directories.
9. Navigate to the {HYBRIS_HOME}\hybris\sampleconfigurations\ems_properties directory and copy the com.hybris.services.entitlements_entitlements-web.properties and com.hybris.services.entitlements_entitlements-web-logback.xml files to the {TOMCAT_INSTALLATION_DIRECTORY}\lib folder. Edit these files if you want to change any of the properties.
10. Follow the instructions in the Starting the Entitlement & Metering Service section below for:
10.1. Start the Mongo Database
10.2. Start Apache Tomcat  
10.3. Start the Entitlement & Metering Server
10.4. Initialize the Entitlement & Metering Service



Starting the Entitlement & Metering Service
-------------------------------------------
Start the Mongo Database:
1. Navigate to the {MONGODB_INSTALLATION_DIRECTORY}\bin folder.
2. Using the Command Prompt, run the mongod.exe --dbpath= {DATABASE_DIRECTORY} command, for example mongod.exe --dbpath=C:\HybrisSuite\mongo\bin\enbtitlementdb.
NOTE: If you create a new database, the {DATABASE_DIRECTORY} path has to point to an already existing folder.


Start Apache Tomcat:
1. For Tomcat Embedded in Platform:
1.1. Navigate to the {TOMCAT_INSTALLATION_DIRECTORY}\bin folder, run the startup.bat batch file, and wait for the server to start.
2. For Tomcat Instance not Platform Embedded - see manual for detailed instructions


Start the Entitlement & Metering Server:
1. In your browser, go to the http://localhost:8080/manager address. Log in as user root with password root.
2. In the entitlements-web row, press the Start button if the Entitlement & Metering Server is not started already.


Start Platform:
1. Build
1.1. Open a command interpreter window in Microsoft Windows (shell in Unix family systems) and navigate to the bin/platform directory.
	--> On Microsoft Windows systems, run the setantenv.bat file by entering setantenv.bat. Do not close the command window.
	--> On Unix-related systems (such as Linux or Mac OS X), run setantenv.sh by entering ./setantenv.sh. Do not close the command window.
1.2 Enter ant clean all. You are prompted to select a configuration template; this is a pre-defined configuration set.
1.3 Press the Enter key to use the default, the develop configuration template.
1.4 Still in the command prompt, run the ant clean all initialize command.
2. Start hybris server, enter hybrisserver.bat in the command prompt.


Initialize the Entitlement & Metering Service
1. On the machine where the Entitlement & Metering Service is installed, open the main Init-App console, for example: http://localhost:9001/entitlements-web/init-app-web/console/main. The Init-App Web Console displays.
2. In the Init-App Web Console, click the Initialize System button. When the system initialization is finished successfully, you receive the message Server Returned Response: ok!
3. In the Tenant Name field, type in single as the tenant name and tick the Project data checkbox.
4. Click the Initialize Schema button. When the schema initialization is finished successfully, you receive the message Server Returned Response: ok!


Accessing the Embedded Documentation
------------------------------------
You can find the following documentation in the {E&M_HOME} directory:
	--> Java API Doc: {E&M_HOME}\documentation\apidocs\index.html 
	--> REST Documentation: {E&M_HOME}\documentation\apidocs\entitlements-rest-webapp\enunciate\index.html