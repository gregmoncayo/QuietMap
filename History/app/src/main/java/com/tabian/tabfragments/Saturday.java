package com.tabian.tabfragments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Saturday extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saturday);

        demo dem = new demo();
        dem.init();

        String[] DaysList = {"Monday, February 18th, 2019", "Tuesday, November 20th, 2018", "Wednesday, October 31st, 2018", "Thursday, August 30th, 2018", "Saturday, June 9th, 2018", "Sunday, Monday 7th, 2017"};

      //  ListAdapter theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DaysList);

        ArrayList<location> mlist = dem.listByDay("Mon");
        mlist.addAll( dem.listByDay("Tue"));
        mlist.addAll( dem.listByDay("Wed"));
        mlist.addAll( dem.listByDay("Thu"));
        mlist.addAll( dem.listByDay("Fri"));
        mlist.addAll( dem.listByDay("Sat"));
        mlist.addAll( dem.listByDay("Sun"));

        ListAdapter adapto = new ArrayAdapter<location>(this, android.R.layout.simple_list_item_1
                , mlist);

        ListView theListView = (ListView) findViewById(R.id.Years);

        theListView.setAdapter(adapto);
    }
}
