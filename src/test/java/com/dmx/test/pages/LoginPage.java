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

    // Locators
    private By usernameInput = By.id("user-name");
    private By passwordInput = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.cssSelector("h3[data-test='error']");
    private By inventoryContainer = By.cssSelector(".inventory_container");
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");
    private By inventoryList = By.cssSelector(".inventory_list");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
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

    /**
     * FIX: Logout đúng cách
     */
    public void logout() {
        try {
            System.out.println("Đang logout...");
            // Click menu button
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
            Thread.sleep(1000);
            
            // Click logout link
            shortWait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
            Thread.sleep(1000);
            
            // Đợi quay lại trang login
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
            System.out.println("Logout thành công");
        } catch (Exception e) {
            System.out.println("Lỗi khi logout: " + e.getMessage());
            // Nếu logout fail, load lại trang login
            driver.get("https://www.saucedemo.com/");
        }
    }

    /**
     * FIX: Reset trạng thái hoàn toàn
     */
    public void resetToLoginPage() {
        try {
            // Nếu đang ở inventory, logout
            if (driver.getCurrentUrl().contains("inventory")) {
                logout();
                return;
            }
            
            // Nếu không ở trang login, load lại
            if (!driver.getCurrentUrl().contains("saucedemo.com")) {
                driver.get("https://www.saucedemo.com/");
            }
            
            // Đợi trang login load
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
            Thread.sleep(1000);
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

    public boolean isErrorDisplayed() {
        try {
            boolean displayed = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
            System.out.println("Error displayed: " + displayed);
            return displayed;
        } catch (TimeoutException e) {
            System.out.println("Error message không hiển thị");
            return false;
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
            return wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
