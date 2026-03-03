package com.dmx.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait longWait;
    private WebDriverWait shortWait;

    // Locators
    private By usernameInput = By.id("user-name");
    private By passwordInput = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.cssSelector("h3[data-test='error']");
    private By errorButton = By.cssSelector(".error-button");
    private By inventoryContainer = By.cssSelector(".inventory_container");
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        
        // Tăng timeout cho CI
        if (System.getenv("CI") != null) {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            this.longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } else {
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            this.longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        }
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

    public void logout() {
        try {
            System.out.println("Đang logout...");
            shortWait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
            Thread.sleep(1000);
            shortWait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
            Thread.sleep(1000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
            System.out.println("Logout thành công");
        } catch (Exception e) {
            System.out.println("Lỗi khi logout: " + e.getMessage());
            driver.get("https://www.saucedemo.com/");
        }
    }

    public void resetToLoginPage() {
        try {
            if (driver.getCurrentUrl().contains("inventory")) {
                logout();
                return;
            }
            
            if (!driver.getCurrentUrl().contains("saucedemo.com")) {
                driver.get("https://www.saucedemo.com/");
            }
            
            waitForPageLoad();
            
        } catch (Exception e) {
            System.out.println("Reset failed: " + e.getMessage());
            driver.get("https://www.saucedemo.com/");
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
            
            // Chờ cho response trả về
            Thread.sleep(2000);
        } catch (Exception e) {
            throw new RuntimeException("Không thể click nút Login", e);
        }
    }

    public void login(String username, String password) {
        System.out.println("Bắt đầu đăng nhập với username: " + username);
        resetToLoginPage();
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /**
     * FIX: Kiểm tra error message với timeout dài hơn và nhiều lần thử
     */
    public boolean isErrorDisplayed() {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                System.out.println("Kiểm tra error message lần " + (retryCount + 1));
                
                // Thử với các locator khác nhau
                boolean displayed = wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(errorMessage),
                    ExpectedConditions.visibilityOfElementLocated(errorButton)
                )).isDisplayed();
                
                if (displayed) {
                    System.out.println("Error message hiển thị!");
                    return true;
                }
            } catch (TimeoutException e) {
                System.out.println("Chưa thấy error message, thử lại...");
            }
            
            retryCount++;
            try {
                Thread.sleep(2000); // Chờ 2 giây giữa các lần thử
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Error message không hiển thị sau " + maxRetries + " lần thử");
        return false;
    }

    public String getErrorMessage() {
        try {
            // Thử với error message trước
            String error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
            System.out.println("Thông báo lỗi: " + error);
            return error;
        } catch (TimeoutException e) {
            try {
                // Thử với error button
                String error = driver.findElement(errorButton).getAttribute("innerText");
                System.out.println("Thông báo lỗi (từ button): " + error);
                return error;
            } catch (Exception ex) {
                System.out.println("Không tìm thấy thông báo lỗi");
                return "";
            }
        }
    }

    public boolean isLoginSuccessful() {
        try {
            boolean success = longWait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer)).isDisplayed();
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
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
