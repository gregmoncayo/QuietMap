package com.tabian.tabfragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private HashMap<String, List<String>> mStringListHashMap;
    private String[] mListHeaderGroup;

    public MyExpandableListAdapter(HashMap<String, List<String>> mStringListHashMap)
    {
        this.mStringListHashMap = mStringListHashMap;
    }


    public MyExpandableListAdapter(Tab1Fragment tab1Fragment, List<String> listDataHeader, HashMap<String, List<String>> mStringListHashMap) {
        this.mStringListHashMap = mStringListHashMap;
        mListHeaderGroup = mStringListHashMap.keySet().toArray(new String[0]);
    }

    @Override
    public int getGroupCount() {
        return mListHeaderGroup.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return mStringListHashMap.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return mListHeaderGroup[i];
    }

    @Override
    public Object getChild(int i, int j) {
        return mStringListHashMap.get(mListHeaderGroup[i]).get(j);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int j) {
        return i*j;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null)
        {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expandable_list_item, viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(String.valueOf(getGroup(i)));
        return view;
    }

    @Override
    public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup) {
        if (view == null)
        {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expandable_list_item, viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(String.valueOf(getChild(i,j)));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int j) {
        return false;
    }
}
