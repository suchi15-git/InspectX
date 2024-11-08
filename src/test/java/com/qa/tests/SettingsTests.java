package com.qa.tests;

import java.lang.reflect.Method;

import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qa.base.Basetest;
import com.qa.pages.InspectionsListPage;
import com.qa.pages.LoginPage;
import com.qa.pages.SettingsPage;

import io.appium.java_client.AppiumDriver;

public class SettingsTests extends Basetest {
	
	LoginPage loginPage;
	InspectionsListPage inspectionsListPage;
	SettingsPage settingsPage;
	JSONObject loginUsers;
	
	@BeforeMethod
	public void beforeMethod(Method m) {
		loginPage = new LoginPage(driver);
		loginPage.login(loginUsers.getJSONObject("validUser").getString("username"), loginUsers.getJSONObject("validUser").getString("password"));
	}
    
    
	
	@AfterMethod
	public void afterMethod(Method m) {
		settingsPage = new SettingsPage(driver);
		
	}
	
	@Test
	public void logoutApplication() {
		inspectionsListPage.clickSettingsBtn();
		settingsPage.clickLogoutBtn();
		settingsPage.clickLogoutConfirmationBtn();
		
	}

}
