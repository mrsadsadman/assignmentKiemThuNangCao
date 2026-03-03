package com.dmx.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;

    // Locators
    private By usernameInput = By.id("user-name");
    private By passwordInput = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.cssSelector("h3[data-test='error']");
    private By errorMessageContainer = By.cssSelector(".error-message-container");
    private By inventoryContainer = By.cssSelector(".inventory_container");
    private By inventoryList = By.cssSelector(".inventory_list");
    private By appLogo = By.cssSelector(".app_logo");
    private By loginContainer = By.cssSelector(".login_container");
    private By loginWrapper = By.cssSelector(".login_wrapper");
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /**
     * Kiểm tra trang đã load xong chưa - version mới không throw exception
     */
    public boolean waitForPageLoad() {
        try {
            System.out.println("Đang chờ trang login load...");
            // Kiểm tra nhanh xem có đang ở inventory không
            if (driver.getCurrentUrl().contains("inventory")) {
                System.out.println("Đang ở inventory page, cần logout trước");
                return false;
            }
            
            // Chờ ít nhất một element xuất hiện
            shortWait.until(ExpectedConditions.presenceOfElementLocated(loginWrapper));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
            System.out.println("Trang login đã load thành công!");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Timeout khi load trang login!");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());
            return false;
        }
    }

    /**
     * Logout nếu đang ở inventory
     */
    public void logoutIfNeeded() {
        try {
            if (driver.getCurrentUrl().contains("inventory")) {
                System.out.println("Đang logout khỏi inventory...");
                shortWait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
                Thread.sleep(500);
                shortWait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
                Thread.sleep(1000);
                System.out.println("Logout thành công");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi logout: " + e.getMessage());
        }
    }

    /**
     * Đảm bảo đang ở trang login
     */
    public void ensureOnLoginPage() {
        logoutIfNeeded();
        
        int retries = 3;
        while (retries > 0) {
            if (waitForPageLoad()) {
                return;
            }
            
            System.out.println("Thử lại lần " + (4 - retries));
            driver.get("https://www.saucedemo.com/");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retries--;
        }
        
        System.out.println("Không thể load trang login sau nhiều lần thử");
    }

    /**
     * Nhập username - có kiểm tra và retry
     */
    public void enterUsername(String username) {
        int retries = 3;
        while (retries > 0) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(usernameInput)).clear();
                driver.findElement(usernameInput).sendKeys(username);
                System.out.println("Đã nhập username: " + username);
                return;
                
            } catch (Exception e) {
                retries--;
                System.out.println("Lỗi nhập username, còn " + retries + " lần thử lại");
                if (retries == 0) {
                    throw new RuntimeException("Không thể nhập username sau 3 lần thử", e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Nhập password
     */
    public void enterPassword(String password) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(passwordInput)).clear();
            driver.findElement(passwordInput).sendKeys(password);
            System.out.println("Đã nhập password");
        } catch (Exception e) {
            throw new RuntimeException("Không thể nhập password", e);
        }
    }

    /**
     * Click nút Login
     */
    public void clickLogin() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
            System.out.println("Đã click nút Login");
        } catch (Exception e) {
            throw new RuntimeException("Không thể click nút Login", e);
        }
    }

    /**
     * Thực hiện đăng nhập
     */
    public void login(String username, String password) {
        System.out.println("Bắt đầu đăng nhập với username: " + username);
        ensureOnLoginPage();
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        
        // Chờ sau khi click
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy thông báo lỗi
     */
    public String getErrorMessage() {
        try {
            String error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
            System.out.println("Thông báo lỗi: " + error);
            return error;
        } catch (TimeoutException e) {
            System.out.println("Không có thông báo lỗi");
            return "";
        }
    }

    /**
     * Kiểm tra error message có hiển thị không
     */
    public boolean isErrorDisplayed() {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageContainer)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra đăng nhập thành công
     */
    public boolean isLoginSuccessful() {
        try {
            boolean success = wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer)).isDisplayed();
            System.out.println("Đăng nhập thành công: " + success);
            return success;
        } catch (TimeoutException e) {
            System.out.println("Đăng nhập thất bại - không tìm thấy inventory container");
            return false;
        }
    }

    /**
     * Lấy URL hiện tại
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Kiểm tra đang ở trang login
     */
    public boolean isOnLoginPage() {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(loginWrapper)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Refresh trang
     */
    public void refreshPage() {
        driver.navigate().refresh();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
