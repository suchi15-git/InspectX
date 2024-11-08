package com.qa.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.qa.base.BasePage;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class SettingsPage extends BasePage{
	

	public SettingsPage(AppiumDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	@AndroidFindBy(xpath = ("//android.widget.TextView[@text=\"Logout\"]"))
	public WebElement logoutBtn;
	@AndroidFindBy(accessibility="android:id/button2")
	public WebElement logoutConfirmationBtn;
	
	
	public void clickLogoutBtn() {
		System.out.println("Clicking logout button");
		click(logoutBtn);
	}
	
	public LoginPage clickLogoutConfirmationBtn() {
		System.out.println("Clicking logout button present on confirmation pop-up");
		click(logoutConfirmationBtn);
		return new LoginPage(driver);
	}
	


}
