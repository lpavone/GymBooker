package com.lpavone.gymbooker;

/**
 * Created by leonardo on 14/03/17.
 */
/**
 * Represent a single workout class.
 */
class Workout{

    private String day;
    private String name;
    private String time;//should be in format "19:15"

    public Workout(String day, String name, String time) {
        this.day = day;
        this.name = name;
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "day='" + day + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}