package page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators – cần kiểm tra lại trên trang thực tế (có thể thay đổi)
    private By phoneInput = By.id("txtPhoneNumber");
    private By continueBtn = By.cssSelector("button[type='submit'].btn");
    private By errorLabel = By.cssSelector("#frmGetVerifyCode label:not(.hide)");
    private By step2Message = By.cssSelector(".step2 .s1");
    private By step1 = By.cssSelector(".step1");
    private By step2 = By.cssSelector(".step2");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterPhone(String phone) {
        driver.findElement(phoneInput).clear();
        driver.findElement(phoneInput).sendKeys(phone);
    }

    public void clickContinue() {
        driver.findElement(continueBtn).click();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorLabel)).getText();
    }

    public boolean isStep2Displayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(step2Message)).isDisplayed();
    }

    public String getStep2Message() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(step2Message)).getText();
    }

    public boolean isStep1Hidden() {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(step1));
    }
}