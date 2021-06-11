package com.kota.simpletimetable;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AddSlot extends AppCompatActivity {

    private String day;
    private TextInputLayout Title,Code,Room;
    private Button save;
    private TextView time_start,time_end;
    private int sTime_H,sTime_M,eTime_H,eTime_M;

    public boolean edit_enable;

    private ArrayList<Slot> monList,tueList,wedList,thuList,friList,satList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slot);

        Spinner spinner = findViewById(R.id.days_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), day, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Title = findViewById(R.id.addslot_title);
        Code = findViewById(R.id.addslot_code);
        Room = findViewById(R.id.addslot_room);

        loadData();

        time_start = findViewById(R.id.time_start);
        time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddSlot.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                sTime_H = hour;
                                sTime_M = minute;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,sTime_H,sTime_M);
                                time_start.setText(DateFormat.format("hh:mm aa",calendar));
                                time_start.setTextColor(Color.parseColor("#FF737373"));

                            }
                        },12,0,false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(sTime_H,sTime_M);
                timePickerDialog.show();
            }
        });

        time_end = findViewById(R.id.time_end);
        time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddSlot.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                eTime_H = hour;
                                eTime_M = minute;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0,0,0,eTime_H,eTime_M);
                                time_end.setText(DateFormat.format("hh:mm aa",calendar));
                                time_end.setTextColor(Color.parseColor("#FF737373"));

                            }
                        },12,0,false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(eTime_H,eTime_M);
                timePickerDialog.show();
            }
        });

        save = findViewById(R.id.addslot_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent example = new Intent(AddSlot.this, test_timetable.class);
                startActivity(example);*/

                if(validateInput()){
                    String title = Title.getEditText().getText().toString().trim();
                    String code = Code.getEditText().getText().toString().trim();
                    String room = Room.getEditText().getText().toString().trim();
                    saveData(title,code,room,sTime_H,sTime_M,eTime_H,eTime_M);
                    Intent TimeTable = new Intent(AddSlot.this, MainActivity.class);
                    startActivity(TimeTable);
                    finish();
                }
            }
        });
    }

    private boolean validateInput() {
        String title = Title.getEditText().getText().toString().trim();
        boolean res = true;
        if (title.isEmpty()) {
            Title.setError("Field can't be empty");
            res= false;
        } else {
            Title.setError(null);
        }

        String code = Code.getEditText().getText().toString().trim();
        if (code.isEmpty()) {
            Code.setError("Field can't be empty");
            res = false;
        } else {
            Code.setError(null);
        }

        String room = Room.getEditText().getText().toString().trim();
        if (room.isEmpty()) {
            Room.setError("Field can't be empty");
            res = false;
        } else {
            Room.setError(null);
        }

        String stime = time_start.getText().toString().trim();
        if (stime.equals("Time")) {
            time_start.setTextColor(Color.parseColor("#FFE41515"));
            res = false;
        } else {
            time_start.setTextColor(Color.parseColor("#FF737373"));
        }

        String etime = time_end.getText().toString().trim();
        if (etime.equals("Time")) {
            time_end.setTextColor(Color.parseColor("#FFE41515"));
            res = false;
        } else {
            time_end.setTextColor(Color.parseColor("#FF737373"));
        }

        return res;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("timetable", MODE_PRIVATE);
        Gson gson = new Gson();
        String monJson = sharedPreferences.getString("Monday", null);
        String tueJson = sharedPreferences.getString("Tuesday", null);
        String wedJson = sharedPreferences.getString("Wednesday", null);
        String thuJson = sharedPreferences.getString("Thursday", null);
        String friJson = sharedPreferences.getString("Friday", null);
        String satJson = sharedPreferences.getString("Saturday", null);

        edit_enable = sharedPreferences.getBoolean("edit_enable",false);

        Type type = new TypeToken<ArrayList<Slot>>() {}.getType();
        monList = gson.fromJson(monJson, type);
        tueList = gson.fromJson(tueJson, type);
        wedList = gson.fromJson(wedJson, type);
        thuList = gson.fromJson(thuJson, type);
        friList = gson.fromJson(friJson, type);
        satList = gson.fromJson(satJson, type);
        if (monList == null) {
            monList = new ArrayList<>();
        }
        if (tueList == null) {
            tueList = new ArrayList<>();
        }
        if (wedList == null) {
            wedList = new ArrayList<>();
        }
        if (thuList == null) {
            thuList = new ArrayList<>();
        }
        if (friList == null) {
            friList = new ArrayList<>();
        }
        if (satList == null) {
            satList = new ArrayList<>();
        }
    }

    private void saveData(String title, String code, String room,int sTime_H,int sTime_M,int eTime_H,int eTime_M) {
        SharedPreferences sharedPreferences = getSharedPreferences("timetable", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        if(day.equals("Monday")){
            monList.add(new Slot(title,code,room,sTime_H,sTime_M,eTime_H,eTime_M));
            Collections.sort(monList, Slot.sortList);
            String json = gson.toJson(monList);
            editor.putString(day, json);
            editor.apply();
        }else if(day.equals("Tuesday")){
            tueList.add(new Slot(title,code,room,sTime_H,sTime_M,eTime_H,eTime_M));
            Collections.sort(tueList, Slot.sortList);
            String json = gson.toJson(tueList);
            editor.putString(day, json);
            editor.apply();
        }else if(day.equals("Wednesday")){
            wedList.add(new Slot(title,code,room,sTime_H,sTime_M,eTime_H,eTime_M));
            Collections.sort(wedList, Slot.sortList);
            String json = gson.toJson(wedList);
            editor.putString(day, json);
            editor.apply();
        }else if(day.equals("Thursday")){
            thuList.add(new Slot(title,code,room,sTime_H,sTime_M,eTime_H,eTime_M));
            Collections.sort(thuList, Slot.sortList);
            String json = gson.toJson(thuList);
            editor.putString(day, json);
            editor.apply();
        }else if(day.equals("Friday")){
            friList.add(new Slot(title,code,room,sTime_H,sTime_M,eTime_H,eTime_M));
            Collections.sort(friList, Slot.sortList);
            String json = gson.toJson(friList);
            editor.putString(day, json);
            editor.apply();
        }else if(day.equals("Saturday")){
            satList.add(new Slot(title,code,room,sTime_H,sTime_M,eTime_H,eTime_M));
            Collections.sort(satList, Slot.sortList);
            String json = gson.toJson(satList);
            editor.putString(day, json);
            editor.apply();
        }
    }
}