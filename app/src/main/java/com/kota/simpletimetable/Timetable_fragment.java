package com.kota.simpletimetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class Timetable_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String dayKey;
    private ArrayList<Slot> list;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public Timetable_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Timetable_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Timetable_fragment newInstance(String param1, String param2) {
        Timetable_fragment fragment = new Timetable_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void removeItem(int position){
        list.remove(position);
        recyclerAdapter.notifyItemRemoved(position);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("timetable", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();


        String json = gson.toJson(list);
        editor.putString(dayKey, json);
        editor.apply();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
//      TextView textView = view.findViewById(R.id.demo);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerSlots);
//        String sTitle = getArguments().getString("dayTitle");
        String listString = getArguments().getString("listKey");
        dayKey = getArguments().getString("day");
        Type type = new TypeToken<ArrayList<Slot>>() {}.getType();
        Gson gson = new Gson();
        list = gson.fromJson(listString, type);
//      textView.setText(sTitle);


        recyclerAdapter = new RecyclerViewAdapter(getContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);

        recyclerAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

        return view;
    }

}