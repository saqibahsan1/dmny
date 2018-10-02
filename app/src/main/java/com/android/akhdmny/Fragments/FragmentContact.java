package com.android.akhdmny.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.akhdmny.R;

import butterknife.ButterKnife;

public class FragmentContact extends Fragment {

    public FragmentContact(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us, container, false);
        ButterKnife.bind(this,view);
        return view;
    }
}
