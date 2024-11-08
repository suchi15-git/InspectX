package com.qa.listeners;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;  // Correct import for file handling
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.qa.base.Basetest;

import Reports.ExtentReportManager;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        // Print stack trace of the failure
        if (result.getThrowable() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);
            System.out.println(sw.toString());
        }

        Basetest base = new Basetest();
        // Take a screenshot
        File file = base.getDriver().getScreenshotAs(OutputType.FILE);

        // Encode the screenshot to Base64
        byte[] encoded = null;
        try {
            encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));  // Read file as byte array
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // Get parameters from the test context (platformName, deviceName, etc.)
        Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

        // Construct the image path
        String imagePath = "Screenshots" + File.separator + params.get("platformName") + "_"
                + params.get("platformVersion") + "_" + params.get("deviceName") + File.separator + base.getDateTime()
                + File.separator + result.getTestClass().getRealClass().getSimpleName() + File.separator
                + result.getName() + ".png";

        // Create directories if they don't exist
        File screenshotDir = new File(imagePath).getParentFile();
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }
        String completeImagePath = System.getProperty("user.dir") + File.separator + imagePath;

        // Save the screenshot to the specified location
        try {
            FileHandler.copy(file, new File(imagePath));
            Reporter.log("This is the sample screenshot");
            Reporter.log("<a href='" + completeImagePath + "'><img src ='" + completeImagePath
                    + "' height='100' width='100' /> <a/>");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Capture the screenshot and log the failure in Extent Reports
            ExtentReportManager.getTest().fail("Test failed due to some issue",
                    MediaEntityBuilder.createScreenCaptureFromPath(completeImagePath).build());

            // Also capture the screenshot as a Base64 string if needed
            if (encoded != null) {
                ExtentReportManager.getTest().fail("Test failed due to some issue", 
                    MediaEntityBuilder.createScreenCaptureFromBase64String(new String(encoded, StandardCharsets.US_ASCII)).build());
            }

        } catch (Exception e) {
            // Handle any I/O exceptions related to the screenshot handling
            e.printStackTrace();
        }
        
        ExtentReportManager.getTest().fail(result.getThrowable());
    }

    @Override
    public void onTestStart(ITestResult result) {
        Basetest base = new Basetest();
        ExtentReportManager.startTest(result.getName(), result.getMethod().getDescription())
                .assignCategory(base.getPlatform() + "_" + base.getDeviceName()).assignAuthor("SuchitaKage");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.getTest().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.getReporter().flush();
    }
}
