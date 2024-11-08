package com.qa.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

import com.qa.base.BasePage;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class LoginPage extends BasePage {

	public LoginPage(AppiumDriver driver) {
		super(driver);
		// PageFactory.initElements(driver, this);
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	@AndroidFindBy(className = "android.widget.EditText")
	private WebElement activationCodeTxFld;
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Connect\"]")
	private WebElement connectBtn;
	@AndroidFindBy(xpath = "//android.widget.Button[@resource-id=\"com.android.permissioncontroller:id/permission_allow_foreground_only_button\"]")
	private WebElement allowLocationPermissionBtn;
	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Login\"]")
	private WebElement loginBtn;
//	@AndroidFindBy(accessibility ="userName")
//	private WebElement usernameTxtFld;
	@AndroidFindBy(xpath = "//android.widget.EditText[@resource-id=\"userName\"]")
private WebElement usernameTxtFld;
	@AndroidFindBy(xpath = "//android.widget.Button[@text=\"Continue\"]")
	private WebElement continueBtn;
	@AndroidFindBy(xpath = "//android.widget.EditText[@resource-id=\"pass\"]")
	private WebElement passwordBtn;
	@AndroidFindBy(xpath = "//android.widget.Button[@text=\"Login\"]")
	private WebElement login;

	public LoginPage enterActivationCode(String activationcode) {
		System.out.println("Entering activation code");
		utils.log().info("entering activation code:" +activationcode);
		sendKeys(activationCodeTxFld, activationcode);
		return this;
	}

	public LoginPage clickConnect() {
		System.out.println("Clicking connect");
		click(connectBtn);
		return this;

	}

	public LoginPage enterPermissionButton() {
		System.out.println("Allowing location permissions");
		click(allowLocationPermissionBtn);
		return this;
	}

	public LoginPage clickLogin() {
		click(loginBtn,"Click Login");
		return this;
	}

	public LoginPage enterUsername(String username) {
		sendKeys(usernameTxtFld, username, "entering username:" +username );
		return this;
	}

	public LoginPage clickContinue() {
		click(continueBtn, "click continue");
		return this;
	}

	public LoginPage enterPassword(String password) {
		sendKeys(passwordBtn, password, "entering password:" +password);
		return this;
	}

	public void clickLoginAgain() {
		WebElement loginAgain = driver.findElement(AppiumBy.xpath("//android.widget.Button[@text=\"Login\"]"));
		System.out.println("Clicking login again");
		click(loginAgain);

	}
	
	public String getAttribute(WebElement e, String attribute) {
		waitForVisibility(e);
		return e.getAttribute(attribute);
		
	}
	
	public InspectionsListPage login(String username, String password) {
		clickLogin();
		enterUsername(username);
		clickContinue();
		enterPassword(password);
		clickLoginAgain();
		return new InspectionsListPage(driver);
		
	}
	


}
