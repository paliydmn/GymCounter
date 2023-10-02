package com.paliy.gymcounter_test_04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class ExpListAdapter extends BaseExpandableListAdapter implements AdapterOnClickHandler{

    private Context context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;

    private AdapterOnClickHandler handler;
    public void setOnClickHandler(AdapterOnClickHandler handler) {
        this.handler = handler;
    }
    public ExpListAdapter(Context context, List<String> expandableListTitle,
                          HashMap<String, List<String>> _listDataChild) {
        this.context = context;
        this._listDataHeader = expandableListTitle;
        this._listDataChild = _listDataChild;
    }

    public void setNewItems(List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this._listDataChild.get(this._listDataHeader.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        textChild.setText(expandedListText);
        Button editEx = (Button) convertView.findViewById(R.id.editListChildItemButton);
        Button deleteEx = (Button) convertView.findViewById(R.id.deleteListChildItemButton);

//hook for handling on click on elements, forward onclick to Main activity
        editEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "EDIT button is pressed", Toast.LENGTH_SHORT).show();
                String editAction = "edit";
                handler.onClick(editAction, getGroup(listPosition).toString(), expandedListText);
            }
        });

        deleteEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(view.getContext(), "EDIT button is pressed: " + getGroup(listPosition) +" ->  "+ getGroup(listPosition), Toast.LENGTH_SHORT).show();
                handler.onClick("delete", getGroup(listPosition).toString(), expandedListText );
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this._listDataChild.get(this._listDataHeader.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this._listDataHeader.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.textGroup);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public void onClick(String action, String groupName, String childName) {

    }
}