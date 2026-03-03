package com.dmx.test.tests;

import com.dmx.test.base.BaseTest;
import com.dmx.test.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LoginTest extends BaseTest {
    private LoginPage loginPage;

    @BeforeMethod
    public void initPage() {
        loginPage = new LoginPage(driver);
        loginPage.resetToLoginPage();
        System.out.println("=== BẮT ĐẦU TEST MỚI ===");
        System.out.println("URL: " + driver.getCurrentUrl());
    }

    @AfterMethod
    public void cleanup() {
        System.out.println("Kết thúc test, URL: " + driver.getCurrentUrl());
        System.out.println("======================");
    }

    @Test(description = "TC01 - Đăng nhập với username/password hợp lệ")
    public void testValidLogin() {
        System.out.println("=== TC01: Valid Login ===");
        
        loginPage.login("standard_user", "secret_sauce");

        assertTrue(loginPage.isLoginSuccessful(), "Không chuyển sang trang inventory");
        assertTrue(loginPage.getCurrentUrl().contains("inventory"), 
                "URL không đúng. Actual: " + loginPage.getCurrentUrl());
        
        System.out.println("=== TC01: PASS ===");
    }

    @Test(description = "TC02 - Đăng nhập với username không tồn tại")
    public void testInvalidUsername() {
        System.out.println("=== TC02: Invalid Username ===");
        
        loginPage.login("invalid_user", "secret_sauce");

        // Đợi thêm để error message xuất hiện
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        assertTrue(loginPage.isErrorDisplayed(), "Error message không hiển thị");
        String error = loginPage.getErrorMessage();
        System.out.println("Error nhận được: '" + error + "'");
        
        assertTrue(error.contains("Username and password do not match") || 
                  error.contains("Epic sadface") ||
                  error.contains("do not match"),
                "Sai nội dung lỗi. Actual: " + error);
        
        System.out.println("=== TC02: PASS ===");
    }

    @Test(description = "TC03 - Đăng nhập với password sai")
    public void testInvalidPassword() {
        System.out.println("=== TC03: Invalid Password ===");
        
        loginPage.login("standard_user", "wrong_password");

        // Đợi thêm để error message xuất hiện
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        assertTrue(loginPage.isErrorDisplayed(), "Error message không hiển thị");
        String error = loginPage.getErrorMessage();
        System.out.println("Error nhận được: '" + error + "'");
        
        assertTrue(error.contains("Username and password do not match") || 
                  error.contains("Epic sadface") ||
                  error.contains("do not match"),
                "Sai nội dung lỗi. Actual: " + error);
        
        System.out.println("=== TC03: PASS ===");
    }

    @Test(description = "TC04 - Để trống username")
    public void testEmptyUsername() {
        System.out.println("=== TC04: Empty Username ===");
        
        loginPage.login("", "secret_sauce");

        // Đợi thêm để error message xuất hiện
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        assertTrue(loginPage.isErrorDisplayed(), "Error message không hiển thị");
        String error = loginPage.getErrorMessage();
        System.out.println("Error nhận được: '" + error + "'");
        
        assertTrue(error.contains("Username is required") || 
                  error.contains("required"),
                "Sai nội dung lỗi. Actual: " + error);
        
        System.out.println("=== TC04: PASS ===");
    }

    @Test(description = "TC05 - Để trống password")
    public void testEmptyPassword() {
        System.out.println("=== TC05: Empty Password ===");
        
        loginPage.login("standard_user", "");

        // Đợi thêm để error message xuất hiện
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        assertTrue(loginPage.isErrorDisplayed(), "Error message không hiển thị");
        String error = loginPage.getErrorMessage();
        System.out.println("Error nhận được: '" + error + "'");
        
        assertTrue(error.contains("Password is required") || 
                  error.contains("required"),
                "Sai nội dung lỗi. Actual: " + error);
        
        System.out.println("=== TC05: PASS ===");
    }

    @Test(description = "TC06 - Để trống cả username và password")
    public void testEmptyBoth() {
        System.out.println("=== TC06: Empty Both ===");
        
        loginPage.login("", "");

        // Đợi thêm để error message xuất hiện
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        assertTrue(loginPage.isErrorDisplayed(), "Error message không hiển thị");
        String error = loginPage.getErrorMessage();
        System.out.println("Error nhận được: '" + error + "'");
        
        assertTrue(error.contains("Username is required") || 
                  error.contains("required"),
                "Sai nội dung lỗi. Actual: " + error);
        
        System.out.println("=== TC06: PASS ===");
    }

    @Test(description = "TC07 - Đăng nhập với locked_out_user")
    public void testLockedOutUser() {
        System.out.println("=== TC07: Locked Out User ===");
        
        loginPage.login("locked_out_user", "secret_sauce");

        // Đợi thêm để error message xuất hiện
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        
        assertTrue(loginPage.isErrorDisplayed(), "Error message không hiển thị");
        String error = loginPage.getErrorMessage();
        System.out.println("Error nhận được: '" + error + "'");
        
        assertTrue(error.contains("locked out") || 
                  error.contains("locked") ||
                  error.contains("Sorry"),
                "Sai nội dung lỗi. Actual: " + error);
        
        System.out.println("=== TC07: PASS ===");
    }
}
