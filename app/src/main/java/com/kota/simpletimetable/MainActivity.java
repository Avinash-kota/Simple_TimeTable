package com.kota.simpletimetable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Slot> monList,tueList,wedList,thuList,friList,satList;
    private  boolean editMode;
    private long backPressedTime;
    private Toast backToast;

    FloatingActionButton fabMain, fabAdd, fabEdit;
    Float translationY = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    Boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Time Table");
        setSupportActionBar(toolbar);*/

        initFabMenu();
        loadData();

        tabLayout = findViewById(R.id.tab_tabview);
        viewPager = findViewById(R.id.tab_viewpager);

        ArrayList<String> titles = new ArrayList<>();
        titles.add("MON");
        titles.add("TUE");
        titles.add("WED");
        titles.add("THU");
        titles.add("FRI");
        titles.add("SAT");

        viewPagerSetup(viewPager,titles);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void viewPagerSetup(ViewPager viewPager, ArrayList<String> titles) {
        timeTable_adapter adapter = new  timeTable_adapter(getSupportFragmentManager());
        Timetable_fragment timetable_fragment = new Timetable_fragment();
        for (int i=0; i<titles.size(); i++){
            Bundle bundle =  new Bundle();
            bundle.putString("dayTitle",titles.get(i));

            Gson gson = new Gson();
            String mondayListString = gson.toJson(monList);
            String tuesdayListString = gson.toJson(tueList);
            String wednesdayListString = gson.toJson(wedList);
            String thursdayListString = gson.toJson(thuList);
            String fridayListString = gson.toJson(friList);
            String saturdayListString = gson.toJson(satList);
            if(i==0){
                bundle.putString("listKey",mondayListString);
                bundle.putString("day","Monday");
            }else if (i==1){
                bundle.putString("listKey",tuesdayListString);
                bundle.putString("day","Tuesday");
            }else if (i==2){
                bundle.putString("listKey",wednesdayListString);
                bundle.putString("day","Wednesday");
            }else if (i==3){
                bundle.putString("listKey",thursdayListString);
                bundle.putString("day","Thursday");
            }else if (i==4){
                bundle.putString("listKey",fridayListString);
                bundle.putString("day","Friday");
            }else if (i==5){
                bundle.putString("listKey",saturdayListString);
                bundle.putString("day","Saturday");
            }

            timetable_fragment.setArguments(bundle);
            adapter.addFragment(timetable_fragment, titles.get(i));
            timetable_fragment = new Timetable_fragment();
        }
        viewPager.setAdapter(adapter);
    }


    private class timeTable_adapter extends FragmentPagerAdapter {

        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title){
            arrayList.add(title);
            fragmentList.add(fragment);

        }

        public timeTable_adapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position);
        }
    }

    private void initFabMenu() {
        fabMain = findViewById(R.id.fabMain);
        fabAdd = findViewById(R.id.fab_add);
        fabEdit = findViewById(R.id.fab_edit);

        fabAdd.setAlpha(0f);
        fabEdit.setAlpha(0f);

        fabAdd.setTranslationY(translationY);
        fabEdit.setTranslationY(translationY);


        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addSlot_intent = new Intent(MainActivity.this, AddSlot.class);
                startActivity(addSlot_intent);
            }
        });
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(TimeTable.this,"Under Development",Toast.LENGTH_LONG).show();


                enable_editMode();

            }
        });
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("timetable", MODE_PRIVATE);
        editMode = sharedPreferences.getBoolean("edit_enable",false);
        loadData();
        if(editMode)
            enable_editMode();
        else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
                return;
            } else {
                backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    private void enable_editMode(){
        SharedPreferences sharedPreferences = getSharedPreferences("timetable", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editMode = sharedPreferences.getBoolean("edit_enable",false);
        editMode = !editMode;
        editor.putBoolean("edit_enable", editMode);
        editor.apply();

        for (int i=0;i<monList.size();i++){
            monList.get(i).setEdit_mode(editMode);
        }
        for (int i=0;i<tueList.size();i++){
            tueList.get(i).setEdit_mode(editMode);
        }
        for (int i=0;i<wedList.size();i++){
            wedList.get(i).setEdit_mode(editMode);
        }
        for (int i=0;i<thuList.size();i++){
            thuList.get(i).setEdit_mode(editMode);
        }
        for (int i=0;i<friList.size();i++){
            friList.get(i).setEdit_mode(editMode);
        }
        for (int i=0;i<satList.size();i++){
            satList.get(i).setEdit_mode(editMode);
        }
        updateData();
        reload();
    }

    private void openMenu() {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fabAdd.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabEdit.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();


    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fabAdd.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabEdit.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();


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

    private void updateData() {
        SharedPreferences sharedPreferences = getSharedPreferences("timetable", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();


        String json = gson.toJson(monList);
        editor.putString("Monday", json);
        editor.apply();

        json = gson.toJson(tueList);
        editor.putString("Tuesday", json);
        editor.apply();

        json = gson.toJson(wedList);
        editor.putString("Wednesday", json);
        editor.apply();

        json = gson.toJson(thuList);
        editor.putString("Thursday", json);
        editor.apply();

        json = gson.toJson(friList);
        editor.putString("Friday", json);
        editor.apply();

        json = gson.toJson(satList);
        editor.putString("Saturday", json);
        editor.apply();

    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}