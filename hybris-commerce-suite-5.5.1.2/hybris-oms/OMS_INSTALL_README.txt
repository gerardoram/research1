Installing the hybris Commerce Suite and Order Management Services (OMS)

These installation instructions how to install the Commerce Suite and OMS.



PRE-INSTALLATION INSTRUCTIONS
*****************************

Databases and Third-Party Software
**********************************

Prior to installing the OMS with the hybris Commerce Suite, make sure that you have installed and set up the following:
* MySQL database 5.6 or higher, see www.mysql.com OR
  Oracle database 11g or higher, see www.oracle.com
* MongoDB 2.4 or higher, see www.mongodb.org
* JRE/JDK 7.0 or higher, see www.java.com
* Apache Tomcat 7.0, see tomcat.apache.org
  NOTE: An Apached Tomcat is bundled in the hybris Commerce Suite installation package. If you are using OMS in an development environment, this 
  Tomcat is sufficient. If you are using OMS in a production environment, hybris suggests that you download and install an additional Tomcat.


System Requirements
*******************

For system requirements, see https://wiki.hybris.com/display/general/System+Requirements+-+Release+5.5

Directory Structure of the hybris Commerce Suite Installation File
******************************************************************

You have already downloaded and unzipped the Commerce Suite installation zip file. The unzipped file should have the following
directory structure:
- hybris
	- bin
	   - ext-accelerator
	   - ext-addon
	   - ext-channel
	   - ext-commerce
	   - ext-content
	   - ext-data
	   - ext-platform
	   - ext-platform-optional
	   - ext-template
	   - platform
- hybris-dependencies
- hybris-ems
- hybris-Mobile-Apps-SDK
- hybris-oms
- hybris-sbg
- licenses




INSTALLING THE HYBRIS COMMERCE SUITE with OMS
*********************************************

To install the hybris Commerce Suite with OMS:

1. Open a command window or shell.

2. In \platform\tomcat, create a directory named webapps–if it does not already exist.

3. Copy the \hybris-oms\binary\webapp\oms-rest-webapp.war to \platform\tomcat\webapps directory.

4. Copy the \hybris-oms\tools\dataonboarding\binary\webapp\dataonboarding-webapp.war to \platform\tomcat\webapps directory.

5. Copy the following four files from \hybris-oms\sample-config to \platform\tomcat\lib directory:
	* com.hybris.dataonboarding_dataonboarding-webapp.properties
	* com.hybris.dataonboarding_dataonboarding-webapp-logback.xml
	* com.hybris.oms_oms-res-webapp.properties
	* com.hybris.oms_oms-res-webapp-logback.xml

6. Go to \platform directory.

7. Set up Apache Ant by entering the following command:
	* For MS Windows systems: setantenv.bat
	* For Unix systems: . ./setantenv.sh

8. Build the hybris Commerce Suite by entering the command: ant clean all.

9. Specify a configuration template for your operation environment: development or production. 
   * development: This configuration template is optimized for use in a development environment, where the hybris Commerce
                  Suite is used internally only. 
   * production: This configuration template is optimized for use in a production environment, where the hybris Commerce Suite is 
	         available to the entire Web.


10. Go to the \hybris\sampleconfigurations directory.
    This directory contains the sample extensions and properties files for the various hybris modules that you can 
    deploy with the Commerce Suite. For OMS, you require the following files:
	* b2c_acc+cis+oms.properties
	* b2c_acc+cis+oms_extensions.xml

11. Copy and rename the files as follows:
	* b2c_acc+cis+oms_extensions.xml   to  \hybris\config\localextensions.xml
	* b2c_acc+cis+oms.properties     to   \hybris\config\local.properties

    Note: 
		Default localextensions.xml and local.properties files already exist in the \hybris\config directory. 
		You can choose to overwrite them or rename them, if you want to keep them.
		
		
12. Edit the local.properties file and update url for oms and dob. For example:
    * If you are deploying OMS to the hybris Tomcat: 
		### OMS configuration
		oms.client.endpoint.uri=http://localhost:9001/oms-rest-webapp/webresources
 
		### data onboarding configuration
		dataonboarding.client.endpoint.uri=http://localhost:9001/dataonboarding-webapp/webresources


13. Edit the localextensions.xml file and add the path to the OMS and Data Onboarding (DoB) war files to the end of the 
    localextensions.xml file before </extensions> as follows:

	< !--  .war files  -->
	<webapp contextroot="oms-rest-webapp" path="<PATH_TO_WAR>/oms-rest-webapp.war" />
 	<webapp contextroot="dataonboarding-webapp" path="<PATH_TO_WAR>/dataonboarding-webapp.war" />

	For example:
	<!-- .war file -->
	<webapp contextroot="oms-rest-webapp" path="/platform/tomcat/webapps/oms-rest-webapp.war" ></webapp>
    <webapp contextroot="dataonboarding-webapp" path="/platform/tomcat/webapps/dataonboarding-webapp.war" ></webapp>
	
14. Edit the \platform\tomcat\lib\com.hybris-oms_oms-rest-webapp.properties file and change 
    the property kernel.autoInitMode from ignore to update, for example:
    kernel.autoInitMode=update
	
	
15. If necessary, edit the JDBC information for the database you are using in the 
    \platform\tomcat\lib\com.hybris.oms_oms-rest-webapp.properties file.
	The default configuration for OMS is a JDBC driver for a MySQL database, for example:
	
		##################### JDBC #####################################################
		# Fully qualified name of the JDBC driver class used to access the database
		#
		dataSource.className=com.mysql.jdbc.jdbc2.optional.MysqlDataSource
		#
		# JDBC connection URL
		#
		dataSource.jdbcUrl=jdbc:mysql://localhost:3306/omsext?useConfigs=maxPerformance&characterEncoding=utf8&sessionVariables=storage_engine=InnoDB&zeroDateTimeBehavior=convertToNull
		# Username to use for connection
		#
		dataSource.username=root
		#
		# Password to use for connection
		#
		dataSource.password=password
		#
		# Database type to be used by Activiti
		# Supported values: h2, mysql, oracle, mssql
		#
		oms.activiti.databaseType=mysql
		#
		
		
	Provide the JDBC driver information for the database that you are using. The JDBC driver information for the 
	HSQL database provided as the default database for the hybris Commerce Suite is as follows:
	
		##################### JDBC #####################################################
		# Fully qualified name of the JDBC driver class used to access the database
		#
		dataSource.driverClass=org.hsqldb.jdbcDriver
		#
		# JDBC connection URL
		#
		dataSource.jdbcUrl=jdbc:hsqldb:file:data/hsql.db;shutdown=true;hsqldb.cache_rows=100000;hsqldb.cache_size=20000;hsqldb.write_delay_millis=1000;hsqldb.inc_backup=true;hsqldb.defrag_limit=2;hsqldb.nio_data_file=false
		# Username to use for connection
		#
		dataSource.username=sa
		#
		# Password to use for connection
		#
		dataSource.password=
		#
		# Database type to be used by Activiti
		# Supported values: h2, mysql, oracle, mssql
		#
		oms.activiti.databaseType=h2

Note: when using mysql, mysql-connector-java--bin.jar need to be download manually from http://dev.mysql.com/downloads/connector/j/ and copy into the platform directory: \platform\lib\dbdriver
	

16. Rebuild the Commerce Suite: go to the \platform directory and enter the command: ant all.

17. In a new command window or shell, start MongoDB, for example: C:\mongodb\bin\mongod
    Note: You must keep the command window or shell open when running the OMS. If you close the command window or shell, you will terminate the MongoDB and OMS will not run.

18. Start the hybris server:
	* On  Windows systems: hybrisserver.bat
	* On Unix systems: ./hybrisserver.sh

19. Initialize the hybris Commerce Suite:
	a. In a browser, open the hybris Administration Console (hAC). The default URL for thje hAC is http://localhost:9001.
	b. Enter a user name and password.
	   The default values are admin and nimda respectively.
	   Note: No need to enter password when it's the first initialization
	c. Select Platform > Initialization.
	d. Click the Initialization button.
	e. When the initialization process ends, click the Continue link.
	f. If your browser fails to dipslay information about the end of the initialization process, check the output of your command or shell 
 	   output. If it displays that the initialization has finished, refresh your browser.

The installation of the hybris Commerce Suite and OMS is now complete. You must now initialize OMS and DoB. See the following sections.



Initializing the OMS
********************

 To create an initial tenant and tenant data in the OMS:  
 
1. Open the main console of the Init App on the machine where the OMS is installed. For example:
   * If you are deploying OMS to the hybris Tomcat: http://<oms_host>:9001/oms-rest-webapp/init-app-web/console/main.
   * If you are deploying OMS to a separate Tomcat: http://<oms_host>:8080/oms-rest-webapp/init-app-web/console/main
   The Init-App Web Console for OMS is displayed.
   
2. Click the Initialize System button. 
   
3. A pop-up window appears informing you that your data might be deleted. Click OK to dismiss the window.
     
4. In the line that contains the Tenant Name field, Project Data check box, and Initialize Schema button, do the following:
   a. In the Tenant Name field, type in single as the tenant name.
   b. Click the Project Data check box.
   c. Click the Initialize Schema button.
   d. A pop-up window appears informing you that your data might be deleted. Click OK to dismiss the window.
   
 If the initialization of the schema is successful, you receive the message: Server Returned Response: ok! 
 
 
 
 Initializing the DoB
 ********************
 
 To initialize the OMS tenant in DoB: 
 
 1. Open the main console of the Init App on the machine where the DoB is installed. For example:
    * If you are deploying OMS to the hybris Tomcat: http://<dob_host>:9001/dataonboarding-webapp/init-app-web/console/main.
    * If you are deploying OMS to a separate Tomcat: http://<dob_host>:8080/dataonboarding-webapp/init-app-web/console/main.
    The Init-App Web Console for DoB is displayed.
	
2. Click the Initialize System button.

3. In the line that contains the Tenant Name field, Project Data check box, and Initialize Schema button, do the following: 
   a. In the Tenant Name field, type in single as the tenant name.
   b. Click the Project Data check box.
   c. Click the Initialize Schema button.
   d. A pop-up window appears informing you that your data might be deleted. Click OK to dismiss the window.

 You have initialized the tenant in the DoB. At this point of the installation, your OMS and DoB web applications 
 are deployed and the tenant single is configured in OMS and DoB. 
 You must now upload inventory and stockroom location data for the OMS. For detailed instructions, see the Installing Order Management Services in the hybris documentation. 




