package com.tabian.tabfragments;

import java.util.Random;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class demo {
    public static void Test () {
        ArrayList list = new ArrayList();
        Date current = new Date();
        int mon = 0;
        int year = 2016;
        String Month = "\n";
        String Day = "\n";

        for(int i = 0; i < 300; ++i){
            location working = new location(current, "temp", false);
            Random rand = new Random();
            int day = rand.nextInt(7);
            String dayOfWeek;
            int start = 1;
            year = 2017;
            Day = "Sun";
            Month = "Jan";

/*		if(day == 0)
			working.setDay("Mon");
		else if( day == 1)
			working.setDay("Tue");
		else if (day == 2)
			working.setDay("Wed");
		else if (day == 3)
			working.setDay("Thu");
		else if (day == 4)
			working.setDay("Fri");
		else if (day == 5)
			working.setDay("Sat");
		else if( day == 6)
			working.setDay("Sun");

		if(i % 25 == 0 && i != 0)
			++mon;
		if(mon == 0)
			working.setMonth("Jan");
		else if (mon == 1)
			working.setMonth("Feb");
		else if (mon == 2)
			working.setMonth("Mar");
		else if (mon == 3)
			working.setMonth("Apr");
		else if (mon == 4)
			working.setMonth("May");
		else if (mon == 5)
			working.setMonth("Jun");
		else if (mon == 6)
			working.setMonth("Jul");
		else if (mon == 7)
			working.setMonth("Aug");
		else if (mon == 8)
			working.setMonth("Sep");
		else if (mon == 9)
			working.setMonth("Oct");
		else if (mon == 10)
			working.setMonth("Nov");
		else if (mon == 11)
			working.setMonth("Dec");

		int date = rand.nextInt(31);

		if(i % 100 == 0 && i != 0)
			++year;

*/
            ++start;
            working.setDay(Day);
            working.setYear(year);
            working.setDate(start);
            working.setMonth(Month);

//		working.setYear(year);
//		working.setDate(date + 1);

            boolean dir = rand.nextBoolean();
            working.setMove(dir);

            int loc = rand.nextInt(5);

            if(loc == 0)
                working.setLocation("Home");
            else if (loc == 1)
                working.setLocation("School");
            else if (loc == 2)
                working.setLocation("Library");
            else if (loc == 3)
                working.setLocation("Movie theature");
            else if(loc == 4)
                working.setLocation("AMC");

            list.add(0, working);

        }

        /* real code starts here */
        System.out.println("Random values have been entered for locations for testing");
        System.out.println( "Here is the ammount of locations currently entered: " + list.size() + "\n\nNow printing top 25" );

        for(int i = 0; i < 25; ++i){
            System.out.println( list.get(i) );
        }

        int index = 0, pastIndex = 0;
        ArrayList<location> p = new ArrayList<location>();
        String command = "quit", pastComm = "quit";
        Scanner reader = new Scanner(System.in);
        Scanner readint = new Scanner(System.in);
        System.out.println("\nWhat would you like to do next?\n");
        menue();
        boolean run = true;
        while(run){
            index = 0;
            command = reader.nextLine();

            if(command.equals("Next") ){
                command = pastComm;
                index = pastIndex;
            }

            if(command.equals("Quit") )
                run = false;
            else if (command.equals("Year") ) {
                int tempYear = 2019;
                System.out.println("What year would you like to see");
                tempYear = readint.nextInt();
                pastIndex = listByYear(list, p, tempYear, index);
                print(p);
            }
            else if(command.equals("Month" ) ) {
                System.out.println("What month would you like to see");
                String tempMonth;
                tempMonth = reader.nextLine();
                pastIndex = listByMonth(tempMonth, list, p, index);
                print(p);
            }
            else if (command.equals("Day" ) ){
                System.out.println("What day");			// need to get the specific day you want
                String dayOfWeek;
                dayOfWeek = reader.nextLine();
                System.out.println(dayOfWeek);
                pastIndex = listByDay(dayOfWeek, list, p , index);
                print(p);
            }
            else if (command.equals("Weekend") ){
                pastIndex = listByWeekend(list, p, index);
                print(p);
            }
            else if(command.equals("Weekday") ){
                pastIndex = listByWeekday(list, p , index);
                print(p);
            }
            else if (command.equals("25") ){
                pastIndex = next25(list, p, index);
                print(p);
            }
            else if(command.equals("Menue") )
                menue();

            pastComm = command;


        }
    }
    public static void print(ArrayList<location> p){		// print whatever is in the small 'print' array
        int line = 1;
        for(int i = 0; i < p.size(); ++i){
            System.out.println(i + ": " + p.get(i).toString() );
        }
    }
    public static int next25(ArrayList<location> l, ArrayList arr, int index){			// find the next 25 in chronological order
        arr.clear();

        while(arr.size() < 25 && index < l.size() ){
            arr.add(l.get(index) );
            ++index;
        }
        return index;
    }
    public static int listByYear(ArrayList<location> l, ArrayList arr, int y, int index){		// find the top 25 values in a certain year
        arr.clear();											// linear time
        while(arr.size() < 25 && index < l.size() ){
            if(l.get(index).getYear() == y)
                arr.add( l.get(index) );
            ++index;
        }
        return index;
    }

    public static int listByMonth(String mon, ArrayList<location> l, ArrayList arr, int index){	// find top 25 of a given month
        arr.clear();
        while(arr.size() < 25 && index < l.size() ){
            if(l.get(index).getMonth().equals(mon)  )
                arr.add( l.get(index) );
            ++index;
        }
        return index;
    }

    public static int listByWeekend(ArrayList<location> l, ArrayList arr, int index ){		//find the top 25 values for the weekend
        arr.clear();											// linear time
        while (arr.size() < 25 && index < l.size() ){
            if(isWeekend( l.get(index).getDay() ) )
                arr.add( l.get(index) );
            ++index;
        }
        return index;
    }

    public static int listByWeekday(ArrayList<location> l, ArrayList arr, int index){		// find the top 25 valued for week days
        arr.clear();											// linear time
        while (arr.size() < 25 && index < l.size() ){
            if(!isWeekend( l.get(index).getDay() ) )
                arr.add( l.get(index) );
            ++index;
        }
        return index;
    }

    public static boolean isMonth(String mon){
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

    public static boolean isWeekend(String d){		// helper function to find weekend days
        if (d == "Sat" || d == "Sun" )
            return true;
        else return false;
    }
    public static void menue(){
        System.out.println("Sort by most recent\tenter 25");
        System.out.println("Sort by day\t\tenter Day");
        System.out.println("Sort by year\t\tenter Year");
        System.out.println("Sort by month\t\tenter Month");
        System.out.println("Sort by weekdays\tenter Weekday");
        System.out.println("Sort by weekends\tenter Weekend");
        System.out.println("Get next 25\t\tNext");
        System.out.println("Quit\t\t\tQuit");

    }
    public static int listByDay(String d, ArrayList<location> l, ArrayList arr, int index ){	// find the top 25 of a specific day
        arr.clear();											// linear time
        while(arr.size() < 25 && index < l.size() ){
            if( l.get(index).getDay().equals( d) ){
                arr.add( l.get(index) );
            }
            ++index;
        }
        return index;
    }


}
