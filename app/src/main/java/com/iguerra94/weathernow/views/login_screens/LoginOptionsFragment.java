package com.iguerra94.weathernow.views.login_screens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.FragmentLoginOptionsBinding;
import com.iguerra94.weathernow.db.entities.exceptions.UserEmailNotRegisteredException;
import com.iguerra94.weathernow.utils.FragmentUtils;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfUserExistsByEmailTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.db.VerifyIfUserExistsByEmailAsyncTask;
import com.iguerra94.weathernow.utils.network.NetworkStateChangeReceiver;
import com.iguerra94.weathernow.utils.network.NetworkUtils;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;
import com.iguerra94.weathernow.views.signup_screens.SignupActivity;
import com.iguerra94.weathernow.views.splash_screen.SplashActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

import static com.iguerra94.weathernow.utils.network.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class LoginOptionsFragment extends Fragment implements View.OnClickListener, IVerifyIfUserExistsByEmailTaskResponse {

    private FragmentLoginOptionsBinding binding;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;
    private static final int GOOGLE_SIGNIN_INTENT_REQUEST_CODE = 201;

    private BroadcastReceiver receiver;

    public LoginOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_options, container, false);

        AppBarLayout appBarLayoutLight = getActivity().findViewById(R.id.loginToolbarLightAppBarLayout);

        AppBarLayout appBarLayoutDark = getActivity().findViewById(R.id.loginToolbarDarkAppBarLayout);
        appBarLayoutDark.setVisibility(View.GONE);

        appBarLayoutLight.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        appBarLayoutLight.setVisibility(View.VISIBLE);

        SimpleToolbar loginOptionsToolbarLight = getActivity().findViewById(R.id.loginToolbarLight);

        loginOptionsToolbarLight.setTitle(getActivity().getResources().getString(R.string.login_options_toolbar_title));
        loginOptionsToolbarLight.setTitleTextColor(getActivity().getResources().getColor(R.color.colorWhite));

        ((AppCompatActivity) getActivity()).setSupportActionBar(loginOptionsToolbarLight);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        binding.btnLoginEmailOption.setOnClickListener(this);
        binding.btnLoginGoogleOption.setOnClickListener(this);

        boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(getActivity());
        NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isConnectedToInternet);

        if (!isConnectedToInternet) {
            showNetworkNotConnectedMessage();
            disableLoginGoogleButton();
        }

        receiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInternetConnectionInfo(intent);
            }
        };

        registerNetworkReceiver(receiver);

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterNetworkReceiver(receiver);
    }

    private void registerNetworkReceiver(BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, intentFilter);
    }

    private void unregisterNetworkReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void disableLoginGoogleButton() {
        binding.btnLoginGoogleOption.setEnabled(false);
        binding.btnLoginGoogleOption.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreyDisabled)));
    }

    private void showNetworkNotConnectedMessage() {
        binding.loginScreenNetworkStatusView.setVisibility(View.VISIBLE);
        binding.loginScreenNetworkStatusViewTextView.setBackgroundColor(getResources().getColor(R.color.colorRedMD700));
        binding.loginScreenNetworkStatusViewTextView.setText(getResources().getString(R.string.app_network_status_without_connection_textview));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateInternetConnectionInfo(Intent intent) {
        boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);

        if (getActivity() != null) {
            if (!isNetworkAvailable) {
                showNetworkNotConnectedMessage();
                disableLoginGoogleButton();
            } else {
                String alreadyConnected =  SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.IS_CONNECTED_TO_INTERNET);

                if (!alreadyConnected.equals(SharedPrefsValues.IS_CONNECTED_TO_INTERNET.CONNECTED)) {
                    showNetworkRecoveredMessage();
                    enableLoginGoogleButton();

                    new Handler().postDelayed(this::hideNetworkRecoveredMessage, 1700);
                }
            }

            NetworkUtils.updateConnectionInfoSharedPrefs(getActivity(), isNetworkAvailable);
        }
    }

    private void hideNetworkRecoveredMessage() {
        binding.loginScreenNetworkStatusView.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enableLoginGoogleButton() {
        binding.btnLoginGoogleOption.setEnabled(true);
        binding.btnLoginGoogleOption.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
    }

    private void showNetworkRecoveredMessage() {
        binding.loginScreenNetworkStatusView.setVisibility(View.VISIBLE);

        TextView loginScreenNetworkStatusViewTextView = getActivity().findViewById(R.id.login_screen_network_status_view_text_view);
        loginScreenNetworkStatusViewTextView.setBackgroundColor(getResources().getColor(R.color.colorGreenMD700));
        loginScreenNetworkStatusViewTextView.setText(getResources().getString(R.string.app_network_status_recovered_connection_textview));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginEmailOption:
                LoginEmailFragment loginEmailFragment = new LoginEmailFragment();
                FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginEmailFragment);

                break;
            case R.id.btnLoginGoogleOption:
                new Handler().postDelayed(() -> {
                    // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();

                    // Build a GoogleSignInClient with the options specified by gso.
                    googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, GOOGLE_SIGNIN_INTENT_REQUEST_CODE);
                }, 800);

                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGNIN_INTENT_REQUEST_CODE) {
            try {
                // The Task returned from this call is always completed, no need to attach a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                account = task.getResult(ApiException.class);

                verifyIfUserEmailIsRegistered(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                Log.w(LoginOptionsFragment.class.getSimpleName(), "signInResult: failed code: " + e.getStatusCode());
            }
        }
    }

    private void verifyIfUserEmailIsRegistered(GoogleSignInAccount account) {
        new VerifyIfUserExistsByEmailAsyncTask(getActivity(), account.getEmail(), this).execute();
    }

    @Override
    public void onVerifyIfUserExistsByEmailTaskDone(boolean userExists) {
        if (!userExists) try {
            String currentLocale = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "El email no esta registrado." : "The email is not registered.";

            throw new UserEmailNotRegisteredException(message);
        } catch (UserEmailNotRegisteredException e) {
            Toast toast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, e.getMessage());
            toast.show();

            signOut();
            return;
        }

        SharedPrefsManager.getInstance(getActivity()).saveString(SharedPrefsKeys.LOGIN_EMAIL, account.getEmail());
        LoginPasswordFragment loginPasswordFragment = new LoginPasswordFragment();
        FragmentUtils.setFragment(getActivity(), R.id.loginContainer, loginPasswordFragment);

        signOut();
    }

    private void signOut() {
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (alreadyloggedAccount != null) {
            googleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    Log.d(SignupActivity.class.getSimpleName(), "Logged out!");
                }
            });
        } else {
            Log.d(SignupActivity.class.getSimpleName(), "Not logged in");
        }
    }
}