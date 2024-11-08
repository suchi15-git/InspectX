package com.qa.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;

//import com.aventstack.extentreports.testng.listener.ExtentITestListenerAdapter;
import com.qa.utils.TestUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.ThreadContext;

//@Listeners(ExtentITestListenerAdapter.class)
public class Basetest {

	protected static AppiumDriver driver;
	protected static Properties props;
	protected static String dateTime;
	InputStream inputstream;
	InputStream stringis;
	// protected TestUtils utils;
	private static AppiumDriverLocalService server;
	private static ThreadLocal<String> platform = ThreadLocal.withInitial(() -> "");  // Default to empty string
	private static ThreadLocal<String> deviceName = new ThreadLocal<>();


	protected static HashMap<String, String> strings = new HashMap<>();
	protected TestUtils utils = new TestUtils();

	@BeforeMethod()
	public void beforeMethod() {
		((CanRecordScreen) driver).startRecordingScreen();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) throws IOException {
		// Stop the screen recording and get the Base64 encoded video
		String media = ((CanRecordScreen) driver).stopRecordingScreen();

		if (result.getStatus() == 2) {
			// Decode the Base64-encoded video string
			byte[] videoBytes = Base64.decodeBase64(media); // Decode once

			Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
			String dir = "Videos" + File.separator + params.get("platformName") + "_" + params.get("platformVersion")
					+ "_" + params.get("deviceName") + File.separator + dateTime + File.separator
					+ result.getTestClass().getRealClass().getSimpleName();

			File videoDir = new File(dir);

			// Create the directory if it doesn't exist
			if (!videoDir.exists()) {
				videoDir.mkdirs();
			}

			// Save the video to the specified path
			try (FileOutputStream stream = new FileOutputStream(new File(videoDir, result.getName() + ".mp4"))) {
				stream.write(videoBytes); // Write the decoded bytes directly
				System.out.println(
						"Video saved successfully to: " + videoDir + File.separator + result.getName() + ".mp4");
			} catch (IOException e) {
				e.printStackTrace(); // Handle the exception as needed
			}
		}

	}

	@BeforeSuite
	public void beforeSuite() throws Exception, Exception {
		ThreadContext.put("ROUTINGKEY", "ServerLogs");
		if (!checkIfAppiumServerIsRunning(4723)) {
			server = getAppiumService();
			server.start();
			server.clearOutPutStreams();
			utils.log().info("appium server started...");
		} else {
			utils.log().info("Appium server is already running..");
		}

	}

	@AfterSuite
	public void afterSuite() {
		server.stop();
		utils.log().info("appium server stopped...");
	}

	public AppiumDriverLocalService getAppiumServerDefault() {
		return AppiumDriverLocalService.buildDefaultService();
	}

	public AppiumDriverLocalService getAppiumService() {
		HashMap<String, String> environment = new HashMap<>();

		// Set the PATH variable to include necessary paths
		environment.put("PATH",
				"/Users/apple/Library/Android/sdk/platform-tools:" + "/Users/apple/Library/Android/sdk/cmdline-tools:"
						+ "/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home/bin:" + "/usr/local/bin:"
						+ System.getenv("PATH"));

		// Set ANDROID_HOME and optionally ANDROID_SDK_ROOT
		environment.put("ANDROID_HOME", "/Users/apple/Library/Android/sdk");
		environment.put("ANDROID_SDK_ROOT", environment.get("ANDROID_HOME"));

		// Ensure the ServerLogs directory exists
		File logDir = new File("ServerLogs");
		if (!logDir.exists()) {
			logDir.mkdirs(); // Create the directory if it doesn't exist
		}

		// Build and return the Appium service
		try {
			return AppiumDriverLocalService
					.buildService(new AppiumServiceBuilder().usingDriverExecutable(new File("/usr/local/bin/node")) // Update
																													// if
																													// necessary
							.withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js")) // Update if
																											// necessary
							.usingPort(4723).withArgument(GeneralServerFlag.SESSION_OVERRIDE)
							.withEnvironment(environment).withLogFile(new File(logDir, "server.log"))); // Log file in
																										// ServerLogs
																										// directory
		} catch (Exception e) {
			System.err.println("Failed to start Appium service: " + e.getMessage());
			return null; // Or handle the error as needed
		}
	}

	public boolean checkIfAppiumServerIsRunning(int port) throws Exception {
		boolean isAppiumServerRunning = false;
		ServerSocket socket;

		try {
			socket = new ServerSocket(port);
			socket.close();
		} catch (IOException e) {
			System.out.println("1");
			isAppiumServerRunning = true;
		} finally {
			socket = null;
		}
		return isAppiumServerRunning;
	}

	@BeforeTest()
	@Parameters({ "platformName", "platformVersion", "deviceName", "udid" })
	public void beforeTest(String platformName, String platformVersion, String deviceName, String udid)
			throws Exception {

		utils = new TestUtils();
		dateTime = utils.getdateTime();
		props = new Properties();

		InputStream inputstream = null;
		InputStream stringis = null;

		String strFile = "logs" + File.separator + platformName + "_" + deviceName;
		File logFile = new File(strFile);
		if (logFile.exists()) {
			logFile.mkdirs();
		}
		ThreadContext.put("ROUTINGKEY", strFile);
		setPlatform(platformName);
		setDeviceName(deviceName);

		try {
			String propFileName = "config.properties";
			String xmlFileName = "Strings/strings.xml";

			// Load properties file
			inputstream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputstream == null) {
				utils.log().error("Property file 'config.properties' not found in the classpath");
				throw new FileNotFoundException("Property file 'config.properties' not found in the classpath");
			}
			props.load(inputstream);

			// Load strings XML file
			stringis = getClass().getClassLoader().getResourceAsStream(xmlFileName);
			if (stringis == null) {
				utils.log().info("XML file 'strings.xml' not found in the classpath");
				throw new FileNotFoundException("XML file 'strings.xml' not found in the classpath");
			}
			strings = utils.parseStringXML(stringis);

			// Setup the driver based on platform
			switch (platformName) {
			case "Android":
				setupAndroidDriver(platformVersion, deviceName, udid);
				break;
			case "iOS":
				setupIOSDriver(platformVersion, deviceName, udid);
				break;
			default:
				throw new Exception("Invalid Platform: " + platformName);
			}

			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		} catch (Exception e) {
			// log.error("Failed to initialize test: " + e.getMessage());
			throw e;
		} finally {
			// Ensure streams are closed properly
			if (inputstream != null) {
				try {
					inputstream.close();
				} catch (IOException e) {
					utils.log().warn("Failed to close inputstream: " + e.getMessage());
				}
			}
			if (stringis != null) {
				try {
					stringis.close();
				} catch (IOException e) {
					utils.log().warn("Failed to close stringis: " + e.getMessage());
				}
			}
		}
	}

	public AppiumDriver getDriver() {
		return driver;
	}

	private void setupAndroidDriver(String platformVersion, String deviceName, String udid) throws Exception {
		UiAutomator2Options options = new UiAutomator2Options();
		options.setCapability("platformName", "Android");
		options.setCapability("platformVersion", platformVersion);
		options.setCapability("deviceName", deviceName);
		options.setCapability("automationName", props.getProperty("androidAutomationName"));
		// options.setCapability("udid", props.getProperty("androidUdid")); // Make UDID
		// configurable
		options.setCapability("udid", udid);

		String appLocation = props.getProperty("androidAppLocation");
		File appFile = new File(appLocation);
		if (!appFile.exists()) {
			throw new FileNotFoundException("APK file not found at: " + appLocation);
		}

		options.setApp(appFile.getAbsolutePath());

		URL androidUrl = new URL(props.getProperty("appiumURL"));
		utils.log().info("appURL is:" + androidUrl);
		driver = new AndroidDriver(androidUrl, options);
	}

	private void setupIOSDriver(String platformVersion, String deviceName, String udid) throws Exception {
		XCUITestOptions options = new XCUITestOptions();
		options.setCapability("platformName", "iOS");
		options.setCapability("platformVersion", platformVersion);
		options.setCapability("deviceName", deviceName);
		options.setCapability("automationName", props.getProperty("iOSAutomationName"));
		// options.setCapability("udid", props.getProperty("iOSUdid")); // Make UDID
		// configurable
		options.setCapability("udid", udid);

		String appLocation = props.getProperty("iOSAppLocation");
		File appFile = new File(appLocation);
		if (!appFile.exists()) {
			throw new FileNotFoundException("App file not found at: " + appLocation);
		}

		options.setApp(appFile.getAbsolutePath());

		URL iosUrl = new URL(props.getProperty("appiumURL"));
		utils.log().info("appURL is:" + iosUrl);
		driver = new IOSDriver(iosUrl, options);
	}

	public String getDateTime() {
		return dateTime;
	}
	
	public String getPlatform() {
		return platform.get();
	}
	
	public void setPlatform(String platformName2) {
	    platform.set(platformName2);  // Set platform value for the current thread
	}
	
	public String getDeviceName() {
		return deviceName.get();
	}
	
	public void setDeviceName (String deviceName2) {
		deviceName.set(deviceName2);
	}

	@AfterTest
	public void afterTest() {
		if (driver != null) {
			driver.quit();
		}
	}
}
