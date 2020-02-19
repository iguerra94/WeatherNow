package com.iguerra94.weathernow.views.signup_screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.iguerra94.weathernow.BuildConfig;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.ActivitySignupBinding;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyFieldsException;
import com.iguerra94.weathernow.db.entities.exceptions.UserExistsException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongEmailFormatException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongNameLengthException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongPasswordLengthException;
import com.iguerra94.weathernow.utils.ImageUtils;
import com.iguerra94.weathernow.utils.LocaleHelper;
import com.iguerra94.weathernow.utils.PopUpMenuUtils;
import com.iguerra94.weathernow.utils.UserUtils;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IVerifyIfUserExistsByEmailTaskResponse;
import com.iguerra94.weathernow.utils.asyncTasks.db.RegisterUserAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.db.VerifyIfUserExistsByEmailAsyncTask;
import com.iguerra94.weathernow.utils.network.NetworkUtils;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, MenuBuilder.Callback, IVerifyIfUserExistsByEmailTaskResponse {

    private ActivitySignupBinding binding;

    private User user;
    private Uri profileImageUri;
    private String imageFilePath;
    private boolean userSelectedImageFromCamera = false;

    private GoogleSignInClient googleSignInClient;

    private static final int IMAGE_CAPTURE_REQUEST_CODE = 100;
    private static final int IMAGE_GALLERY_REQUEST_CODE = 101;
    private static final int GOOGLE_SIGNIN_INTENT_REQUEST_CODE = 201;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        setupToolbar();

        binding.signupProfileImageView.setOnClickListener(this);
        binding.btnSignupNewUser.setOnClickListener(this);

        profileImageUri = null;

        boolean isConnectedToInternet = NetworkUtils.isConnectedToInternet(this);

        if (isConnectedToInternet) {
            new Handler().postDelayed(() -> {
                // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                googleSignInClient = GoogleSignIn.getClient(this, gso);

                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGNIN_INTENT_REQUEST_CODE);
            }, 800);
        }

        binding.signupFirstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearFirstNameFieldError();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.signupLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearLasttNameFieldError();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.signupEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearEmailFieldError();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.signupPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearPasswordFieldError();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isUserLogged = SharedPrefsManager.getInstance(this).readBoolean(SharedPrefsKeys.IS_USER_LOGGED);

        if (isUserLogged) {
            finish();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.signupToolbar);

        assert getSupportActionBar() != null;

        getSupportActionBar().setTitle(getResources().getString(R.string.signup_toolbar_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearFirstNameFieldError() {
        if (binding.signupFirstNameLayout.getError() != null && !binding.signupFirstNameLayout.getError().toString().isEmpty()) {
            binding.signupFirstNameLayout.setError(null);
            binding.signupFirstNameLayout.setErrorEnabled(false);
        }
    }

    private void clearLasttNameFieldError() {
        if (binding.signupLastNameLayout.getError() != null && !binding.signupLastNameLayout.getError().toString().isEmpty()) {
            binding.signupLastNameLayout.setError(null);
            binding.signupLastNameLayout.setErrorEnabled(false);
        }
    }

    private void clearEmailFieldError() {
        if (binding.signupEmailLayout.getError() != null && !binding.signupEmailLayout.getError().toString().isEmpty()) {
            binding.signupEmailLayout.setError(null);
            binding.signupEmailLayout.setErrorEnabled(false);
        }
    }

    private void clearPasswordFieldError() {
        if (binding.signupPasswordLayout.getError() != null && !binding.signupPasswordLayout.getError().toString().isEmpty()) {
            binding.signupPasswordLayout.setError(null);
            binding.signupPasswordLayout.setErrorEnabled(false);
        }
    }

    private void clearFieldsError() {
        if (binding.signupFirstNameLayout.getError() != null && !binding.signupFirstNameLayout.getError().toString().isEmpty()) {
            binding.signupFirstNameLayout.setError(null);
            binding.signupFirstNameLayout.setErrorEnabled(false);
        }
        if (binding.signupLastNameLayout.getError() != null && !binding.signupLastNameLayout.getError().toString().isEmpty()) {
            binding.signupLastNameLayout.setError(null);
            binding.signupLastNameLayout.setErrorEnabled(false);
        }
        if (binding.signupEmailLayout.getError() != null && !binding.signupEmailLayout.getError().toString().isEmpty()) {
            binding.signupEmailLayout.setError(null);
            binding.signupEmailLayout.setErrorEnabled(false);
        }
        if (binding.signupPasswordLayout.getError() != null && !binding.signupPasswordLayout.getError().toString().isEmpty()) {
            binding.signupPasswordLayout.setError(null);
            binding.signupPasswordLayout.setErrorEnabled(false);
        }
    }

    private void signupNewUser() throws EmptyFieldsException, WrongNameLengthException, WrongEmailFormatException, WrongPasswordLengthException {
        String firstName = binding.signupFirstNameEditText.getText().toString();
        String lastName = binding.signupLastNameEditText.getText().toString();
        String email = binding.signupEmailEditText.getText().toString();
        String password = binding.signupPasswordEditText.getText().toString();

        clearFieldsError();

        user = new User(firstName, lastName, email, password);

        UserUtils.checkEmptyFieldsAtRegister(this, user, binding);
        UserUtils.checkNamesLength(this, user);
        UserUtils.checkEmailFormat(this, user.getEmail());
        UserUtils.checkPasswordLength(this, user.getPassword());

        new VerifyIfUserExistsByEmailAsyncTask(this, user.getEmail(), this).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignupNewUser) {
            try {
                signupNewUser();
            } catch (EmptyFieldsException e) {
                if (binding.signupFirstNameEditText.getText().toString().isEmpty()) {
                    binding.signupFirstNameLayout.setErrorEnabled(true);
                    binding.signupFirstNameLayout.setError(e.getMessage());
                }
                if (binding.signupLastNameEditText.getText().toString().isEmpty()) {
                    binding.signupLastNameLayout.setErrorEnabled(true);
                    binding.signupLastNameLayout.setError(e.getMessage());
                }
                if (binding.signupEmailEditText.getText().toString().isEmpty()) {
                    binding.signupEmailLayout.setErrorEnabled(true);
                    binding.signupEmailLayout.setError(e.getMessage());
                }
                if (binding.signupPasswordEditText.getText().toString().isEmpty()) {
                    binding.signupPasswordLayout.setErrorEnabled(true);
                    binding.signupPasswordLayout.setError(e.getMessage());
                }
            } catch (WrongNameLengthException e) {
                if (binding.signupFirstNameEditText.getText().length() < 3) {
                    binding.signupFirstNameLayout.setErrorEnabled(true);
                    binding.signupFirstNameLayout.setError(e.getMessage());
                }
                if (binding.signupLastNameEditText.getText().length() < 3) {
                    binding.signupLastNameLayout.setErrorEnabled(true);
                    binding.signupLastNameLayout.setError(e.getMessage());
                }
            } catch (WrongEmailFormatException e) {
                binding.signupEmailLayout.setErrorEnabled(true);
                binding.signupEmailLayout.setError(e.getMessage());
            } catch (WrongPasswordLengthException e) {
                binding.signupPasswordLayout.setErrorEnabled(true);
                binding.signupPasswordLayout.setError(e.getMessage());
            }
        }

        if (v.getId() == R.id.signup_profile_image_view) {
            List<Object> popupMenu = PopUpMenuUtils.createPopUpMenu(this, binding.signupProfileImageView, R.menu.menu_media_options);

            // Set Item Click Listener
            MenuBuilder menuBuilder = (MenuBuilder) popupMenu.get(0);
            MenuPopupHelper optionsMenu = (MenuPopupHelper) popupMenu.get(1);

            menuBuilder.setCallback(this);

            // Display the menu
            optionsMenu.show();
        }
    }

    @Override
    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_take_photo:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = ImageUtils.createImageFile(this);
                        imageFilePath = photoFile.getAbsolutePath();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.d(SignupActivity.class.getSimpleName(), "Error creating image file.");
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(this,
                                String.format("%s.provider", BuildConfig.APPLICATION_ID),
                                photoFile);

                        Log.d(SignupActivity.class.getSimpleName(), "photoUri: " + photoUri);

                        // set flag to give temporary permission to external app to use your FileProvider
                        takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
                    }
                }
                break;
            case R.id.nav_image_gallery:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                if (galleryIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(galleryIntent, "Seleccione una aplicacion"), IMAGE_GALLERY_REQUEST_CODE);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onMenuModeChange(MenuBuilder menu) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_CAPTURE_REQUEST_CODE:
                profileImageUri = Uri.parse(imageFilePath);
                Log.d(SignupActivity.class.getSimpleName(), "profileImageUri: " + profileImageUri);

                Bitmap bitmapScaled = ImageUtils.getImageBitmapScaled(binding.signupProfileImageView, imageFilePath);

                Glide.with(this)
                        .load(bitmapScaled)
                        .placeholder(R.mipmap.icon_placeholder)
                        .into(binding.signupProfileImageView);

                userSelectedImageFromCamera = true;
                break;
            case IMAGE_GALLERY_REQUEST_CODE:
                profileImageUri = data.getData();
                Log.d(SignupActivity.class.getSimpleName(), "profileImageUri: " + profileImageUri);

                Glide.with(this)
                        .load(profileImageUri)
                        .placeholder(R.mipmap.icon_placeholder)
                        .into(binding.signupProfileImageView);

                userSelectedImageFromCamera = false;
                break;
            case GOOGLE_SIGNIN_INTENT_REQUEST_CODE:
                try {
                    // The Task returned from this call is always completed, no need to attach a listener.
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    updateUIWithUserData(account);
                    signOut();
                } catch (ApiException e) {
                    // The ApiException status code indicates the detailed failure reason.
                    Log.w(SignupActivity.class.getSimpleName(), "signInResult: failed code: " + e.getStatusCode());
                }
                break;
            default:
                break;
        }
    }

    private void updateUIWithUserData(GoogleSignInAccount account) {
        String givenName = account.getGivenName();
        String familyName = account.getFamilyName();
        String email = account.getEmail();
        profileImageUri = account.getPhotoUrl();
        Log.d(SignupActivity.class.getSimpleName(), "profileImageUri: " + profileImageUri);

        binding.signupFirstNameEditText.setText(givenName);
        binding.signupLastNameEditText.setText(familyName);
        binding.signupEmailEditText.setText(email);
        Glide.with(binding.getRoot())
                .load(profileImageUri)
                .placeholder(R.mipmap.icon_placeholder)
                .into(binding.signupProfileImageView);
    }

    private void signOut() {
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            googleSignInClient.signOut().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(SignupActivity.class.getSimpleName(), "Logged out!");
                }
            });
        } else {
            Log.d(SignupActivity.class.getSimpleName(), "Not logged in");
        }
    }

    @Override
    public void onVerifyIfUserExistsByEmailTaskDone(boolean userExists) {
        if (userExists) try {
            String currentLocale = SharedPrefsManager.getInstance(this).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "El email ingresado ya esta registrado." : "The email entered is already registered.";

            throw new UserExistsException(message);
        } catch (UserExistsException e) {
            binding.signupEmailLayout.setErrorEnabled(true);
            binding.signupEmailLayout.setError(e.getMessage());

            return;
        }

        //Hash password with SHA-256 Algorithm
        user.setPassword(UserUtils.hashPassword(user.getPassword()));

        if (profileImageUri != null) {
            user.setProfileImageUri(profileImageUri.toString());

            if (userSelectedImageFromCamera) {
                ImageUtils.galleryAddPic(this, imageFilePath);
            }
        }

        new RegisterUserAsyncTask(this, user).execute();
    }
}