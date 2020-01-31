package com.iguerra94.weathernow.views.signup_screens;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.utils.UserUtils;
import com.iguerra94.weathernow.utils.asyncTasks.RegisterUserAsyncTask;
import com.iguerra94.weathernow.views.exceptions.EmptyFieldsException;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setupToolbar();

        Button btnSignupNewUser = findViewById(R.id.btnSignupNewUser);
        btnSignupNewUser.setOnClickListener(this);
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

    private void signupNewUser() throws EmptyFieldsException {
        EditText mFirstNameEditText = findViewById(R.id.signup_first_name_edit_text);
        EditText mLastNameEditText = findViewById(R.id.signup_last_name_edit_text);
        EditText mEmailEditText = findViewById(R.id.signup_email_edit_text);
        EditText mPasswordEditText = findViewById(R.id.signup_password_edit_text);

        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        User user = new User(firstName, lastName, email, password);

        UserUtils.checkEmptyFieldsAtRegister(user);

        new RegisterUserAsyncTask(this, user).execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignupNewUser) {
            try {
                signupNewUser();
            } catch (EmptyFieldsException e) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = layout.findViewById(R.id.custom_toast_text_view);
                text.setText(e.getMessage());

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        }

    }

}