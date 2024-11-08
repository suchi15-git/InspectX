package com.qa.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import com.qa.base.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class InspectionsListPage extends BasePage {

    public InspectionsListPage(AppiumDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"List\"]")  
    public WebElement listPageTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Settings']")
    public WebElement settingsBtn;
    
    @AndroidFindBy(xpath="//android.widget.TextView[@text=\"Working Set\"]")
    public WebElement workingsetBtn;

    public String getTxtInspectionListPageTitle() {
        return getAttribute(listPageTitle, "text");
    }
    
	public SettingsPage clickSettingsBtn() {
		System.out.println("Clicking settings button");
		click(settingsBtn);
		return new SettingsPage(driver);
	}
    
    
}
