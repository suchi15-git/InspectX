package com.qa.base;

import java.time.Duration;
import java.util.HashMap;


import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.qa.utils.TestUtils;

import Reports.ExtentReportManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

public class BasePage {

	public AppiumDriver driver;
	public WebDriverWait wait;
	public TestUtils utils;
	

	public BasePage(AppiumDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
		this.utils = new TestUtils(); 
	}

	public void waitForVisibility(WebElement e) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
		wait.until(ExpectedConditions.visibilityOf(e));
	}

	public void click(WebElement e) {
		waitForVisibility(e);
		e.click();
	}
	
	public void click(WebElement e, String msg) {
		waitForVisibility(e);
		utils.log().info(msg);
		ExtentReportManager.getTest().log(Status.INFO, msg);
		e.click();
	}

	public void sendKeys(WebElement e, String txt) {
		waitForVisibility(e);
		e.sendKeys(txt);
	}
	
	
	public void sendKeys(WebElement e, String txt, String msg) {
		waitForVisibility(e);
		utils.log().info(msg);
		ExtentReportManager.getTest().log(Status.INFO, msg);
		e.sendKeys(txt);
	}

	public String getAttribute(WebElement e, String attribute) {
		try {
			waitForVisibility(e); // Wait until the element is visible
			return e.getAttribute(attribute);
		} catch (StaleElementReferenceException e1) {
			// Handle stale element reference
			System.out.println("StaleElementReferenceException: The element is no longer attached to the DOM.");
			return null; // Or handle as needed
		} catch (NoSuchElementException e2) {
			// Handle the case where the element is not found
			System.out.println("NoSuchElementException: The element was not found.");
			return null; // Or handle as needed
		} catch (Exception e3) {
			// Handle any other exceptions
			System.out.println("An error occurred: " + e3.getMessage());
			return null; // Or handle as needed
		}
	}

	public WebElement scrollToElement(String parentDescription, String childDescription) {
        String uiAutomatorString =
            "new UIScrollable(new UISelector()).scrollIntoView(new UISelector().description(\"" + parentDescription + "\").childSelector(new UISelector().description(\"" + childDescription + "\")));";

        return driver.findElement(AppiumBy.androidUIAutomator(uiAutomatorString));
    }
	
	public void iOSScrollToElement() {
		RemoteWebElement element = (RemoteWebElement)driver.findElement(AppiumBy.className("XCUIElementTypeTable"));
		String parentID = element.getId();
		HashMap <String , String> scrollObject = new HashMap <String , String>();
		scrollObject.put("element", parentID);
		//scrollObject.put("direction", "down");
		scrollObject.put("predicateString", "label='element lable'");
		driver.executeScript("mobile:scroll", scrollObject);
		
	}
	


}
