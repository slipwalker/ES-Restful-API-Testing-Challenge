# ES-Restful-API-Testing-Challenge
Small project aiming showing interactions with Elastic Search node through Restful API

Minimun reequirements for machine where tests going to be executed:

   - Docker version should be at least 1.6.0
   - Docker environment should have more than 2GB free disk space
   - Maven should be installed
  
To run the tests just type the following command in the terminal window:

    mvn clean test -P restful_api_tests,regression
  
After tests execution finished in order to see Allure report you need to type the following command:

    mvn site

Report is generated to folder target/allure-results/allure-report and available by name index.html.
