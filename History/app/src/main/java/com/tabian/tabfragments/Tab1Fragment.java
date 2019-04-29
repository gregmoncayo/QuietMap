package com.tabian.tabfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    private MyExpandableListAdapter expandableListView;
    private Button btnTEST;
    List l;
    ExpandableListAdapter expandableListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        btnTEST = (Button) view.findViewById(R.id.btnTEST);
       // btnTEST.setVisibility(View.INVISIBLE);

        location l = new location();
        String n = l.toString();
        ArrayList<String> P = new ArrayList<String>();
        HashMap<String, List<String>> item = new HashMap<>();
        P.add("January 1st");

        ArrayList<String> s = new ArrayList<>();
        s.add("January");
        s.add("February");
        s.add("March");
        s.add("April");
        s.add("May");
        s.add("June");
        s.add("July");
        s.add("August");
        s.add("September");
        s.add("October");
        s.add("November");
        s.add("December");

        ListView lv = (ListView) view.findViewById(R.id.listView1);
        item.put("January 1st", s);
        MyExpandableListAdapter adapt = new MyExpandableListAdapter(item);

        ArrayAdapter<String> lva = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, s);
        lv.setAdapter(lva);
/*
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str[] = new String[] {"January 2nd"};
                Toast.makeText(getActivity(), str[i],Toast.LENGTH_SHORT).show();
            }
        });
        */

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_away);

        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(Tab1Fragment.this.getActivity(), February.class);
                startActivity(j);
            }
        });

        return view;
    }

}