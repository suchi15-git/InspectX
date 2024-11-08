//package Reports;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import com.aventstack.extentreports.reporter.configuration.Theme;
//
//public class ExtentReportManager {
//    private static ExtentReports extent;
//    private static final String filePath = "Extent.html";
//    static Map extentTestMap = new HashMap();
//
//    public synchronized static ExtentReports getReporter() {
//        if (extent == null) {
//            ExtentSparkReporter spark = new ExtentSparkReporter(filePath);
//            spark.config().setDocumentTitle("Appium Framework");
//            spark.config().setReportName("MyApp");
//            spark.config().setTheme(Theme.DARK);
//            extent = new ExtentReports();
//            extent.attachReporter(spark);  // Fixed method name to 'attachReporter'
//        }
//        return extent;
//    }
//    
//    public static synchronized ExtentTest getTest() {
//    	return (ExtentTest) extentTestMap.get((int) (long) (Thread.currentThread().getId()));
//    }
//    
//    
//    public static synchronized ExtentTest startTest(String testName, String desc) {
//    	ExtentTest test = getReporter().createTest(testName,desc);
//    	extentTestMap.put((int)(long)(Thread.currentThread().getId()),test);
//    	
//    	return test;
//    }
//}

package Reports;

import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static final String filePath = "Extent.html";
    private static final Map<Long, ExtentTest> extentTestMap = new HashMap<>();

    // Get the ExtentReports instance
    public synchronized static ExtentReports getReporter() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(filePath);
            spark.config().setDocumentTitle("Appium Framework");
            spark.config().setReportName("MyApp");
            spark.config().setTheme(Theme.DARK);
            
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }
    
    
    // Start a new test and store it in the map
    public static synchronized ExtentTest startTest(String testName, String description) {
        ExtentTest test = getReporter().createTest(testName, description);
        extentTestMap.put(Thread.currentThread().getId(), test);
        return test;
    }
    
    // Get the current test for the current thread
    public static synchronized ExtentTest getTest() {
        return extentTestMap.get(Thread.currentThread().getId());
    }


    // Flush the reports
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}

