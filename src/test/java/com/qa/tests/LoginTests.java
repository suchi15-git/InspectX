package com.qa.tests;

import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.qa.base.Basetest;
import com.qa.pages.InspectionsListPage;
import com.qa.pages.LoginPage;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class LoginTests extends Basetest {

	LoginPage loginPage;
	InspectionsListPage inspectionsListPage;
	InputStream datais;
	JSONObject loginUsers;

//	@BeforeClass
//	public void beforeClass() throws Exception {
//		try {
//			String dataFileName = "data/LoginUsers.json";
//			datais = getClass().getClassLoader().getResourceAsStream(dataFileName);
//			JSONTokener tokener = new JSONTokener(datais);
//			loginUsers = new JSONObject(tokener);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		} finally {
//			if (datais != null) {
//				datais.close();
//			}
//		}
//
//	}
	
	@BeforeClass
	public void beforeClass() {
	    String dataFileName = "data/LoginUsers.json";
	    try (InputStream datais = getClass().getClassLoader().getResourceAsStream(dataFileName)) {
	        if (datais == null) {
	            throw new FileNotFoundException("File not found: " + dataFileName);
	        }
	        JSONTokener tokener = new JSONTokener(datais);
	        loginUsers = new JSONObject(tokener);
	    } catch (Exception e) {
	       // utils.log().error("Failed to load login user data: ", e);
	        throw new RuntimeException("Failed to load login user data", e);
	    }
	}


	@BeforeMethod
	public void setUp() {
		loginPage = new LoginPage(driver);
		inspectionsListPage = new InspectionsListPage(driver);
	}

	@Test
	public void successfulLogin() throws InterruptedException {

		Thread.sleep(2000);
		loginPage.enterActivationCode("gdottest");

		loginPage.clickConnect();
		Thread.sleep(2000);

		loginPage.enterPermissionButton();

		loginPage.clickLogin();

		Thread.sleep(2000);

		loginPage.enterUsername(loginUsers.getJSONObject("validUser").getString("username"));

		loginPage.clickContinue();

		loginPage.enterPassword(loginUsers.getJSONObject("validUser").getString("password"));

		Thread.sleep(2000);

		loginPage.clickLoginAgain();

		Thread.sleep(2000);

		String actualTitle = inspectionsListPage.getTxtInspectionListPageTitle();
		System.out.println(actualTitle);
		String expectedProductTitle = "List";
		Assert.assertEquals(expectedProductTitle, actualTitle);

	}

}
