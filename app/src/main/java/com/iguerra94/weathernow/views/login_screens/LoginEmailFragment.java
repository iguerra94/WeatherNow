package com.iguerra94.weathernow.views.login_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.utils.FragmentUtils;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class LoginEmailFragment extends Fragment implements View.OnClickListener {

    public LoginEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_email, container, false);

        AppBarLayout appBarLayoutLight = getActivity().findViewById(R.id.loginToolbarLightAppBarLayout);
        appBarLayoutLight.setVisibility(View.GONE);

        AppBarLayout appBarLayoutDark = getActivity().findViewById(R.id.loginToolbarDarkAppBarLayout);

        appBarLayoutDark.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
        appBarLayoutDark.setVisibility(View.VISIBLE);

        SimpleToolbar loginEmailToolbarDark = getActivity().findViewById(R.id.loginToolbarDark);
        ((AppCompatActivity) getActivity()).setSupportActionBar(loginEmailToolbarDark);

        loginEmailToolbarDark.setTitle("");
        loginEmailToolbarDark.setTitleTextColor(getActivity().getResources().getColor(R.color.colorBlack));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        Button btnContinueLoginEmailOption = v.findViewById(R.id.btnContinueLoginEmailOption);
        btnContinueLoginEmailOption.setOnClickListener(this);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            LoginOptionsFragment loginOptionsFragment = new LoginOptionsFragment();
            FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginOptionsFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinueLoginEmailOption) {
            LoginPasswordFragment loginPasswordFragment = new LoginPasswordFragment();
            FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginPasswordFragment);
        }
    }
}