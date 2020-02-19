package com.iguerra94.weathernow.views.main_screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.BuildConfig;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.databinding.FragmentProfileScreenBinding;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.db.entities.exceptions.UserDataNotChangedException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongNameLengthException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongPasswordLengthException;
import com.iguerra94.weathernow.utils.ImageUtils;
import com.iguerra94.weathernow.utils.PopUpMenuUtils;
import com.iguerra94.weathernow.utils.ToastUtils;
import com.iguerra94.weathernow.utils.UserUtils;
import com.iguerra94.weathernow.utils.asyncTasks.db.UpdateUserDataAsyncTask;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.views.signup_screens.SignupActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProfileScreenFragment extends Fragment implements View.OnClickListener, MenuBuilder.Callback {

    private FragmentProfileScreenBinding binding;
    private Uri profileImageUri;
    private String imageFilePath;
    private boolean userSelectedImageFromCamera = false;

    private static final int IMAGE_CAPTURE_REQUEST_CODE = 100;
    private static final int IMAGE_GALLERY_REQUEST_CODE = 101;

    public ProfileScreenFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_screen, container, false);

        AppBarLayout appBarLayout = getActivity().findViewById(R.id.mainToolbarAppBarLayout);
        appBarLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));

        SimpleToolbar profileScreenToolbar = getActivity().findViewById(R.id.mainToolbar);

        profileScreenToolbar.getBackground().setAlpha(0);
        profileScreenToolbar.setNavigationIcon(R.drawable.ic_sort);

        profileScreenToolbar.setTitle(getActivity().getResources().getString(R.string.menu_profile));
        profileScreenToolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.colorBlack));

        profileImageUri = null;

        fillViewsWithUserData();

        binding.profileImageView.setOnClickListener(this);
        binding.btnSaveUserProfileData.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.profileFirstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    String currentUserGivenName = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_USER_GIVEN_NAME);
                    binding.profileFirstNameEditText.setText(currentUserGivenName);
                } else {
                    clearFirstNameFieldError();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.profileLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    String currentUserFamilyName = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_USER_FAMILY_NAME);
                    binding.profileLastNameEditText.setText(currentUserFamilyName);
                } else {
                    clearLasttNameFieldError();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.profilePasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearPasswordFieldError();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void fillViewsWithUserData() {
        String currentUserProfileImageUri = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_USER_PROFILE_IMAGE_URI);
        String currentUserEmail = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_USER_EMAIL);
        String currentUserGivenName = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_USER_GIVEN_NAME);
        String currentUserFamilyName = SharedPrefsManager.getInstance(getActivity()).readString(SharedPrefsKeys.CURRENT_USER_FAMILY_NAME);

        profileImageUri = Uri.parse(currentUserProfileImageUri);

        Glide.with(this)
                .load(currentUserProfileImageUri)
                .placeholder(R.mipmap.icon_placeholder)
                .error(R.mipmap.account_circle_white_bg)
                .into(binding.profileImageView);

        binding.profileEmailTextView.setText(currentUserEmail);
        binding.profileFirstNameEditText.setText(currentUserGivenName);
        binding.profileLastNameEditText.setText(currentUserFamilyName);
    }

    private void clearFirstNameFieldError() {
        if (binding.profileFirstNameLayout.getError() != null && !binding.profileFirstNameLayout.getError().toString().isEmpty()) {
            binding.profileFirstNameLayout.setError(null);
            binding.profileFirstNameLayout.setErrorEnabled(false);
        }
    }

    private void clearLasttNameFieldError() {
        if (binding.profileLastNameLayout.getError() != null && !binding.profileLastNameLayout.getError().toString().isEmpty()) {
            binding.profileLastNameLayout.setError(null);
            binding.profileLastNameLayout.setErrorEnabled(false);
        }
    }

    private void clearPasswordFieldError() {
        if (binding.profilePasswordLayout.getError() != null && !binding.profilePasswordLayout.getError().toString().isEmpty()) {
            binding.profilePasswordLayout.setError(null);
            binding.profilePasswordLayout.setErrorEnabled(false);
        }
    }

    private void clearFieldsError() {
        if (binding.profileFirstNameLayout.getError() != null && !binding.profileFirstNameLayout.getError().toString().isEmpty()) {
            binding.profileFirstNameLayout.setError(null);
            binding.profileFirstNameLayout.setErrorEnabled(false);
        }
        if (binding.profileLastNameLayout.getError() != null && !binding.profileLastNameLayout.getError().toString().isEmpty()) {
            binding.profileLastNameLayout.setError(null);
            binding.profileLastNameLayout.setErrorEnabled(false);
        }
        if (binding.profilePasswordLayout.getError() != null && !binding.profilePasswordLayout.getError().toString().isEmpty()) {
            binding.profilePasswordLayout.setError(null);
            binding.profilePasswordLayout.setErrorEnabled(false);
        }
    }

    private void updateUser() throws WrongNameLengthException, WrongPasswordLengthException, UserDataNotChangedException {
        String firstName = binding.profileFirstNameEditText.getText().toString();
        String lastName = binding.profileLastNameEditText.getText().toString();
        String email = binding.profileEmailTextView.getText().toString();
        String password = binding.profilePasswordEditText.getText().toString();

        clearFieldsError();

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (profileImageUri != null) {
            user.setProfileImageUri(profileImageUri.toString());

            if (userSelectedImageFromCamera) {
                ImageUtils.galleryAddPic(getContext(), imageFilePath);
            }
        }

        Log.d(ProfileScreenFragment.class.getSimpleName(), "USER: " + user);

        UserUtils.checkNamesLength(getActivity(), user);
        UserUtils.checkNewPasswordLength(getActivity(), user.getPassword());

        if (user.getPassword().isEmpty()) {
            UserUtils.checkIfUserDataNotChanged(getActivity(), user);
        }

        new UpdateUserDataAsyncTask(getActivity(), user).execute();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSaveUserProfileData) {
            try {
                updateUser();
            } catch (WrongNameLengthException e) {
                if (binding.profileFirstNameEditText.getText().length() < 3) {
                    binding.profileFirstNameLayout.setErrorEnabled(true);
                    binding.profileFirstNameLayout.setError(e.getMessage());
                }
                if (binding.profileLastNameEditText.getText().length() < 3) {
                    binding.profileLastNameLayout.setErrorEnabled(true);
                    binding.profileLastNameLayout.setError(e.getMessage());
                }
            } catch (WrongPasswordLengthException e) {
                binding.profilePasswordLayout.setErrorEnabled(true);
                binding.profilePasswordLayout.setError(e.getMessage());
            } catch (UserDataNotChangedException e) {
                Toast toast = ToastUtils.createCustomToast(getActivity(), Toast.LENGTH_LONG, e.getMessage());
                toast.show();

                return;
            }
        }
        if (view.getId() == R.id.profile_image_view) {
            List<Object> popupMenu = PopUpMenuUtils.createPopUpMenu(getActivity(), binding.profileImageView, R.menu.menu_media_options);

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
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = ImageUtils.createImageFile(getActivity());
                        imageFilePath = photoFile.getAbsolutePath();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.d(SignupActivity.class.getSimpleName(), "Error creating image file.");
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(getActivity(),
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
                if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_CAPTURE_REQUEST_CODE:
                profileImageUri = Uri.parse(imageFilePath);
                Log.d(SignupActivity.class.getSimpleName(), "profileImageUri: " + profileImageUri);

                Bitmap bitmapScaled = ImageUtils.getImageBitmapScaled(binding.profileImageView, imageFilePath);

                Glide.with(this)
                        .load(bitmapScaled)
                        .placeholder(R.mipmap.icon_placeholder)
                        .error(R.mipmap.account_circle_white_bg)
                        .into(binding.profileImageView);

                clearFieldsError();

                userSelectedImageFromCamera = true;
                break;
            case IMAGE_GALLERY_REQUEST_CODE:
                profileImageUri = data.getData();
                Log.d(SignupActivity.class.getSimpleName(), "profileImageUri: " + profileImageUri);

                Glide.with(this)
                        .load(profileImageUri)
                        .placeholder(R.mipmap.icon_placeholder)
                        .error(R.mipmap.account_circle_white_bg)
                        .into(binding.profileImageView);

                clearFieldsError();

                userSelectedImageFromCamera = false;
                break;
            default:
                break;
        }
    }

}