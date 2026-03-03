package com.dmx.test.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DebugUtils {
    
    public static void printPageInfo(WebDriver driver, String step) {
        System.out.println("=== DEBUG INFO - " + step + " ===");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + driver.getTitle());
        System.out.println("Page Source Length: " + driver.getPageSource().length());
    }
    
    public static void takeScreenshot(WebDriver driver, String name) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            // Tạo thư mục screenshots nếu chưa có
            File screenshotsDir = new File("screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }
            
            File destFile = new File("screenshots/" + name + "_" + timestamp + ".png");
            Files.copy(srcFile.toPath(), destFile.toPath());
            System.out.println("Screenshot saved: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Không thể chụp screenshot: " + e.getMessage());
        }
    }
}
