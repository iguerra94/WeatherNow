package com.iguerra94.weathernow.views.main_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class ProfileScreenFragment extends Fragment {

    public ProfileScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        AppBarLayout appBarLayout = getActivity().findViewById(R.id.mainToolbarAppBarLayout);
        appBarLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));

        SimpleToolbar profileScreenToolbar = getActivity().findViewById(R.id.mainToolbar);

        profileScreenToolbar.getBackground().setAlpha(0);
        profileScreenToolbar.setNavigationIcon(R.drawable.ic_sort);

        profileScreenToolbar.setTitle("Perfil");
        profileScreenToolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.colorBlack));

        return v;
    }

}