//package com.bizdirect.orders.proto.messages;
//
//
//import java.io.File;
//
//import org.testng.Assert;
//import org.testng.ITestResult;
//import org.testng.SkipException;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.Test;
//
//import com.relevantcodes.extentreports.ExtentReports;
//import com.relevantcodes.extentreports.ExtentTest;
//import com.relevantcodes.extentreports.LogStatus;
//
//public class ExtentReportClass{
//    ExtentReports extent;
//    ExtentTest logger;
//
//
//    @BeforeTest
//    public void startReport(){
//        extent = new ExtentReports (System.getProperty("user.dir") +"/STMExtentReport.html", true);
//        extent
//                .addSystemInfo("Host Name", "SoftwareTestingMaterial")
//                .addSystemInfo("Environment", "Automation Testing")
//                .addSystemInfo("User Name", "Rajkumar SM");
//        extent.loadConfig(new File(System.getProperty("user.dir")+"/extent-config.xml"));
//    }
//
//
//    @AfterMethod
//    public void getResult(ITestResult result){
//        if(result.getStatus() == ITestResult.FAILURE){
//            logger.log(LogStatus.FAIL, "Test Case Failed is "+result.getName());
//            logger.log(LogStatus.FAIL, "Test Case Failed is "+result.getThrowable());
//        }else if(result.getStatus() == ITestResult.SKIP){
//            logger.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
//        }
//        extent.endTest(logger);
//    }
//    @AfterTest
//    public void endReport(){
//        extent.flush();
//        extent.close();
//    }
//}
//
//
//
////    ExtentReports extent;
////    ExtentTest log;
////
////
////    @BeforeSuite
////    public void startReport() {
////
////        extent = new ExtentReports("STMExtentReport.html", true);
////        extent
////                .addSystemInfo("Host Name", "Checkout")
////                .addSystemInfo("Environment", "Automation Testing")
////                .addSystemInfo("User Name", "Ullas");
////        extent.loadConfig(new File("extent-config.xml"));
////    }
////
////    @AfterMethod
////    public void getResult(ITestResult result) {
////        if (result.getStatus() == ITestResult.FAILURE) {
////            log.log(LogStatus.FAIL, "Test Case Failed is " + result.getName());
////            log.log(LogStatus.FAIL, "Test Case Failed is " + result.getThrowable());
////        } else if (result.getStatus() == ITestResult.SKIP) {
////            log.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
////        }else if(result.getStatus() == ITestResult.SUCCESS){
////            log.log(LogStatus.PASS, "Test Case passed is " + result.getName());
////
////        }
////
////        extent.endTest(log);
////    }
////
////    @AfterSuite
////    public void endReport() {
////
////        extent.flush();
////        extent.close();
////    }
//
//
//
////    private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal<>();
////    static ExtentTest test;
////    private static ExtentReports extent;
////
////
////    @BeforeTest
////    public void beforeTest() {
////        System.out.println("Before Suite");
////        extent = ExtentManager.createInstance("extent.html");
////        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent.html");
////        extent.attachReporter(htmlReporter);
////        test = extent.createTest(getClass().getSimpleName());
////
////    }
////
////
////    @AfterMethod
////    public synchronized void afterMethod(ITestResult result) {
////        if (result.getStatus() == ITestResult.FAILURE)
////            test.fail(result.getThrowable());
////        else if (result.getStatus() == ITestResult.SKIP)
////            test.skip(result.getThrowable());
////        else
////            test.pass("Test passed");
////
////    }
////
////    @AfterClass
////    protected void afterClass() {
////        System.out.println("After Class");
////        extent.close();
////        extent.flush();
////
////    }
////
//
