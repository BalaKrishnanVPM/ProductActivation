package Product_Activation_Queue;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class AccessExcel 
{
	private static final String screenshotpath = new File(System.getProperty("user.dir")).getParent() + "\\Results\\";

	WebElement element = null;
	String req = null;
	public static WebDriverWait wait;

	int checkcount,total = 0;
	int count, countfordp,countfordpstarting, logicdp =0;
	String prdctName,status;
	static int  exitqueue,firstTime= 0;

	readingZPL_File read = new readingZPL_File();
	ArrayList<String> queueCheck = new ArrayList<String>();

	public WebElement getElement(WebDriver driver,Map<String, String> objects,Logger logger) throws InterruptedException
	{
		String selectorType = objects.get("Selectortype");
		String selector = objects.get("Selector");
		String conditions = objects.get("Conditions");
		String keyboard = objects.get("Keyboard");
		String functions = objects.get("Functions");
		String date = date();
		int time=0;
		if(objects.get("Time")!="")
		{
			time = Integer.parseInt(objects.get("Time"));
		}
		String wait = objects.get("Wait");
		try
		{
			if(selectorType.equals("xpath")&& conditions.isEmpty() && keyboard.isEmpty() && functions.isEmpty())
			{
				element = driver.findElement(By.xpath(selector));
			}
			else if(selectorType.equals("xpath")&& wait.contains("wait") && functions.isEmpty())
			{
				eWait(driver, conditions, selector, time);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_enter"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.ENTER);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_down"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.DOWN);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_backspace"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.BACK_SPACE);
			}
			else if(selectorType.equals("xpath")&& keyboard.contains("k_cntrl_A"))
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath(selector)).sendKeys(Keys.chord(Keys.CONTROL,"a"));
			}
			else if(selectorType.equals("xpath")  && functions.contains("is"))
			{
				checkbox_Checking(driver,selectorType, selector,objects, logger);
			}
			else if(selectorType.equals("id"))
			{
				element = driver.findElement(By.id(selector));
			}
			else if(selectorType.equals("xpath") && functions.equals("WaitForQueue"))
			{
				logger.info("Sleeping for 3 minutes.");
				Thread.sleep(180000); 
				logger.info("Sleep Time Over.");
			}
			else if(selectorType.equals("Screenshot"))
			{
				captureScreenshot(driver,objects,date,logger);
			}
			else if (selectorType.equals("") || !functions.isEmpty())
			{
			}
			else
			{
				System.out.println("invalid selector");
				logger.info("invalid selector Provided");
			}
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.error("An Exception has occured in getElement method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
		return element;
	}


	public void doAction(WebDriver driver,WebElement element,Map<String, String> objects,String productname2,String getGroupname, Logger logger) throws InterruptedException, IOException
	{
		String action = objects.get("Action");
		String input = objects.get("Input");
		String selector = objects.get("Selector");
		String key_function = objects.get("Functions");

		List<String> tcdetails = new ArrayList<String>();
		tcdetails = read.readTCDetailsFile(driver, logger);
		String zplno = tcdetails.get(1).toUpperCase();
		String grpname = getGroupname;

		try
		{
			if(action.equals("click"))
			{
				try 
				{
					element.click();
					Thread.sleep(2000);
					infoLog(objects, logger);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
					logger.info("Exception occur in do_action method, in  Click action");
					driver.quit();
					System.exit(0);
				}
			}
			else if(action.equals("sendkeys"))
			{
				element.sendKeys(input);
				infoLog(objects, logger);
			}
			else if(action.equals("sendkeysfordrpdown"))
			{
				Thread.sleep(3000);
				element.sendKeys(input);
			}
			else if(action.contentEquals("ZPLNO"))
			{
				element.sendKeys(zplno);
				infoLog(objects, logger);
			}
			else if(action.contentEquals("GroupName"))
			{
				element.sendKeys(grpname);
			}
			else if(action.contentEquals("ProductName"))
			{
				element.sendKeys(productname2);
			}
			else if(action.equals("compare"))
			{
				WebDriverWait wait1 = new WebDriverWait(driver, 30);
				wait1.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//*[@id='aw-console']/div[3]/div[2]")));
				boolean TxtBoxContent = IsRequiredTextPresent(driver,selector);

				if(TxtBoxContent==false)
				{
					read.createErrorFile(driver, logger);
					logger.info("ZPL number Already Exists !!!!!");
					logger.info("Terminating the script!!!!");
					driver.quit();
					System.exit(0);
				}
			}
			else if(action.equals("Alert"))
			{
				boolean popupdlg = IsPopupDialogPresent(driver,selector);
				if(popupdlg==true)
				{
					element.click();
				}
				infoLog(objects, logger);
				logger.info(" Popup dialog Appearence in the Airwatch Console ==> "+popupdlg);
			}
			else if(action.equals("hover"))
			{
				Thread.sleep(2000);
				//action1.moveToElement(element).build().perform();
			}
			else if(action.equals("getTextBoxValue"))
			{
				String getTextBoxvalue = element.getText();
				getTextBoxvalue.trim();
				logger.info("Added ZPL is find in the Group =>"+getTextBoxvalue);
				if(getTextBoxvalue.contains(zplno))
				{
					logger.info("Checking whether Zpl is added or not in Group,ZPL is matching in the console");
				}
				else
				{
					logger.info("Checking whether Zpl is added or not in Group,ZPL is not matching in the console");
					logger.info("Terminating the Program..");
					driver.quit();
					System.exit(0);
				}
			}
			else if(action.equals("ProductQueueCheck"))
			{
				String getTextvalue = element.getText();
				getTextvalue.trim();
				queueCheck.add(getTextvalue);
			}
			else if(key_function.equals("setdropdown"))
			{
				dynamicDropdown(driver, selector, action, input, zplno, logger);
			}
			else if(key_function.equals("CheckQueue"))
			{
				exitqueue++;
				checkingQueued_Product(driver,productname2,exitqueue,queueCheck,getGroupname,logger);
			}
			else if(key_function.equals("isNewProduct"))
			{
				infoLog(objects, logger);
				if(driver.findElement(By.xpath(".//*[@id='addRule']/input")).isDisplayed())
				{
					logger.info("It is New Product Clicking Add Rules.");
					driver.findElement(By.xpath(".//*[@id='addRule']/input")).click();
				}
				else if(driver.findElement(By.xpath("//a[@class='edit-rules']")).isDisplayed())
				{
					logger.info("It is Existing Product Clicking Edit Rules.");
					driver.findElement(By.xpath("//a[@class='edit-rules']")).click();
				}
			}
			else if(action.isEmpty())
			{
			}
			else
			{
				System.out.println("invalid action");
			}
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.error("An Exception has occured in doAction method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
	}

	public boolean IsRequiredTextPresent(WebDriver driver,String selector)
	{
		try
		{
			driver.findElement(By.xpath(selector));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean IsPopupDialogPresent(WebDriver driver, String selector)
	{
		try
		{
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.findElement(By.xpath("selector"));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public void checkbox_Checking(WebDriver driver,String selectorType,String selector,Map<String, String> objects,Logger logger)
	{
		String func = objects.get("Functions"); 
		String doEnable = objects.get("Functions_do");
		try
		{
			if(func.contentEquals("ischecked"))
			{
				Thread.sleep(2000);
				boolean checkstatus = driver.findElement(By.xpath(selector)).isSelected();
				if(doEnable.contains("uncheck") && checkstatus!=false)
				{
					driver.findElement(By.xpath(selector)).click();
				}
				else if(doEnable.contentEquals("check") && checkstatus!=true)
				{
					driver.findElement(By.xpath(selector)).click();
				}
				else
				{
				}
			}
		}
		catch(Exception e)
		{
			logger.error("There is some problem in ischeckvalue method --> \n"+e.toString());
		}
	}


	public void dynamicDropdown(WebDriver driver,String selector,String action,String input,String zplno,Logger logger)
	{
		try
		{
			if(count<1)
			{
				List<WebElement> xpath = driver.findElements(By.xpath(selector));
				countfordpstarting =xpath.size();
				countfordp = countfordpstarting * 2;

				List<WebElement> xpath1 = driver.findElements(By.xpath("//select[@data-property='LogicalOperator']"));
				logicdp = xpath1.size();
				logicdp = countfordp - 1;
			}
			if((action.contains("Logic")) && (countfordpstarting > 0))
			{
				driver.findElement(By.xpath("//a[@class='add icon logicalOperator']")).click();
				logger.info("Add Logical Operator is Clicked.");

				if(logicdp > 1)
				{
					driver.findElement(By.xpath(".//*[@id='Rule_Steps["+logicdp+"]_LogicalOperator']")).sendKeys(input);
				}
				else
				{
					driver.findElement(By.xpath(".//*[@id='Rule_Steps[1]_LogicalOperator']")).sendKeys(input);
				}
			}
			else if(action.contains("Rule"))
			{
				driver.findElement(By.xpath("//a[@class='add icon ruleStatement']")).click();
				logger.info("Add Rule Button is Clicked.");
			}
			if(!(countfordpstarting >= 1))
			{
				countfordp = 0;
			}

			Thread.sleep(1000);
			if(action.contains("Attribute"))
			{
				driver.findElement(By.xpath(".//*[@id='Rule_Steps["+ countfordp+"]_AttributeId']")).sendKeys(input);
				Thread.sleep(2000);
			}
			else if(action.contains("Operator"))
			{
				driver.findElement(By.xpath(".//*[@id='Rule_Steps["+ countfordp+"]_Operator']")).sendKeys("=");
			}
			else if(action.contains("Value"))
			{
				driver.findElement(By.xpath("//select[@id='Rule_Steps["+ countfordp +"]_Value']")).click();
				driver.findElement(By.xpath("//select[@id='Rule_Steps["+ countfordp +"]_Value']")).sendKeys(zplno);
				driver.findElement(By.xpath("//select[@id='Rule_Steps["+ countfordp +"]_Value']")).sendKeys(Keys.ENTER);
				Thread.sleep(5000);
				Select sel = new Select(driver.findElement(By.xpath("//select[@id='Rule_Steps["+ countfordp +"]_Value']")));
				WebElement option = sel.getFirstSelectedOption();
				String zplcompare = option.getText().trim();
				if(zplcompare.contentEquals(zplno))
				{
					logger.info("Right ZPL is entered into the Edit rules in Product Activation");
				}
				else
				{
					logger.info("Right ZPL is not entered into the Edit rules in Product Activation");
					read.createErrorFile(driver, logger);
					driver.quit();
					System.exit(0);
				}
			}
			count++;
		}
		catch (Exception e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("Problem ocuurs in dynamicDropdown function");
			logger.info("program is terminating...");
			driver.quit();
			System.exit(0);
		}
	}


	public void checkingQueued_Product(WebDriver driver,String productname,int exitqueue,ArrayList<String> queueCheck,String getGroupname, Logger logger) throws IOException
	{
		try
		{
			MasterRun run = new MasterRun();
			logger.info("Checking for queued Product...");

			prdctName = queueCheck.get(0).toLowerCase();
			status = queueCheck.get(1);
			logger.info("ProductName "+prdctName);
			logger.info("Stauts For Queue is "+ status);
			if (prdctName.contentEquals(productname.toLowerCase()) && status.contentEquals("Queued"))
			{
				total = 1;
				read.createSuccessFile(driver, logger);
				logger.info("Succesfully the product is queued and Succes Text file is created...");
			}
			if(total!=1)
			{
				logger.info("Product Queued is not found, Reactivating the Product.");
				run.reactivationExcel(driver,productname,getGroupname,logger);
			}
			if(exitqueue > 3)
			{
				read.createErrorFile(driver, logger);
				logger.info("Queue Product is checked "+ exitqueue + " Times in console,  but it is not queued...");
				logger.info("Error Txt file is created.");
				logger.info("Program is Terminating...");
				driver.quit();
				System.exit(0);
			}
		}
		catch(Exception e)
		{
			read.createErrorFile(driver, logger);
			logger.info("Error in checkingQueued_Product method");
			driver.quit();
			System.exit(0);
		}
	}


	public void eWait(WebDriver driver,String conditions, String selector, int time) 
	{
		wait = new WebDriverWait(driver, time);

		if(conditions.equals("vElement"))
		{
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
		}
		else if(conditions.equals("ivElement"))
		{
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(selector)));
		}
		else
		{
			System.out.println("Invalid wait condition");
		}
	}


	public void captureScreenshot(WebDriver driver, Map<String, String> objects, String time,Logger logger)
	{
		try 
		{   
			List<String> testcaseid = read.readTCDetailsFile(driver, logger);
			String tcID = testcaseid.get(0)+"_Results";

			File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE); 
			FileUtils.copyFile(src, new File(screenshotpath+"\\"+tcID+"/"+"Screen_"+time+".png"));              
		} 
		catch (IOException e)
		{
			read.createErrorFile(driver, logger);
			logger.error("There is some problem in Screen Capturing --> \n"+e.toString());
			driver.quit();
			System.exit(0);
		}
	}


	public void infoLog(Map<String, String> objects,Logger logger)
	{
		String infoLog = objects.get("LoggerInfo");
		if(!infoLog.isEmpty())
		{
			logger.info(infoLog);
		}
	}

	public static String date()
	{
		DateFormat dateFormat = new SimpleDateFormat("hh_mm_ss");
		Date now = new Date();
		String nms_date = dateFormat.format(now);
		return nms_date;
	}


	@Override
	public String toString() 
	{
		return "accessExcel []";
	}

}

