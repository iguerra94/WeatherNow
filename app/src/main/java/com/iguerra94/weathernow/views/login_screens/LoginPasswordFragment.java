package com.iguerra94.weathernow.views.login_screens;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.FragmentLoginPasswordBinding;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyPasswordException;
import com.iguerra94.weathernow.db.entities.exceptions.UserWrongPasswordException;
import com.iguerra94.weathernow.utils.FragmentUtils;
import com.iguerra94.weathernow.utils.UserUtils;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfUserExistsByEmailAndPasswordTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.db.VerifyIfUserExistsByEmailAndPasswordAsyncTask;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;
import com.iguerra94.weathernow.views.dialogs.DialogFactory;
import com.iguerra94.weathernow.views.main_screens.MainScreenActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

public class LoginPasswordFragment extends Fragment implements View.OnClickListener, IVerifyIfUserExistsByEmailAndPasswordTaskResponse {

    private FragmentLoginPasswordBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_password, container, false);

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

        binding.loginPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearPasswordFieldError();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.btnLoginUser.setOnClickListener(this);

        return binding.getRoot();
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
            try {
                verifyUserOnButtonPressed();
            } catch (EmptyPasswordException e) {
                binding.loginPasswordLayout.setErrorEnabled(true);
                binding.loginPasswordLayout.setError(e.getMessage());
            }
        }
    }

    private void goToMainScreenActivity() {
        Intent i = new Intent(getActivity(), MainScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        getActivity().finish();
    }

    private void clearPasswordFieldError() {
        if (binding.loginPasswordLayout.getError() != null && !binding.loginPasswordLayout.getError().toString().isEmpty()) {
            binding.loginPasswordLayout.setError(null);
            binding.loginPasswordLayout.setErrorEnabled(false);
        }
    }

    private void verifyUserOnButtonPressed() throws EmptyPasswordException {
        String loginPassword = binding.loginPasswordEditText.getText().toString();
        clearPasswordFieldError();

        UserUtils.checkEmptyPasswordAtLogin(getActivity(), loginPassword);

        String loginEmail = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.LOGIN_EMAIL);

        User user = new User();
        user.setEmail(loginEmail);

        //Hash password with SHA-256 Algorithm
        user.setPassword(UserUtils.hashPassword(loginPassword));

        new VerifyIfUserExistsByEmailAndPasswordAsyncTask(getActivity(), user, this).execute();
    }

    @Override
    public void onVerifyIfUserExistsByEmailAndPasswordTaskDone(User userFound) {
        if (userFound == null) try {
            String currentLocale = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "Revisa tu clave." : "Check your password.";

            throw new UserWrongPasswordException(message);
        } catch (UserWrongPasswordException e) {
            binding.loginPasswordLayout.setErrorEnabled(true);
            binding.loginPasswordLayout.setError(e.getMessage());
            return;
        }

        AlertDialog dialogSigninUser = (AlertDialog) DialogFactory.getInstance().getCustomLoadingDialog().create(getActivity(), R.layout.custom_loading_dialog, R.string.dialog_signin_user_message_text);
        dialogSigninUser.setCancelable(false);
        dialogSigninUser.show();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.LOGIN_EMAIL, "");
            SharedPrefsManager.getInstance(getActivity()).saveUserDataInSharedPrefs(getActivity(), userFound);

            goToMainScreenActivity();
            dialogSigninUser.dismiss();
        }, 2000);
    }


}