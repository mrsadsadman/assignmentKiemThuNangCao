package test;

import base.BaseTest;
import page.LoginPage;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;

import static org.testng.Assert.*;

public class LoginTest extends BaseTest {
    private LoginPage loginPage;


    @BeforeMethod
    public void initPage() {
        loginPage = new LoginPage(driver);
    }

    @Test(description = "TC01 - Số điện thoại hợp lệ 10 số")
    public void testValidPhone() {
        loginPage.enterPhone("0901234777");
        loginPage.clickContinue();

        assertTrue(loginPage.isStep2Displayed(), "Không chuyển sang màn hình nhập OTP");
        assertTrue(loginPage.getStep2Message().contains("Mã xác nhận đã được gửi"),
                "Thông báo không đúng");
        assertTrue(loginPage.isStep1Hidden(), "Step1 vẫn còn hiển thị");
    }

    @Test(description = "TC02 - Số điện thoại ngắn hơn 10 số")
    public void testPhoneTooShort() {
        loginPage.enterPhone("09123");
        loginPage.clickContinue();

        String error = loginPage.getErrorMessage();
        assertEquals(error, "Số điện thoại không hợp lệ", "Thông báo lỗi sai");
    }

    @Test(description = "TC03 - Số điện thoại chứa chữ cái")
    public void testPhoneWithLetters() {
        loginPage.enterPhone("09abc12345");
        loginPage.clickContinue();

        String error = loginPage.getErrorMessage();
        assertTrue(error.contains("không hợp lệ") || error.contains("định dạng"),
                "Không hiển thị lỗi định dạng. Lỗi thực tế: " + error);
    }

    @Test(description = "TC04 - Để trống số điện thoại")
    public void testEmptyPhone() {
        loginPage.enterPhone("");
        loginPage.clickContinue();

        String error = loginPage.getErrorMessage();
        assertEquals(error, "Vui lòng nhập số điện thoại", "Thông báo lỗi sai");
    }

    @Test(description = "TC05 - Số điện thoại chứa ký tự đặc biệt")
    public void testPhoneWithSpecialChars() {
        loginPage.enterPhone("09012###89");
        loginPage.clickContinue();

        String error = loginPage.getErrorMessage();
        assertTrue(error.contains("không hợp lệ") || error.contains("định dạng"),
                "Không hiển thị lỗi. Lỗi thực tế: " + error);
    }
}