package com.tabian.tabfragments;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class February extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_february);

        demo de = new demo();

        de.init();

      String[] monthList = {"January 1st", "February 14th", "March 7th", "April 1st", "May 5th"};

        ArrayList<location> mList = de.listByMonth("Jan");
        mList.addAll(de.listByMonth("Feb"));
        mList.addAll(de.listByMonth("Mar"));
        mList.addAll(de.listByMonth("Apr"));
        mList.addAll(de.listByMonth("May"));
        mList.addAll(de.listByMonth("Jun"));
        mList.addAll(de.listByMonth("Jul"));
        mList.addAll(de.listByMonth("Aug"));
        mList.addAll(de.listByMonth("Sep"));
        mList.addAll(de.listByMonth("Oct"));
        mList.addAll(de.listByMonth("Nov"));
        mList.addAll(de.listByMonth("Dec"));

        ListAdapter adap = new ArrayAdapter<location>(this, android.R.layout.simple_list_item_1
                , mList);

    //  ListAdapter theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, monthList);

      ListView theListView = (ListView) findViewById(R.id.February);

      theListView.setAdapter(adap);
    }

}
