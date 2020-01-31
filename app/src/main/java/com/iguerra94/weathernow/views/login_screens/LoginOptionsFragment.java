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

public class LoginOptionsFragment extends Fragment implements View.OnClickListener {

    public LoginOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_options, container, false);

        AppBarLayout appBarLayoutLight = getActivity().findViewById(R.id.loginToolbarLightAppBarLayout);

        AppBarLayout appBarLayoutDark = getActivity().findViewById(R.id.loginToolbarDarkAppBarLayout);
        appBarLayoutDark.setVisibility(View.GONE);

        appBarLayoutLight.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        appBarLayoutLight.setVisibility(View.VISIBLE);

        SimpleToolbar loginOptionsToolbarLight = getActivity().findViewById(R.id.loginToolbarLight);
        ((AppCompatActivity) getActivity()).setSupportActionBar(loginOptionsToolbarLight);

        loginOptionsToolbarLight.setTitle(R.string.login_options_toolbar_title);
        loginOptionsToolbarLight.setTitleTextColor(getActivity().getResources().getColor(R.color.colorWhite));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        Button btnLoginEmailOption = v.findViewById(R.id.btnLoginEmailOption);
        btnLoginEmailOption.setOnClickListener(this);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLoginEmailOption) {
            LoginEmailFragment loginEmailFragment = new LoginEmailFragment();
            FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginEmailFragment);
        }
    }
}