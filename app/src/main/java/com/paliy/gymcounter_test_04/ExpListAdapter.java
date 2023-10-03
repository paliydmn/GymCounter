package com.paliy.gymcounter_test_04;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import static com.paliy.gymcounter_test_04.OnClickActions.CREATE_EXERCISE;
import static com.paliy.gymcounter_test_04.OnClickActions.DELETE_EXERCISE;
import static com.paliy.gymcounter_test_04.OnClickActions.DELETE_SET;
import static com.paliy.gymcounter_test_04.OnClickActions.EDIT_EXERCISE;
import static com.paliy.gymcounter_test_04.OnClickActions.EDIT_SET;
import static com.paliy.gymcounter_test_04.OnClickActions.SUBMIT_SET_TO_MAIN_VIEW;

public class ExpListAdapter extends BaseExpandableListAdapter implements AdapterOnClickHandler {

    private final Context context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;

    private AdapterOnClickHandler handler;

    public ExpListAdapter(Context context, List<String> expandableListTitle,
                          HashMap<String, List<String>> _listDataChild) {
        this.context = context;
        this._listDataHeader = expandableListTitle;
        this._listDataChild = _listDataChild;
    }

    public void setOnClickHandler(AdapterOnClickHandler handler) {
        this.handler = handler;
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

        EditText editTextChild = convertView.findViewById(R.id.editTextChild);

        TextView textChild = convertView.findViewById(R.id.textChild);

        editTextChild.setText(expandedListText);
        textChild.setText(expandedListText);
        Button editExBtn = convertView.findViewById(R.id.editListChildItemButton);
        Button deleteExBtn = convertView.findViewById(R.id.deleteListChildItemButton);

//hook for handling on click on elements, forward onclick to Main activity

        editExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //#ToDo code review is need to cleanup it
                String editedVal = editTextChild.getText().toString();
                textChild.setText(editedVal);
                _listDataChild.get(_listDataHeader.get(listPosition)).set(expandedListPosition, editedVal);
                if (editTextChild.getVisibility() == View.VISIBLE) {
                    editExBtn.setBackgroundResource(R.drawable.edit_pen);
                    textChild.setVisibility(View.VISIBLE);
                    editTextChild.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    handler.onClick(EDIT_EXERCISE, getGroup(listPosition).toString(), editedVal, expandedListText);
                } else {
                    textChild.setVisibility(View.GONE);
                    editTextChild.setVisibility(View.VISIBLE);
                    editExBtn.setBackgroundResource(R.drawable.enter_changes_light_2);
                }
            }
        });
        deleteExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                handler.onClick(DELETE_EXERCISE, getGroup(listPosition).toString(), expandedListText, expandedListText);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

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
        String set_name = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }
        TextView setNameTV = convertView.findViewById(R.id.setNameTV);
        EditText editSetNameEdT = convertView.findViewById(R.id.editSetNameEdT);
        setNameTV.setText(set_name);
        editSetNameEdT.setText(set_name);

        TextView addNewExBtn = convertView.findViewById(R.id.addNewExTVBtn);
        ImageView trashImBtn = convertView.findViewById(R.id.trashImB);
        ImageView editSetImBtn = convertView.findViewById(R.id.editSetImBtn);
        ImageView applySetImBtn = convertView.findViewById(R.id.applySetImB);

        editSetImBtn.setOnClickListener(view -> {
            String editedSetNameVal = editSetNameEdT.getText().toString();
            setNameTV.setText(editedSetNameVal);
            if (editSetNameEdT.getVisibility() == View.VISIBLE) {
                editSetImBtn.setBackgroundResource(R.drawable.edit_pen);
                setNameTV.setVisibility(View.VISIBLE);
                editSetNameEdT.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                handler.onClick(EDIT_SET, set_name, editedSetNameVal, set_name);
            } else {
                setNameTV.setVisibility(View.INVISIBLE);
                editSetNameEdT.setVisibility(View.VISIBLE);
                editSetImBtn.setBackgroundResource(R.drawable.enter_changes_light_2);
            }
        });
//Add New Ex to current SET
        addNewExBtn.setOnClickListener(view -> {
            handler.onClick(CREATE_EXERCISE, set_name, null, null);
        });
//Delete Set with all exercises.
        trashImBtn.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            handler.onClick(DELETE_SET, set_name, null, null);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        applySetImBtn.setOnClickListener(view -> {
            //ToDo Apply Set to main view
            handler.onClick(SUBMIT_SET_TO_MAIN_VIEW, set_name, null, null);

        });
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
    public void onClick(OnClickActions action, String groupName, String childName, String old_name) {

    }
}