package com.lpavone.gymbooker;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
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
class HeadlessApp {

    private WebClient webClient;
    private String details;
    private String user;

    private final List<Workout> workoutsToBook = new ArrayList<>();

    public HeadlessApp(String user) {
        this.user = user;
        setDetails(user);
    }

    public HeadlessApp() {}

    private void setDetails(String user) {
        initWebBrowserCapabilities();
        details = user.equals("leo")? Constants.LEO_LOGIN : Constants.CLAU_LOGIN;
    }

    public void setUser(String user) {
        this.user = user;
        setDetails(user);
    }

    private void initWebBrowserCapabilities() {
        webClient = new WebClient(BrowserVersion.FIREFOX_52);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
    //    webClient.getOptions().setThrowExceptionOnScriptError(true);

        System.out.println("Webclient created and initialized");
    }

    public List<Workout> getAvailableClasses() throws Exception {
        doLogin();
        //find available classes
        goToTimetable();
        HtmlPage page = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
        HtmlElement timetableElem = page.getHtmlElementById("MemberTimetable");
        DomNodeList<HtmlElement> timetableItems = timetableElem.getElementsByTagName("tr");
        int daysParsed = 0;
        String currentDay = null;
        for(HtmlElement item : timetableItems){
            if(item.getAttribute("class").equals("dayHeader")){
                currentDay = item.getElementsByTagName("h5").get(0).asText().trim();
                daysParsed++;
            } else if( !item.getAttribute("class").equals("header") && daysParsed == 2){
                workoutsToBook.add( new Workout(
                        currentDay,
                        ((HtmlElement)item.getFirstByXPath(".//td[2]/span/a")).getTextContent(),
                        ((HtmlElement)item.getFirstByXPath(".//td[1]/span")).getTextContent())
                );
            }
            if(daysParsed > 2){
                break;
            }
        }

        return workoutsToBook;
    }

    public void goToTimetable() {
        try {
            webClient.getPage(Constants.TIMETABLE_PAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doLogin() throws Exception{
        //LOGIN
        try {
            // Get the first page
            final HtmlPage page1 = webClient.getPage(Constants.WEBSITE_HOME_URL);
            // Get the form that we are dealing with and within that form,
            // find the submit button and the field that we want to change.
            final HtmlForm form = page1.getHtmlElementById("form1");

            final HtmlSubmitInput button = (HtmlSubmitInput) form.getElementsByAttribute(
                    "input","id","login").get(0);
            final HtmlTextInput emailField = form.getInputByName(Constants.EMAIL_FIELD_NAME);
            final HtmlPasswordInput passField = form.getInputByName(Constants.PASSWORD_FIELD_NAME);

            // Change the value of the text field
            emailField.setValueAttribute( getEmail());
            passField.setValueAttribute( getPassword());
            // Now submit the form by clicking the button and get back the second page.
            final HtmlPage page2 = button.click();
            //try 20 times to wait .5 second each for filling the page.
            for (int i = 0; i < 20; i++) {
                try{
                    if (page2.getHtmlElementById("CSCAdmin") != null){
                        break;
                    }
                } catch (ElementNotFoundException e){}
                synchronized (page2) {
                    page2.wait(500);
                }
            }
            System.out.println("BROWSER EVENT: Logged in");
        } catch (Exception e) {
            System.out.println("BROWSER EVENT: cannot login");
            throw new Exception(e);
        }
    }

    public void doLogout(){
        try{
            webClient.getPage(Constants.LOGOUT);
        } catch (Exception e){
            System.out.println("Cannot logout");
        }
    }

    public void storeBooking(String option) throws IOException {
        String[] indexes = option.trim().split(",");
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

    public void findWorkouts(String time, String name) throws InterruptedException, IOException {
        HtmlPage page = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
        HtmlElement timetableElem = page.getHtmlElementById("MemberTimetable");
        DomNodeList<HtmlElement> timetableItems = timetableElem.getElementsByTagName("tr");
        System.out.println("Opening timetable page");
        int daysParsed = 0;
        for(HtmlElement item : timetableItems){
            if(item.getAttribute("class").equals("dayHeader")){
                daysParsed++;
            } else if( !item.getAttribute("class").equals("header") //&& daysParsed == 1
                    && isClassTime(time, item) && isClassName(name, item) && isBookeable(item)){
                System.out.println("Item to book has been found");
                HtmlAnchor aElem = item.getFirstByXPath(".//td[7]/a");
                page = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
                String jsFunction = aElem.getAttribute("onclick");
                jsFunction = jsFunction.replace("return false;", "");
                ScriptResult scriptResult = page.executeJavaScript( jsFunction);
                webClient.waitForBackgroundJavaScript(10000);
                synchronized (webClient) {
                    webClient.wait(10000);
                }
                webClient.getPage(Constants.CONFIRM_LINK);
                System.out.println("Booking Confirmed");
                break;
            }
            if(daysParsed >= 3){
                break;
            }
        }
    }

    private boolean isBookeable(HtmlElement item) {
        return "Book".equals(((HtmlElement)item.getFirstByXPath(".//td[7]/a")).getTextContent());
    }

    private boolean isClassName(String name, HtmlElement item) {
        return name.replaceAll("\"","").equals(((HtmlElement)item.getFirstByXPath(".//td[2]/span/a")).getTextContent());
    }

    private boolean isClassTime(String time, HtmlElement item) {
        return time.replaceAll("\"","").equals(((HtmlElement)item.getFirstByXPath(".//td[1]/span")).getTextContent());
    }

}
