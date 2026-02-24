package com.dmx.test.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        // Xác định hệ điều hành
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Trên Windows, dùng driver từ thư mục drivers/
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
        }
        // Trên Linux (CI), chromedriver sẽ được cài riêng và nằm trong PATH

        ChromeOptions options = new ChromeOptions();
        // Nếu chạy trên CI (headless)
        if (System.getenv("CI") != null) {
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://www.dienmayxanh.com/lich-su-mua-hang/dang-nhap");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}