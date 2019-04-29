package com.tabian.tabfragments;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Year2014 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year2014);

        demo d = new demo();
        d.init();

        String[] YearList = {"January 1st, 2019", "December 30th, 2018", "November 21st, 2018", "October 31st, 2018", "August 9th, 2017"};

        ArrayList<location> mockList = d.listByYear(2017);
        mockList.addAll(d.listByYear(2018));
        mockList.addAll(d.listByYear(2019));


        ListAdapter adapt = new ArrayAdapter<location>(this, android.R.layout.simple_list_item_1
        , mockList);

      //  ListAdapter theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, YearList);

        ListView theListView = (ListView) findViewById(R.id.Years);

        theListView.setAdapter(adapt);
    }
}
