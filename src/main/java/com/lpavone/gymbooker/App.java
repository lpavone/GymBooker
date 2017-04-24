package com.lpavone.gymbooker;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author leonardo
 */
class App {

    private WebDriver driver;
    private String details;
    private String user;

    private final List<Workout> workoutsToBook = new ArrayList<>();

    public App(String user) {
        this.user = user;
        setDetails(user);
    }

    public App() {}

    private void setDetails(String user) {
        initWebBrowserCapabilities();
        details = user.equals("leo")? Constants.LEO_LOGIN : Constants.CLAU_LOGIN;
    }

    public void setUser(String user) {
        this.user = user;
        setDetails(user);
    }

    private void initWebBrowserCapabilities() {
        driver = WebDriverUtils.getWebDriverInstance();
        System.out.println("Selenium web driver created and initialized");
    }

    public List<Workout> getAvailableClasses() throws Exception{
        doLogin();
        //find available classes
        goToTimetable();
        WebElement timetableElem = driver.findElement(By.id("MemberTimetable"));
        List<WebElement> timetableItems = timetableElem.findElements(By.tagName("tr"));
        int daysParsed = 0;
        String currentDay = null;
        for(WebElement item : timetableItems){
            if(item.getAttribute("class").equals("dayHeader")){
                currentDay = item.findElement(By.tagName("h5")).getText().trim();
                daysParsed++;
            } else if( !item.getAttribute("class").equals("header") && daysParsed == 2){
                workoutsToBook.add( new Workout(
                        currentDay,
                        item.findElement(By.xpath(".//td[2]/span/a")).getText(),
                        item.findElement(By.xpath(".//td[1]/span")).getText())
                );
            }
            if(daysParsed > 2){
                break;
            }
        }

        return workoutsToBook;
    }

    public void goToTimetable() {
        driver.get(Constants.TIMETABLE_PAGE);
    }

    public void doLogin() throws Exception{
        //LOGIN
        try {
            driver.get(Constants.WEBSITE_HOME_URL);
            driver.findElement(By.id(Constants.EMAIL_FIELD_NAME)).sendKeys(getEmail());
            driver.findElement(By.id(Constants.PASSWORD_FIELD_NAME)).sendKeys(getPassword());
            driver.findElement(By.id("login")).click();
            //wait until log out form is loaded (max 6 secs)
            new WebDriverWait(driver, 6)
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("CSCAdmin")));
            System.out.println("BROWSER EVENT: Logged in");
        } catch (Exception e) {
            System.out.println("BROWSER EVENT: cannot login");
            throw new Exception(e);
        }
    }

    public void doLogout(){
        driver.get(Constants.LOGOUT);
    }

    public void storeBooking(String option) throws IOException {
        String[] indexes = option.split(",");
        CsvUtils csvUtils = new CsvUtils();
        FileWriter fileWriter = csvUtils.createFileWriter(user);
        for(String index : indexes){
            Workout classSelected = workoutsToBook.get( Integer.parseInt(index));
            csvUtils.writeLine(fileWriter,
                    Arrays.asList(
                            classSelected.getTime(),
                            classSelected.getName()
                    ));
        }
        csvUtils.flushContent(fileWriter);
    }

    private String getPassword() {
        return details.split(",")[1];
    }

    private String getEmail(){
        return details.split(",")[0];
    }

    public void findWorkouts(String time, String name) {
        WebElement timetableElem = driver.findElement(By.id("MemberTimetable"));
        System.out.println("Opening timetable page");
        List<WebElement> timetableItems = timetableElem.findElements(By.tagName("tr"));
        int daysParsed = 0;
        for(WebElement item : timetableItems){
            if(item.getAttribute("class").equals("dayHeader")){
                daysParsed++;
            } else if( !item.getAttribute("class").equals("header") && daysParsed == 1
                    && isClassTime(time, item) && isClassName(name, item) && isBookeable(item)){
                System.out.println("Item to book has been found");
                WebElement aElem = item.findElement(By.xpath(".//td[7]/a"));
                aElem.click();
                System.out.println("Book button clicked");
                //wait 3 seconds after click
                new WebDriverWait(driver, 3)
                        .until(ExpectedConditions.presenceOfElementLocated(By.id("btnPayNow")));
                //complete booking
                aElem = driver.findElement(By.id("btnPayNow"));
                aElem.click();
                System.out.println("Booking Confirmed");
                break;
            }
            if(daysParsed >= 2){
                break;
            }
        }
    }

    private boolean isBookeable(WebElement item) {
        return "Book".equals(item.findElement(By.xpath(".//td[7]/a")).getText());
    }

    private boolean isClassName(String name, WebElement item) {
        return name.replaceAll("\"","").equals(item.findElement(By.xpath(".//td[2]/span/a")).getText());
    }

    private boolean isClassTime(String time, WebElement item) {
        return time.replaceAll("\"","").equals(item.findElement(By.xpath(".//td[1]/span")).getText());
    }

}
