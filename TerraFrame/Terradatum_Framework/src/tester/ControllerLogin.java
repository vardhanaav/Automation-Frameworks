package tester;
/**
 * @author Ashwin A. Vardhan
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ControllerLogin {

	/**
	 * file to read config.properties
	 */
	public Properties config = new Properties();
	/**
	 * file to read OR.properties
	 */
	public Properties object = new Properties();
	
	//file properties
	String filePath = System.getProperty("user.dir")+"\\Test Reports\\";	//setting the path to blank will save it in the current directory.
	String filePathExcel = System.getProperty("user.dir")+"\\Test Data\\";
	String fileName = "";
//	String RUN_DATE = Calendar.getInstance().getTime().toString();
	
	Date date = Calendar.getInstance().getTime();
    DateFormat formatter = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss");
    String today = formatter.format(date);
    String reportName = "index_"+today+".html";   
    
    String filePathScreenshot = System.getProperty("user.dir")+"\\Screenshots\\";
    String directoryScreenshot = "screenshot_"+String.valueOf(formatter.format(Calendar.getInstance().getTime()))+"\\";
    
	//web elements
	String xpathUsername = "";
	String xpathPassword = "";
	String xpathLoginBtn = "";
	String xpathForgotBtn = "";
	String xpathLogoutBtn = "";
	
	String userName = "";
	String passWord = "";
	
	String url = "";
	
	FileWriter fstream =null;
	BufferedWriter out =null;
	
	int k = 0;
	boolean flagLogin = false, flagLogout = false, flagNavigate = false;
	//flag->login | flag1->logout | flag2->navigate
	int numPass = 0, numFail = 0;
	
	
	ArrayList<String> sheet1;
	

	/**
	 * Creates the directories if they are not present. Ignores otherwise
	 * @param list list of the directories to be created.
	 */
	void createDirectories(String...list) {
		for (String file:list) {
			File theDir = new File(System.getProperty("user.dir")+"\\"+file);
			// if the directory does not exist, create it
			if (!theDir.exists()) {
				System.out.println("creating directory: " + theDir.getName());
				boolean result = false;

				try{
					theDir.mkdir();
					result = true;
				} 
				catch(SecurityException se){
					//handle it
					se.printStackTrace();
				}        
				if(result) {    
					System.out.println("DIR created");  
				}
			}
		}

	}
	
	/**
	 * Creates a unique screenshot directory for each testing session 
	 * @param list list of directories to be created
	 */
	void createDirectoryForScreenshots(String...list) {
		for (String file:list) {
			File theDir = new File(System.getProperty("user.dir")+"\\Screenshots\\"+file);
			// if the directory does not exist, create it
			if (!theDir.exists()) {
				System.out.println("creating directory: " + theDir.getName());
				boolean result = false;

				try{
					theDir.mkdir();
					result = true;
				} 
				catch(SecurityException se){
					//handle it
					se.printStackTrace();
				}        
				if(result) {    
					System.out.println("created");  
				}
			}
		}

	}
	
	/**
	 * Sets the preferences such that no (web) pop-ups and (push) notifications are allowed. Exclusive to firefox.
	 * It can also be further used to set preferences according to the users' needs.
	 * Called by default.
	 * @param disable The profile object of the current browsing session.
	 */
	public void setCustomPreferences(FirefoxProfile disable) {
		disable.setPreference("geo.enabled", false);
		disable.setPreference("geo.provider.use_corelocation", false);
		disable.setPreference("geo.prompt.testing", false);
		disable.setPreference("geo.prompt.testing.allow", false);

		disable.setPreference("dom.webnotifications.enabled", false);
		disable.setPreference("dom.push.enabled", false);
		disable.setPreference("dom.push.connection.enabled", false);
		disable.setPreference("network.http.spdy.allow-push", false);
		disable.setPreference("security.insecure_field_warning.contextual.enabled", false);
	}
	
	/**
	 * Loads config.properties and OR.properties into the system
	 * @throws IOException
	 */
	void getConfiguration() throws IOException {
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\config\\config.properties");
		config.load(fis);
//		System.out.println(config);
	
		fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\config\\OR.properties");
		object.load(fis);
//		System.out.println(object);
	}
	
	/**
	 * Assigns the data members according to the .properties files
	 */
	void setConfiguration() {
		xpathUsername = object.getProperty("username");
		xpathPassword = object.getProperty("password");
		xpathLoginBtn = object.getProperty("login");
		xpathForgotBtn = object.getProperty("forgot_password");
		xpathLogoutBtn = object.getProperty("logout");
		url = object.getProperty("testsite");
		fileName = object.getProperty("testfile");
//		System.out.println(xpathForgotBtn+"\n"+xpathLoginBtn+"\n"+xpathLogoutBtn+"\n"+xpathPassword+"\n"+xpathUsername);
	}
	
	/**
	 * Reads the excel files and stores them into ArrayLists
	 */
	public void getElements() {
		ReadExcel reader = new ReadExcel();
		ArrayList<ArrayList<String>> al = new ArrayList<>();
		al = reader.readFile(filePathExcel, fileName);
		
		sheet1 = new ArrayList<>();
		sheet1 = al.get(0);
//		System.out.println(sheet1);
		
	}
	
	/**
	 * Function that controls the workflow of the project
	 * @param disable profile that has all non-essential settings disabled
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void controller(FirefoxProfile disable) throws InterruptedException, IOException {
		
		createReportFile();

		int limit1 = sheet1.size();

		while (k < limit1) {
			flagNavigate = false;
			WebDriver driver = null;
			if (config.getProperty("browser").equalsIgnoreCase("firefox")) {
				driver = new FirefoxDriver(disable);
			} else if (config.getProperty("browser").equalsIgnoreCase("chrome")) {
				driver = new ChromeDriver();
				driver.manage().window().maximize();
			} else if (config.getProperty("browser").equalsIgnoreCase("ie") 
					|| config.getProperty("browser").equalsIgnoreCase("internet explorer") 
					|| config.getProperty("browser").equalsIgnoreCase("edge")) {
				driver = new InternetExplorerDriver();
				driver.manage().window().maximize();
			}
			driver.get(url);
			Thread.sleep(450);
			try {					
				WebElement element = driver.findElement(By.xpath(xpathLoginBtn));
				//navigate flag
				flagNavigate = true;
				numPass++;
				System.out.println("Navigation success");
				takeScreenshot(driver);
			} catch (Exception e) {
				flagNavigate = false;
				numFail++;
			}
			flagLogin = false;
			flagLogout = false;

			userName = sheet1.get(k);
			passWord = sheet1.get(k+1);

			login(driver);
			if (flagLogin) {
//				new Workflow(driver);		//This constructor call will work further aftyer login.
				logout(driver);
			} else {
				numFail++;
			}
			Thread.sleep(1200);
//			takeScreenshot(driver);
			driver.close();			
			k += 2;
			generateReport();
		}

		appendToReportFile();
	}		
	
	/**
	 * Writes the test case statistics in the html report file 
	 * @throws IOException
	 */
	private void appendToReportFile() throws IOException {
		// TODO Auto-generated method stub
		out.newLine();
		out.write("<table  border=1 cellspacing=1 cellpadding=1 >\n");
		out.write("<tr>\n");
		out.write("<br>");

		out.write("<td width=150 align= left  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Total Test Cases</b></td>\n");
		out.write("<td width=150 align= left ><FONT COLOR=black FACE= Arial  SIZE=2.75><b>"+String.valueOf((numPass+numFail))+"</b></td>\n");
		out.write("</tr>\n");

		out.write("<td width=150 align= left  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Passed Test Cases</b></td>\n");
		out.write("<td width=150 align= left ><FONT COLOR=black FACE= Arial  SIZE=2.75><b>"+String.valueOf(numPass)+"</b></td>\n");
		out.write("</tr>\n");

		out.write("<td width=150 align= left  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2.75><b>Failed Test Cases</b></td>\n");
		out.write("<td width=150 align= left ><FONT COLOR=black FACE= Arial  SIZE=2.75><b>"+String.valueOf(numFail)+"</b></td>\n");
		out.write("</tr>\n");

		out.write("</table>\n");
		out.close();
		
	}

	/**
	 * Creates an html report file
	 */
	private void createReportFile() {
		// TODO Auto-generated method stub
		try{
			// Create file 

			fstream = new FileWriter(filePath+reportName);
			out = new BufferedWriter(fstream);			

			out.newLine();

			out.write("<html>\n");
			out.write("<HEAD>\n");
			out.write(" <TITLE>Vardhan Inc. Framework</TITLE>\n");
			out.write("</HEAD>\n");

			out.write("<body>\n");


			out.write("<table  border=0 cellspacing=0 cellpadding=0 >\n");
			out.write("<tr>\n");

			out.write("<td width=150 align=left><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2.75> <img src='cm_logo.png' align=right></img></td>\n"); 


			out.write("</tr>\n");

			out.write("</table>\n");
			out.write("<h2 align=center><FONT COLOR=010101 FACE=AriaL SIZE=9><b>Test Results</b></h2>\n");


			out.write("<table  border=1 cellspacing=1 cellpadding=1 >\n");
			out.write("<tr>\n");

			out.write("<h4> <FONT COLOR=black FACE=Arial SIZE=4.5> <u>Test Details</u>:-</h4>\n");
			out.write("<td width=140 align=left bgcolor=blue><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2.75><b>Run Date</b></td>\n");
			out.write("<td width=200 align=left><FONT COLOR=black FACE=Arial SIZE=2.75><b>"+Calendar.getInstance().getTime().toString()+"</b></td>\n");
			out.write("</tr>\n");
			
			out.write("<tr>\n");
//			out.write("<h4> <FONT COLOR=black FACE=Arial SIZE=4.5> <u>Test Details</u>:-</h4>\n");
			out.write("<td width=140 align=left bgcolor=blue><FONT COLOR=#E0E0E0 FACE=Arial SIZE=2.75><b>Test Site</b></td>\n");
			out.write("<td width=200 align=left><FONT COLOR=black FACE=Arial SIZE=2.75><b>"+url+"</b></td>\n");
			out.write("</tr>\n");
			
			out.write("</table>\n");
			out.write("<br>");
			out.newLine();

		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}

	/**
	 * Adds test case details in the html report file
	 * @throws IOException
	 */
	private void generateReport() throws IOException {
		int temp = 1;
		// TODO Auto-generated method stub
//		out.write("<h4> <FONT COLOR=black FACE= Arial  SIZE=4.5> <u>"+"THIS IS TEST STRING"+" Report :</u></h4>\n");
//		out.newLine();
		out.write("<br>");
		out.write("<table  border=1 cellspacing=1 cellpadding=1 width=100%>\n");
		out.write("<tr>\n");		
		out.write("<td width=10%  align= center  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Test Script "+String.valueOf(k/2)+"</b></td>\n");		
		
		out.write("<td width=40% align= center  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Test Case</b></td>\n");
//		out.write("<td width=12% align= center  bgcolor=red><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Password</b></td>\n");
		out.write("<td width=10% align= center  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Status</b></td>\n");
		out.write("<td width=20% align= center  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Run Start Timestamp</b></td>\n");
		out.write("<td width=20% align= center  bgcolor=blue><FONT COLOR=#E0E0E0 FACE= Arial  SIZE=2><b>Run End Timestamp</b></td>\n");
		out.write("</tr>\n");
		
		out.write("<tr>\n");
		out.write("<td width=10% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+String.valueOf(temp)+"</b></td>\n");
		out.write("<td width=40% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>Navigate</b></td>\n");
//		out.write("<td width=12% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+passWord+"</b></td>\n");
		if (flagNavigate)
			out.write("<td width=10% align= center><FONT COLOR=green FACE= Arial  SIZE=2><b>Passed</b></td>\n");
		else 
			out.write("<td width=10% align= center><FONT COLOR=red FACE= Arial  SIZE=2><b>Failed</b></td>\n");
		out.write("<td width=20% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+Calendar.getInstance().getTime().toString()+"</b></td>\n");
		out.write("<td width=20% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+Calendar.getInstance().getTime().toString()+"</b></td>\n");
		out.write("</tr>\n");
		temp++;
		
		out.write("<tr>\n");
		out.write("<td width=10% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+String.valueOf(temp)+"</b></td>\n");
		out.write("<td width=40% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>Login</b></td>\n");
//		out.write("<td width=12% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+passWord+"</b></td>\n");
		if (flagLogin)
			out.write("<td width=10% align= center><FONT COLOR=green FACE= Arial  SIZE=2><b>Passed</b></td>\n");
		else 
			out.write("<td width=10% align= center><FONT COLOR=red FACE= Arial  SIZE=2><b>Failed</b></td>\n");
		out.write("<td width=20% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+Calendar.getInstance().getTime().toString()+"</b></td>\n");
		out.write("<td width=20% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+Calendar.getInstance().getTime().toString()+"</b></td>\n");
		out.write("</tr>\n");
		temp++;
		
		out.write("<tr>\n");
		out.write("<td width=10% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+String.valueOf(temp)+"</b></td>\n");
		out.write("<td width=40% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>Logout</b></td>\n");
//		out.write("<td width=12% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+passWord+"</b></td>\n");
		if (flagLogout)
			out.write("<td width=10% align= center><FONT COLOR=green FACE= Arial  SIZE=2><b>Passed</b></td>\n");
		else 
			out.write("<td width=10% align= center><FONT COLOR=red FACE= Arial  SIZE=2><b>Failed</b></td>\n");
		out.write("<td width=20% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+Calendar.getInstance().getTime().toString()+"</b></td>\n");
		out.write("<td width=20% align= center><FONT COLOR=black FACE= Arial  SIZE=2><b>"+Calendar.getInstance().getTime().toString()+"</b></td>\n");
		out.write("</tr>\n");
		
		out.write("</table>\n");	
		
	}

	/**
	 * Display the current statistics
	 */
	public void displayStats() {
		System.out.println("=====================");
		System.out.println("Username : "+userName);
		System.out.println("Password : "+passWord);
		System.out.println("Total Test Cases Passed : "+numPass);
		System.out.println("Total Test Cases Failed : "+numFail);
		System.out.println("=====================");
	}
	
	/**
	 * Function that logs into a website.
	 * @param driver Current web driver
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void login(WebDriver driver) throws InterruptedException, IOException {

			driver.findElement(By.xpath(xpathUsername)).sendKeys(userName);
			driver.findElement(By.xpath(xpathPassword)).sendKeys(passWord);
			takeScreenshot(driver);
			driver.findElement(By.xpath(xpathLoginBtn)).click();
//			driver.findElement(By.xpath(xpathForgotBtn)).click(); //working fine, don't worry!
			
			try {
				WebDriverWait wait = new WebDriverWait(driver, 5);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathLogoutBtn)));
				WebElement element = driver.findElement(By.xpath(xpathLogoutBtn));				
				// if found
				numPass++;
				//login flag
				flagLogin = true;
				System.out.print("Logging in");
				for (int i=1; i<=3; i++) {
					Thread.sleep(550);
					System.out.print(".");
				} 
				System.out.println();
				System.out.println("Success!");
//				displayStats();
				Thread.sleep(850);
				takeScreenshot(driver);
			} catch(Exception e) {
				flagLogin = false;
				numFail++;
				System.out.println("Login Failed");
				takeScreenshot(driver);
//				displayStats();
//				e.printStackTrace();
			}
	}
	
	/**
	 * Logs out of the website.
	 * @param driver Current web driver
	 * @throws InterruptedException
	 * @throws IOException
	 */
	void logout(WebDriver driver) throws InterruptedException, IOException {		
		driver.findElement(By.xpath(xpathLogoutBtn)).click();
		Thread.sleep(450);
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathLoginBtn)));
			WebElement element = driver.findElement(By.xpath(xpathLoginBtn));
			flagLogout = true;
			numPass++;
			System.out.print("Logging out");
			for (int i=1; i<=3; i++) {
				Thread.sleep(550);
				System.out.print(".");
			}
			System.out.println();
			System.out.println("Success!");
			takeScreenshot(driver);
		} catch (Exception e) {
			// TODO: handle exception
			flagLogout = false;
			numFail++;
			System.out.println("Logout Failed");
			takeScreenshot(driver);
		}
	}
	
	/**
	 * Take a screenshot at any point
	 * @param driver Current web driver
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void takeScreenshot(WebDriver driver) throws IOException, InterruptedException {

//		driver.get(url);
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		//		Copy to .png
		FileUtils.copyFile(scrFile, new File(filePathScreenshot+directoryScreenshot+"screenshot_"+String.valueOf(formatter.format(Calendar.getInstance().getTime()))+".png"));
		System.out.println("Screenshot taken!!!");
		Thread.sleep(1000);
	}
	
	/**
	 * The master function that runs it all.
	 * @param args command line arguments. Not applicable.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		ControllerLogin tester = new ControllerLogin();

		FirefoxProfile disable = new FirefoxProfile();
		tester.setCustomPreferences(disable);
		
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\Drivers\\chromedriver.exe");
		System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"\\Drivers\\geckodriver.exe");
		System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\Drivers\\IEDriverServer.exe");
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability("marionette", true);		
		capabilities.setCapability(FirefoxDriver.PROFILE, disable);
		
		tester.createDirectories("Screenshots", "Test Reports");
		tester.createDirectoryForScreenshots(tester.directoryScreenshot);
		tester.getConfiguration();
		tester.setConfiguration();
		tester.getElements();
		tester.controller(disable);

	}
	
}
