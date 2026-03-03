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
        System.out.println("Bắt đầu test với URL: " + driver.getCurrentUrl());
    }

    @AfterMethod
    public void cleanup() {
        System.out.println("Kết thúc test, URL: " + driver.getCurrentUrl());
        System.out.println("----------------------------------------");
    }

    @Test(description = "TC01 - Đăng nhập với username/password hợp lệ")
    public void testValidLogin() {
        System.out.println("=== BẮT ĐẦU TC01: Valid Login ===");
        
        loginPage.login("standard_user", "secret_sauce");

        assertTrue(loginPage.isLoginSuccessful(), "Không chuyển sang trang inventory");
        assertTrue(loginPage.getCurrentUrl().contains("inventory"), 
                "URL không đúng. Expected: chứa 'inventory', Actual: " + loginPage.getCurrentUrl());
        
        System.out.println("=== KẾT THÚC TC01: PASS ===");
    }

    @Test(description = "TC02 - Đăng nhập với username không tồn tại")
    public void testInvalidUsername() {
        System.out.println("=== BẮT ĐẦU TC02: Invalid Username ===");
        
        loginPage.login("invalid_user", "secret_sauce");

        assertTrue(loginPage.isErrorDisplayed(), "Không hiển thị thông báo lỗi");
        String error = loginPage.getErrorMessage();
        System.out.println("Error message: '" + error + "'");
        
        assertTrue(error.contains("Username and password do not match") || 
                  error.contains("Epic sadface"),
                "Thông báo lỗi không đúng. Thực tế: " + error);
        
        System.out.println("=== KẾT THÚC TC02: PASS ===");
    }

    @Test(description = "TC03 - Đăng nhập với password sai")
    public void testInvalidPassword() {
        System.out.println("=== BẮT ĐẦU TC03: Invalid Password ===");
        
        loginPage.login("standard_user", "wrong_password");

        assertTrue(loginPage.isErrorDisplayed(), "Không hiển thị thông báo lỗi");
        String error = loginPage.getErrorMessage();
        System.out.println("Error message: '" + error + "'");
        
        assertTrue(error.contains("Username and password do not match") ||
                  error.contains("Epic sadface"),
                "Thông báo lỗi không đúng. Thực tế: " + error);
        
        System.out.println("=== KẾT THÚC TC03: PASS ===");
    }

    @Test(description = "TC04 - Để trống username")
    public void testEmptyUsername() {
        System.out.println("=== BẮT ĐẦU TC04: Empty Username ===");
        
        loginPage.login("", "secret_sauce");

        assertTrue(loginPage.isErrorDisplayed(), "Không hiển thị thông báo lỗi");
        String error = loginPage.getErrorMessage();
        System.out.println("Error message: '" + error + "'");
        
        assertTrue(error.contains("Username is required"), 
                "Thông báo lỗi không đúng. Thực tế: " + error);
        
        System.out.println("=== KẾT THÚC TC04: PASS ===");
    }

    @Test(description = "TC05 - Để trống password")
    public void testEmptyPassword() {
        System.out.println("=== BẮT ĐẦU TC05: Empty Password ===");
        
        loginPage.login("standard_user", "");

        assertTrue(loginPage.isErrorDisplayed(), "Không hiển thị thông báo lỗi");
        String error = loginPage.getErrorMessage();
        System.out.println("Error message: '" + error + "'");
        
        assertTrue(error.contains("Password is required"), 
                "Thông báo lỗi không đúng. Thực tế: " + error);
        
        System.out.println("=== KẾT THÚC TC05: PASS ===");
    }

    @Test(description = "TC06 - Để trống cả username và password")
    public void testEmptyBoth() {
        System.out.println("=== BẮT ĐẦU TC06: Empty Both ===");
        
        loginPage.login("", "");

        assertTrue(loginPage.isErrorDisplayed(), "Không hiển thị thông báo lỗi");
        String error = loginPage.getErrorMessage();
        System.out.println("Error message: '" + error + "'");
        
        assertTrue(error.contains("Username is required"), 
                "Thông báo lỗi không đúng. Thực tế: " + error);
        
        System.out.println("=== KẾT THÚC TC06: PASS ===");
    }

    @Test(description = "TC07 - Đăng nhập với locked_out_user")
    public void testLockedOutUser() {
        System.out.println("=== BẮT ĐẦU TC07: Locked Out User ===");
        
        loginPage.login("locked_out_user", "secret_sauce");

        assertTrue(loginPage.isErrorDisplayed(), "Không hiển thị thông báo lỗi");
        String error = loginPage.getErrorMessage();
        System.out.println("Error message: '" + error + "'");
        
        assertTrue(error.contains("locked out") || 
                  error.contains("Sorry"),
                "Thông báo lỗi không đúng. Thực tế: " + error);
        
        System.out.println("=== KẾT THÚC TC07: PASS ===");
    }
}
