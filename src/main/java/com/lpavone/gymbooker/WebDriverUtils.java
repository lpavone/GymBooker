package com.lpavone.gymbooker;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created by leonardo on 14/03/17.
 */
class WebDriverUtils {

    private static WebDriver driver;

    private WebDriverUtils() {
    }

    /**
     * Return Web Driver Instance for Selenium
     * @return
     */
    public static WebDriver getWebDriverInstance() {
        if(driver == null){
            System.setProperty("webdriver.gecko.driver", "/home/leonardo/geckodriver");
            driver = new FirefoxDriver();
        }
        return driver;
    }

    public static void disposeWebDriverInstance() {
        if(driver != null){
            driver.quit();
        }
    }

}