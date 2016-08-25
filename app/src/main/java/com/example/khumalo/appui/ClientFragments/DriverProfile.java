package com.example.khumalo.appui.ClientFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.khumalo.appui.R;



public class DriverProfile extends Fragment {


    public static DriverProfile newInstance() {
        DriverProfile fragment = new DriverProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DriverProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_profile, container, false);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}
