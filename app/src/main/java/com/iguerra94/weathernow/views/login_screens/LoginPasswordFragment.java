package com.iguerra94.weathernow.views.login_screens;

import android.content.Intent;
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
import com.iguerra94.weathernow.views.main_screens.MainScreenActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class LoginPasswordFragment extends Fragment implements View.OnClickListener {

    public LoginPasswordFragment() {
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
        View v = inflater.inflate(R.layout.fragment_login_password, container, false);

        AppBarLayout appBarLayoutLight = getActivity().findViewById(R.id.loginToolbarLightAppBarLayout);
        appBarLayoutLight.setVisibility(View.GONE);

        AppBarLayout appBarLayoutDark = getActivity().findViewById(R.id.loginToolbarDarkAppBarLayout);

        appBarLayoutDark.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
        appBarLayoutDark.setVisibility(View.VISIBLE);

        SimpleToolbar loginPasswordToolbarDark = getActivity().findViewById(R.id.loginToolbarDark);
        ((AppCompatActivity) getActivity()).setSupportActionBar(loginPasswordToolbarDark);

        loginPasswordToolbarDark.setTitle("");
        loginPasswordToolbarDark.setTitleTextColor(getActivity().getResources().getColor(R.color.colorBlack));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        Button btnLoginUser = v.findViewById(R.id.btnLoginUser);
        btnLoginUser.setOnClickListener(this);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            LoginEmailFragment loginEmailFragment = new LoginEmailFragment();
            FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginEmailFragment);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLoginUser) {
            goToMainScreenActivity();
        }
    }

    private void goToMainScreenActivity() {
        Intent i = new Intent(getActivity(), MainScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        getActivity().finish();
    }
}