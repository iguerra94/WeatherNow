package com.iguerra94.weathernow.views.signup_screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.Toolbar;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyFieldsException;
import com.iguerra94.weathernow.db.entities.exceptions.UserExistsException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongEmailFormatException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongPasswordLengthException;
import com.iguerra94.weathernow.utils.ImageUtils;
import com.iguerra94.weathernow.utils.PopUpMenuUtils;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.UserUtils;
import com.iguerra94.weathernow.utils.asyncTasks.RegisterUserAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.VerifyIfUserExistsAsyncTask;
import com.iguerra94.weathernow.utils.asyncTasks.callbacks.IAsyncTaskResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, MenuBuilder.Callback, IAsyncTaskResponse {

    private User user;
    private Uri profileImageUri;
    private CircleImageView signupProfileImageView;
    private boolean userSelectedImageFromCamera = false;

    private static final int IMAGE_CAPTURE_REQUEST_CODE = 100;
    private static final int IMAGE_GALLERY_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setupToolbar();

        signupProfileImageView = findViewById(R.id.signup_profile_image_view);
        signupProfileImageView.setOnClickListener(this);

        Button btnSignupNewUser = findViewById(R.id.btnSignupNewUser);
        btnSignupNewUser.setOnClickListener(this);

        profileImageUri = null;
    }

    private void setupToolbar() {
        Toolbar signupToolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(signupToolbar);

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

    private void signupNewUser() throws EmptyFieldsException, WrongEmailFormatException, WrongPasswordLengthException {
        EditText mFirstNameEditText = findViewById(R.id.signup_first_name_edit_text);
        EditText mLastNameEditText = findViewById(R.id.signup_last_name_edit_text);
        EditText mEmailEditText = findViewById(R.id.signup_email_edit_text);
        EditText mPasswordEditText = findViewById(R.id.signup_password_edit_text);

        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        user = new User(firstName, lastName, email, password);

        UserUtils.checkEmptyFieldsAtRegister(user);

        UserUtils.checkEmailFormat(user.getEmail());

        UserUtils.checkPasswordLength(user.getPassword());

        new VerifyIfUserExistsAsyncTask(this, user, this).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignupNewUser) {
            try {
                signupNewUser();
            } catch (EmptyFieldsException e) {
                Toast signupErrorToast = ToastUtils.createCustomToast(this, e.getMessage());
                signupErrorToast.show();
            } catch (WrongEmailFormatException e) {
                Toast signupErrorToast = ToastUtils.createCustomToast(this, e.getMessage());
                signupErrorToast.show();
            } catch (WrongPasswordLengthException e) {
                Toast signupErrorToast = ToastUtils.createCustomToast(this, e.getMessage());
                signupErrorToast.show();
            }
        }

        if (v.getId() == R.id.signup_profile_image_view) {
            List<Object> popupMenu = PopUpMenuUtils.createPopUpMenu(this, signupProfileImageView, R.menu.menu_media_options);

            // Set Item Click Listener
            MenuBuilder menuBuilder = (MenuBuilder) popupMenu.get(0);
            MenuPopupHelper optionsMenu = (MenuPopupHelper) popupMenu.get(1);

            menuBuilder.setCallback(this);

            // Display the menu
            optionsMenu.show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onTaskDone(boolean userExists) {
        if (userExists) try {
            throw new UserExistsException("El email ingresado ya esta registrado.");
        } catch (UserExistsException e) {
            Toast signupErrorToast = ToastUtils.createCustomToast(this, e.getMessage());
            signupErrorToast.show();

            return;
        }

        //Hash password with SHA-256 Algorithm
        user.setPassword(UserUtils.hashPassword(user.getPassword()));

        if (profileImageUri != null) {
            user.setProfileImageUri(profileImageUri.toString());

            if (userSelectedImageFromCamera) {
                ImageUtils.galleryAddPic(this, profileImageUri);
            }
        }

        new RegisterUserAsyncTask(this, user).execute();
    }

    @Override
    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_take_photo:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
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
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Uri profileImageUri = ImageUtils.getImageUriFromBitmap(this, imageBitmap);
            signupProfileImageView.setImageBitmap(imageBitmap);

            userSelectedImageFromCamera = true;
            Toast.makeText(this, "Uri: " + profileImageUri, Toast.LENGTH_SHORT).show();
        }
        if (requestCode == IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            profileImageUri = data.getData();
            signupProfileImageView.setImageURI(profileImageUri);

            userSelectedImageFromCamera = false;

            Toast.makeText(this, "Uri: " + profileImageUri, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}