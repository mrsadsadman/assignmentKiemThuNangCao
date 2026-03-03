package com.dmx.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait longWait;

    // Locators
    private By usernameInput = By.id("user-name");
    private By passwordInput = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.cssSelector("h3[data-test='error']");
    private By inventoryContainer = By.cssSelector(".inventory_container");
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");
    
    // FIX: Thêm locator cho popup
    private By popupOkButton = By.xpath("//button[contains(text(),'OK')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        
        // Timeout cho CI
        if (System.getenv("CI") != null) {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            this.longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        } else {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            this.longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        }
    }

    /**
     * FIX: Đóng popup "Change password" nếu xuất hiện
     */
    public void closePopupIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement popupOk = shortWait.until(
                ExpectedConditions.elementToBeClickable(popupOkButton)
            );
            popupOk.click();
            System.out.println("Đã đóng popup Change Password");
            Thread.sleep(1000);
        } catch (Exception e) {
            // Không có popup, bỏ qua
        }
    }

    /**
     * FIX: Logout mạnh mẽ hơn
     */
    public void forceLogout() {
        try {
            System.out.println("Đang force logout...");
            
            // Cách 1: Click menu và logout
            try {
                wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
                Thread.sleep(1000);
                wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
                Thread.sleep(1000);
                System.out.println("Logout thành công qua menu");
                return;
            } catch (Exception e) {
                System.out.println("Logout qua menu thất bại: " + e.getMessage());
            }
            
            // Cách 2: Direct logout URL
            try {
                driver.get("https://www.saucedemo.com/");
                System.out.println("Đã về trang login bằng direct URL");
            } catch (Exception e) {
                System.out.println("Direct URL thất bại: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Force logout thất bại: " + e.getMessage());
        }
        
        // Luôn về trang login
        driver.get("https://www.saucedemo.com/");
        waitForPageLoad();
    }

    /**
     * FIX: Reset hoàn toàn state
     */
    public void hardReset() {
        // Clear cookies
        driver.manage().deleteAllCookies();
        
        // Về trang login
        driver.get("https://www.saucedemo.com/");
        waitForPageLoad();
        
        // Đóng popup nếu có
        closePopupIfPresent();
        
        System.out.println("Hard reset completed");
    }

    public boolean waitForPageLoad() {
        try {
            System.out.println("Đang chờ trang login load...");
            wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput));
            System.out.println("Trang login đã load thành công!");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Timeout khi load trang login!");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    public void enterUsername(String username) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(usernameInput)).clear();
            driver.findElement(usernameInput).sendKeys(username);
            System.out.println("Đã nhập username: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Không thể nhập username", e);
        }
    }

    public void enterPassword(String password) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(passwordInput)).clear();
            driver.findElement(passwordInput).sendKeys(password);
            System.out.println("Đã nhập password");
        } catch (Exception e) {
            throw new RuntimeException("Không thể nhập password", e);
        }
    }

    public void clickLogin() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
            System.out.println("Đã click nút Login");
            Thread.sleep(2000);
        } catch (Exception e) {
            throw new RuntimeException("Không thể click nút Login", e);
        }
    }

    public void login(String username, String password) {
        System.out.println("Bắt đầu đăng nhập với username: " + username);
        hardReset(); // Reset hoàn toàn trước mỗi login
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public boolean isErrorDisplayed() {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                System.out.println("Kiểm tra error message lần " + (retryCount + 1));
                
                WebElement errorElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorMessage)
                );
                
                if (errorElement.isDisplayed()) {
                    System.out.println("Error message hiển thị!");
                    return true;
                }
                
            } catch (TimeoutException e) {
                System.out.println("Chưa thấy error message");
            }
            
            retryCount++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Error message không hiển thị sau " + maxRetries + " lần thử");
        return false;
    }

    public String getErrorMessage() {
        try {
            String error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
            System.out.println("Thông báo lỗi: " + error);
            return error;
        } catch (TimeoutException e) {
            System.out.println("Không tìm thấy thông báo lỗi");
            return "";
        }
    }

    public boolean isLoginSuccessful() {
        try {
            boolean success = longWait.until(
                ExpectedConditions.visibilityOfElementLocated(inventoryContainer)
            ).isDisplayed();
            System.out.println("Đăng nhập thành công: " + success);
            return success;
        } catch (TimeoutException e) {
            System.out.println("Đăng nhập thất bại");
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isOnLoginPage() {
        try {
            return driver.findElement(usernameInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
