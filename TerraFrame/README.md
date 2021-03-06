# Automation-Frameworks
## TerraFrame (AAV_Framework)
This framework was developed by me as an intern during the summer holidays of 2017.

Setting Up:
1. Copy the project into your workspace.

2. Right click on the project, select Build Path -> Configure Build Path -> Add External JARs.

3. First delete the jar files that have red cross against them.

4. Now add the jar files.


For adding the JAR files:
1. All the jar files required have been bundled with the project in the folder JAR_Files_Terradatum. They can be added from there.


For the drivers:
1. All the drivers have been bundled with the project in the folder named Drivers. They can be added from there.


For excel files:
1. All the headings that are not supposed to be used for testing should be written in CAPITAL letters.

2. Blank entries in excel files are not processed. Hence do not leave any entry in the excel test file as blank, otherwise the program will throw an exception.

3. Any new type of data, if needs to be added to the excel file (other thn username and password), will have to be added in a new sheet. 
Create a new sheet and add the values there. Example: for dropdown values.

4. To read this new sheet in the code, add a new arraylist definition in the code (ControllerLogin) as follows:
		ArrayList<String> sheet1, sheet2; //here sheet1 is already defined for usernames and passwords. sheet2 will be your additional sheet.

5. to get the values in the arraylist sheet2, go inside the function getElements(), and append the following line:
		sheet2 = al.get(1);

6. Similarly, for sheet(n), you have to write al.get(n-1).


For properties file:
1. All the data will be read from the Object Repository file named OR.properties. The only data read from config.properties will be the browser that you want to use.

2. Preferred browser is firefox as it is highly compatible with selenium. However, chrome works fine, but internet explorer has some unresolved issues.

For the code:
1. Any additional code can be added in the class named Workflow.java, or any other class name of your choice, other than ControllerLogin.

2. Doing this will prevent adding too much of code into one class, and will enhance the reusability and readability of the code.

3. Preferred method would be to call the constructor just before the logout() invokation in the controller method as follows:
		new Workflow(WebDriver driver);

4. Inside the constructor definition, you can call other methods as you proceed with automation testing, with the code for testing written inside the particular function.

5. To take screenshots, just call the takeScreenshot function as follows:
		takeScreenshot(WebDriver driver);
6. SQL Injection (SQLi), Cross-site Scripting (XSS), secure code


NOTE:
Make sure you have the folder named "Test Data" in your project, as it will contain the excel file(s) that you want to use.
Screenshot functions encounters problem with alerts. So be careful to call them if an alert is generated by the browser.
If the folders Screenshots and Test Reports do not exist in the project, they will be created automatically.
The class ReadExcel only reads the excel files, and returns the Arraylist(s) containing the sheet(s) of the excel file.

												By: Ashwin A. Vardhan

  
