package com.example.gangaprasadkoturwar.heyywaiter;

/**
 * Created by Gangaprasad.Koturwar on 08-02-2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MyOrder extends Fragment {

    View myFragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_mydish, container, false);

        return myFragmentView;
    }

}

