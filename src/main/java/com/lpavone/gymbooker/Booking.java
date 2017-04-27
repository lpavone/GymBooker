package com.lpavone.gymbooker;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Created by leonardo on 14/03/17.
 */
class Booking {

        public static void main(final String[] args) throws Exception {
            if(args.length == 2 && "autojob".equals(args[0].trim())){
                BookerJob.book(args[1].trim());
            } else {
                System.out.println("Select User, (1) Clau or (2) Leo: ");
                Scanner scanner = new Scanner(System.in);
                HeadlessApp app = new HeadlessApp(Integer.parseInt( scanner.nextLine()) == 1 ? "clau" : "leo");
                List<Workout> availableClasses = app.getAvailableClasses();
                IntStream.range(0, availableClasses.size())
                        .forEach(i -> System.out.println("(" + i + ") " + availableClasses.get(i).getDay() + " - "
                                + availableClasses.get(i).getTime() + " - " + availableClasses.get(i).getName()));
                System.out.println("Enter the class you want to book (separated by comma if more than one): \n==> ");
                scanner = new Scanner(System.in);
                app.storeBooking( scanner.nextLine());
            }
            //WebDriverUtils.disposeWebDriverInstance();
            System.out.println("\n ==> SUCCESSFULLY BOOKED!");
        }

}
