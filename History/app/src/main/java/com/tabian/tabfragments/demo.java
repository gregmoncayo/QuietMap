package com.tabian.tabfragments;

import java.util.Random;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class demo {

    private ArrayList<location> list = new ArrayList();

    public void init () {					// just used to initialize dummy date for demonstration
        Date current = new Date();
        int date = 1;
        int year = 2017;
        String Day = "Mon";
        String month = "Dec";
        int start = 1;
        Random rand = new Random();

        for(int i = 0; i < 100; ++i){
            location working = new location(current, "temp", false );	// creat new location variable
            location working2 = new location(current, "temp", false);


            ++date;		// increment the date every time

            if( month == "Dec" && date % 28 == 0 ) {	// if we are in december and the end of the month go to the next year
                date = 1;
                ++year;
                month = nextMonth(month);
                Day = nextDay(Day);
            }
            else if(date % 28 == 0 ) {			// we are at the end of the month so go to the next month
                month = nextMonth(month);
                Day = nextDay(Day);
                date = 1;
            }

            Day = nextDay(Day);

            working.setDay(Day);			// set paramaters for both variables
            working.setDate(date);
            working.setMonth(month);
            working.setYear(year);
            working.setMove(true);

            working2.setDay(Day);			// coppy just leaving
            working2.setDate(date);
            working2.setMonth(month);
            working2.setYear(year);
            working2.setMove(false);


            int skip = rand.nextInt(10) + 1;	// skip at random to make date more irregular
            if(skip % 3 == 0 ){}
            else {
                list.add(working);		// add new location to the list
                list.add(working2);
            }
        }
    }
    private String nextDay(String d){
        if(d == "Mon")
            return "Tue";
        else if (d == "Tue")
            return "Wed";
        else if (d == "Wed")
            return "Thu";
        else if (d == "Thu")
            return "Fri";
        else if (d == "Fri")
            return "Sat";
        else if (d == "Sat")
            return "Sun";
        else return "Mon";

    }
    private String nextMonth(String m){
        if(m == "Jan" )
            return "Feb";
        else if (m == "Feb" )
            return "Mar";
        else if (m == "Mar" )
            return "Apr";
        else if (m == "Apr" )
            return "May";
        else if (m == "May" )
            return "Jun";
        else if (m == "Jun" )
            return "Jul";
        else if (m == "Jul" )
            return "Aug";
        else if (m == "Aug" )
            return "Sep";
        else if (m == "Sep" )
            return "Oct";
        else if (m == "Oct" )
            return "Nov";
        else if(m == "Nov")
            return "Dec";
        else return "Jan";
    }

    // print the entire array
    public void print(){
        int line = 1;
        for(int i = 0; i < list.size(); ++i) {
            System.out.println(line + ": " + list.get(i).toString() );
            ++line;
        }
    }
    public ArrayList listByYear(int y){		//find the top 25 values in a certain year
        ArrayList ret = new ArrayList();
        for(int i = 0; i < list.size(); ++i){
            if(list.get(i).getYear() == y)
                ret.add(list.get(i));
        }
        return ret;
    }

    public ArrayList listByMonth(String mon){			// list elements by a given month 'mon'
        ArrayList ret = new ArrayList();
        if( isMonth(mon) ) {
            for(int i = 0; i < list.size(); ++i){
                if(list.get(i).getMonth() == mon)
                    ret.add(list.get(i));
            }
        }
        return ret;
    }

    public ArrayList listByDay(String d){				// list elements by a given day	'd'
        ArrayList ret = new ArrayList();
        if(isDay(d) ) {
            for(int i = 0; i < list.size(); ++i)
                if(list.get(i).getDay() == d)
                    ret.add(list.get(i));
        }
        return ret;
    }

    /* Helper functions */
    private boolean isMonth(String mon){
        if(mon == "Jan" || mon == "Feb" || mon == "Mar" || mon == "Apr")
            return true;
        else if (mon == "May" || mon == "Jun" || mon == "Jul" )
            return true;
        else if(mon == "Aug" || mon == "Sep" || mon == "Oct")
            return true;
        else if (mon == "Nov" || mon == "Dec" )
            return true;
        else return false;
    }

    private boolean isDay(String d) {
        if( d == "Mon" || d == "Tue" || d == "Wed" )
            return true;
        else if (d == "Thu" || d == "Fri" || d == "Sat" || d == "Sun" )
            return true;
        else return false;
    }

}
