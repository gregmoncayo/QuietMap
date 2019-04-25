package com.tabian.tabfragments;

/* This class is inteded to be an entity class that sotres data for
 * and individual movement from ether out of a location to in or
 * vice versa, more information at the end of this file */

/* This class is inteded to be an entity class that sotres data for
 * and individual movement from ether out of a location to in or
 * vice versa, more information at the end of this file */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class location{

    // constructors
    public location(){		// defalul constructor
        month = "Jan";
        day = "Mon";
        date = 1;
        year = 1;

        location = null;
        movement = true;	// ture = entering, false = leaving
    }

    public location(String mon, String da, int d, int y, String loc, boolean M){	// constructs using information and cheaks
        // valid data for year, month, day and time

        if ( isMonth(mon) )					// cheak paramaters for valid entries
            month = mon;
        else month = "Jan";

        if ( isDay(da) )
            day = da;
        else day = "Mon";

        if (d > 0 && d < 31 )
            date = d;
        else date = 1;

        if( y > 0 && y <= Year() )		// if year is between 0 and current year
            year = y;
        else year = 1;

        location = loc;						// set location and movement
        movement = M;
    }

    public location(Date d, String loc, boolean M){

        if ( d.toString().contains("Tue") )
            day = "Tue";
        else if (d.toString().contains("Wed") )
            day = "Wed";
        else if (d.toString().contains("Thu") )
            day = "Thu";
        else if (d.toString().contains("Fri") )
            day = "Fri";
        else if (d.toString().contains("Sat") )
            day = "Sat";
        else if (d.toString().contains("Sun") )
            day = "Sun";
        else day = "Mon";

        if ( d.toString().contains("Feb") )
            month = "Feb";
        else if ( d.toString().contains("Mar") )
            month = "Mar";
        else if ( d.toString().contains("Apr") )
            month = "Apr";
        else if ( d.toString().contains("May") )
            month = "May";
        else if ( d.toString().contains("Jun") )
            month = "Jun";
        else if ( d.toString().contains("Jul") )
            month = "Jul";
        else if ( d.toString().contains("Aug") )
            month = "Aug";
        else if ( d.toString().contains("Sep") )
            month = "Sep";
        else if ( d.toString().contains("Oct") )
            month = "Oct";
        else if ( d.toString().contains("Nov") )
            month = "Noc";
        else if ( d.toString().contains("Dec") )
            month = "Dec";
        else month = "Jan";

        date = d.getDate();
        year = (1900 + d.getYear() );		// year must be changed to the acutal year not an offset since 1900
        location = loc;
        movement = M;
    }


    /* Methods */
    public String date(){						// return the date in a string, format
        String ret = "";						// day of week, month, day of month, year

        ret += day;						// converting to strings
        ret += " ";
        ret += month;
        ret += " ";
        ret += date;
        ret += " ";
        ret += Integer.toString(year);

        return ret;
    }

    public String getDay(){						// returns day
        return day;
    }

    public String getMonth(){					// returns month
        return month;
    }

    public int getYear(){						// returns year
        return year;
    }

    public String loc(){						// return location
        return location;
    }

    public String move() {						// return movement in or out of "silent zone"
        if(movement)
            return "arriving";
        else return "Leaving";
    }

    public String toString(){					// returns a string with the given format
        String ret = date() + " " + move() + " " + location;		// Date, location, arriving/leaving
        return ret;
    }

    public void setDay(String a) {					// change day of the week
        if( isDay(a) )
            day = a;
        else day = "Mon";
    }

    public void setDate(int a){					// change day of the month
        if( a > 0 && a < 32 )
            date = a;
    }

    public void setMonth(String a){					// change month
        if( isMonth(a) )
            month = a;
    }

    public void setYear(int a) {					// change year
        if( a > 1 && a <= Year() )
            year = a;
    }

    public void setLocation(String a){				// change location
        location = a;
    }

    public void setMove(boolean a){					// set move
        movement = a;
    }

    /* Member data */
    int date, year;
    String location, month, day;
    boolean movement;

    /* Helper methods */
    /* See if it is a valid month */
    private boolean isMonth(String par) {
        if( par == "Jan" || par == "Feb" || par == "Mar" || par == "Apr" || par == "May" || par == "Jun" ||
                par == "Jul" || par == "Aug" || par == "Sep" || par == "Oct" || par == "Nov" || par == "Dec" )
            return true;
        else return false;
    }

    /* Find the current year */
    private int Year(){
        int theYear = Calendar.getInstance().get(Calendar.YEAR);
        return theYear;
    }
    /* See if it is a valid Day of the week */
    private boolean isDay(String a){
        if(a == "Mon" || a ==  "Tue" || a == "Wed" || a == "Thu" || a == "Fri" || a == "Sat" || a == "Sun" )
            return true;
        else return false;
    }
}

    /*
     * This class is intended to be used in conjuction with a history class. This class only records
     * The date in the form of time, day, date, month year and the location the even takes place
     * and the weather the user is leaving or comming.
     * There are other functions to change the data in each onject, these are intended for code
     * Data class doesn't allow you to easily change and save the Date objects for testing.
     * There are also a function to save the data from this object to a file as well as read the
     * data from a file into the onject
     *
     * All workd for this class was done by Bryce Brooks */

