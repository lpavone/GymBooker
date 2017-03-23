package com.lpavone.gymbooker;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by leonardo on 14/03/17.
 */
class Booking {

        public static void main(final String[] args) throws IOException {
            if(args.length == 2 && "autojob".equals(args[0].trim())){
                BookerJob.book(args[1].trim());
            } else {
                Console c = System.console();
                String user = c.readLine("Select User, (1) Clau or (2) Leo: ");
                App app = new App(Integer.parseInt(user) == 1 ? "clau" : "leo");
                List<Workout> availableClasses = app.getAvailableClasses();
                IntStream.range(0, availableClasses.size())
                        .forEach(i -> System.out.println("(" + i + ") " + availableClasses.get(i).getDay() + " - "
                                + availableClasses.get(i).getTime() + " - " + availableClasses.get(i).getName()));
                String input = c.readLine("Enter the class you want to book (separated by comma if more than one): \n==> ");
                app.storeBooking(input);
            }
            WebDriverUtils.disposeWebDriverInstance();
            System.out.println("\n ==> SUCCESSFULLY BOOKED!");
        }

}
