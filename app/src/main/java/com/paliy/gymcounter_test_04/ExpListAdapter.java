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

public class ExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;

    public ExpListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<String>> _listDataChild) {
        this.context = context;
        this._listDataHeader = expandableListTitle;
        this._listDataChild = _listDataChild;
    }

    public void setNewItems(List<String> listDataHeader,HashMap<String, List<String>> listChildData) {
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

//            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.child_view, null);
//        }
//
//        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
//        textChild.setText(mGroups.get(groupPosition).get(childPosition));
//
//        Button button = (Button)convertView.findViewById(R.id.editListChildItemButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext,"button is pressed",Toast.LENGTH_SHORT).show();
//            }
//        });


        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        textChild.setText(expandedListText);

        Button button = (Button)convertView.findViewById(R.id.editListChildItemButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "button is pressed", Toast.LENGTH_SHORT).show();
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
}


//
//
//    private ArrayList<ArrayList<String>> mGroups;
//    private Context mContext;
//
//    public ExpListAdapter (Context context,ArrayList<ArrayList<String>> groups){
//        mContext = context;
//        mGroups = groups;
//    }
//
//    @Override
//    public int getGroupCount() {
//        return mGroups.size();
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return mGroups.get(groupPosition).size();
//    }
//
//    @Override
//    public Object getGroup(int groupPosition) {
//        return mGroups.get(groupPosition);
//    }
//
//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//        return mGroups.get(groupPosition).get(childPosition);
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
//                             ViewGroup parent) {
//
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.group_view, null);
//        }
//
//        if (isExpanded){
//            //Изменяем что-нибудь, если текущая Group раскрыта
//        }
//        else{
//            //Изменяем что-нибудь, если текущая Group скрыта
//        }
//
//        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
//
//        //#ToDo
//        String name =  getGroup(groupPosition).toString();
//        textGroup.setText(name);
//        ImageView imageView = (ImageView) convertView.findViewById(R.id.trashImB);
//
//        textGroup.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        textGroup.setSingleLine();
//        textGroup.setSelected(true);
//
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("TRASH! # " + groupPosition);
//            }
//        });
//
//        return convertView;
//
//    }
//
//    @Override
//    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
//                             View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.child_view, null);
//        }
//
//        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
//        textChild.setText(mGroups.get(groupPosition).get(childPosition));
//
//        Button button = (Button)convertView.findViewById(R.id.editListChildItemButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext,"button is pressed",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return convertView;
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
//
//}