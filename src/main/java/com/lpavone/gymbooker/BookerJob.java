package com.lpavone.gymbooker;

import java.util.List;

/**
 * Created by leonardo on 19/03/17.
 */
class BookerJob {

    public static void book(String user){
        App app = new App();
        CsvUtils csvUtils = new CsvUtils();
        app.setUser(user);
        app.doLogin();
        System.out.println(String.format("Login done for %s",user));
        List<String> csvContent = csvUtils.readCsv(user + Constants.FILENAME);
        System.out.println(String.format("File for %s has been read!", user));
        csvContent.forEach(c -> {
            app.goToTimetable();
            System.out.println(String.format("Attempting to book: %s", c));
            String[] classInfo = c.split(",");
            app.findWorkouts(classInfo[0], classInfo[1]);
        });
        app.doLogout();
        System.out.println("Logout done.");
    }

}
