package com.iguerra94.weathernow.views.login_screens;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.FragmentLoginEmailBinding;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyEmailException;
import com.iguerra94.weathernow.db.entities.exceptions.UserEmailNotRegisteredException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongEmailFormatException;
import com.iguerra94.weathernow.utils.FragmentUtils;
import com.iguerra94.weathernow.utils.UserUtils;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfUserExistsByEmailTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.db.VerifyIfUserExistsByEmailAsyncTask;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class LoginEmailFragment extends Fragment implements View.OnClickListener, IVerifyIfUserExistsByEmailTaskResponse {

    private FragmentLoginEmailBinding binding;

    public LoginEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        String loginEmail = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.LOGIN_EMAIL);

        if (!loginEmail.isEmpty()) {
            binding.loginEmailEditText.setText(loginEmail);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_email, container, false);

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

        binding.loginEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearEmailFieldError();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.btnContinueLoginEmailOption.setOnClickListener(this);

        return binding.getRoot();
    }

    private void clearEmailFieldError() {
        if (binding.loginEmailLayout.getError() != null && !binding.loginEmailLayout.getError().toString().isEmpty()) {
            binding.loginEmailLayout.setError(null);
            binding.loginEmailLayout.setErrorEnabled(false);
        }
    }

    private void verifyEmailOnButtonPressed() throws EmptyEmailException, WrongEmailFormatException {
        String email = binding.loginEmailEditText.getText().toString();
        clearEmailFieldError();

        UserUtils.checkEmptyEmailAtLogin(getActivity(), email);
        UserUtils.checkEmailFormat(getActivity(), email);

        new VerifyIfUserExistsByEmailAsyncTask(getActivity(), email, this).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.LOGIN_EMAIL, "");

            LoginOptionsFragment loginOptionsFragment = new LoginOptionsFragment();
            FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginOptionsFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContinueLoginEmailOption) {
            try {
                verifyEmailOnButtonPressed();
            } catch (EmptyEmailException | WrongEmailFormatException e) {
                binding.loginEmailLayout.setErrorEnabled(true);
                binding.loginEmailLayout.setError(e.getMessage());
            }
        }
    }

    @Override
    public void onVerifyIfUserExistsByEmailTaskDone(boolean userExists) {
        if (!userExists) try {
            String currentLocale = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "El email ingresado no esta registrado." : "The email entered is not registered.";

            throw new UserEmailNotRegisteredException(message);
        } catch (UserEmailNotRegisteredException e) {
            binding.loginEmailLayout.setErrorEnabled(true);
            binding.loginEmailLayout.setError(e.getMessage());
            return;
        }

        SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.LOGIN_EMAIL, binding.loginEmailEditText.getText().toString());
        LoginPasswordFragment loginPasswordFragment = new LoginPasswordFragment();
        FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginPasswordFragment);
    }

}