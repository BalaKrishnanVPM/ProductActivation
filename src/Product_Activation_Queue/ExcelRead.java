package Product_Activation_Queue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelRead 
{
	public ArrayList<Map<String,String>> linklist = new ArrayList<Map<String,String>>();
	public ArrayList<Map<String,String>> loginPagelist = new ArrayList<Map<String,String>>();
	public ArrayList<Map<String,String>> activationlist = new ArrayList<Map<String,String>>();
	public ArrayList<Map<String,String>> reactivationlist = new ArrayList<Map<String,String>>();
	
	String TCFilePath = new File(System.getProperty("user.dir")).getParent() + "\\TC_Data.xls";
	String productactivationsheet = new File(System.getProperty("user.dir")).getParent() + "\\ExcelData\\ProductActivation.xls";
	
	public static List<String> id;
	List<String> testcaseid;
	String tcID = null;
	
	readingZPL_File read = new readingZPL_File();
	ArrayList<String> sheetname = new ArrayList<String>();
	LinkedHashMap<String, String> sheetmap = new LinkedHashMap<>();
	
	public LinkedHashMap<String,String> productGroupNameMap(WebDriver driver,Logger logger) throws IOException, BiffException
	{
		try 
		{
			sheetmap = new LinkedHashMap<>();
			readingZPL_File read = new readingZPL_File();
			testcaseid = read.readTCDetailsFile(driver, logger);
			tcID = testcaseid.get(0);
			
			FileInputStream fs = new FileInputStream(TCFilePath);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(0);
			
			for(int row=1;row<sh.getRows();row++)
			{
				if(sh.getCell(0, row).getContents().contentEquals(tcID))
				{
					for(int col=1;col<3;col++)
					{
						sheetmap.put(sh.getCell(col, 0).getContents(), sh.getCell(col, row).getContents());
					}
				}
			}
		} 
		catch (IOException e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("An exception has occured in productGroupNameMap method --> "+e.toString());
			driver.quit();
			System.exit(0);
		}
		return sheetmap;
	}
	
	
	public ArrayList<Map<String,String>> linkDataSheet(WebDriver driver,Logger logger) throws BiffException
	{
		Map<String,String> linkmap ;
		try 
		{
			FileInputStream fs = new FileInputStream(productactivationsheet);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(0);
			for(int row=1;row<sh.getRows();row++)
			{
				linkmap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					linkmap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				linklist.add(linkmap);
			}
		} 
		catch (IOException e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("Error in linkDataSheet Method");
			driver.quit();
			System.exit(0);
		}
		return linklist;
	}
	
	
	public ArrayList<Map<String,String>> loginPageDataSheet(WebDriver driver,Logger logger) throws BiffException
	{
		Map<String,String> activationmap ;
		try 
		{
			FileInputStream fs = new FileInputStream(productactivationsheet);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(1);
			for(int row=1;row<sh.getRows();row++)
			{
				activationmap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					activationmap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				loginPagelist.add(activationmap);
			}
		} 
		catch (IOException e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("Error in reactivationDatasheet Method");
			driver.quit();
			System.exit(0);
		}
		return loginPagelist;
	}
	
	
	public ArrayList<Map<String,String>> activationDataSheet(WebDriver driver,Logger logger) throws BiffException
	{
		Map<String,String> activationmap ;
		try 
		{
			FileInputStream fs = new FileInputStream(productactivationsheet);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(2);
			for(int row=1;row<sh.getRows();row++)
			{
				activationmap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					activationmap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				activationlist.add(activationmap);
			}
		} 
		catch (IOException e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("Error in reactivationDatasheet Method");
			driver.quit();
			System.exit(0);
		}
		return activationlist;
	}
	
	
	
	public ArrayList<Map<String,String>> reactivationDataSheet(WebDriver driver,Logger logger) throws BiffException
	{
		Map<String,String> reactivationmap ;
		try 
		{
			FileInputStream fs = new FileInputStream(productactivationsheet);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(3);
			
			for(int row=1;row<sh.getRows();row++)
			{
				reactivationmap = new HashMap<String, String>();
				for(int column=0;column<sh.getColumns();column++)
				{
					reactivationmap.put(sh.getCell(column, 0).getContents(), sh.getCell(column, row).getContents());
				}
				reactivationlist.add(reactivationmap);
			}
		} 
		catch (IOException e) 
		{
			read.createErrorFile(driver, logger);
			logger.info("Error in reactivationDatasheet Method");
			driver.quit();
			System.exit(0);
		}
		return reactivationlist;
	}
	

	@Override
	public String toString() 
	{
		return "Excelread[]";
	}
	
}