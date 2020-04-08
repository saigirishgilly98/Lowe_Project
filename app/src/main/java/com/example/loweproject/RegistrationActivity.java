package com.example.loweproject;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationActivity extends AppCompatActivity {

    /**
     * Declaration of views
     */
    EditText name;
    EditText email;
    EditText phone;
    EditText password;
    EditText confirmpassword;
    TextInputLayout name_layout, email_layout, phone_layout, password_layout, confirm_layout;
    Button register;
    SharedPreferences sp;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        /**
         * Declaration and linking of views
         */
        name = findViewById(R.id.name_text_view);
        email = findViewById(R.id.email_text_view);
        phone = findViewById(R.id.phone_text_view);
        password = findViewById(R.id.password_text_view);
        confirmpassword = findViewById(R.id.confirm_password_text_view);
        name_layout = findViewById(R.id.layout_name);
        email_layout = findViewById(R.id.layout_email);
        phone_layout = findViewById(R.id.layout_phone);
        password_layout = findViewById(R.id.layout_password);
        confirm_layout = findViewById(R.id.layout_confirm);
        register = findViewById(R.id.register_button);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        if (sp.getBoolean("logged", false)) {
            Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(i);
        }

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();



        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isEmpty(name)) {
                        name_layout.setError("Name is required!");
                    } else
                        name_layout.setError(null);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isEmail(email) == false) {
                        email_layout.setError("Enter valid email!");
                    } else
                        email_layout.setError(null);
                }
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isValidMobile(phone) == false) {
                        phone_layout.setError("Phone Number must have 10 digits!");
                    } else
                        phone_layout.setError(null);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isValidPassword(password) == false) {
                        password_layout.setError("Password must be at least 8 characters!");
                    } else
                        password_layout.setError(null);
                }
            }
        });

        confirmpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!password.getText().toString().equals(confirmpassword.getText().toString())) {
                        confirm_layout.setError("Password did not match!");
                    } else
                        confirm_layout.setError(null);
                }
            }
        });

        /**
         * When register button is clicked ,
         * checkDataEntered() is evaluated
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(email.getText().toString(), password.getText().toString());
            }
        });

    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

    }
    // [END on_start_check_user]


    private void createAccount(final String email, final String password) {
        // Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegistrationActivity.this, "Registration Successful",
                                    Toast.LENGTH_SHORT).show();
                            //Store in Firebase Database
                            try {
                                database.getReference("User").child(mAuth.getCurrentUser().getUid()).child("name").setValue(name.getText().toString());
                                database.getReference("User").child(mAuth.getCurrentUser().getUid()).child("email").setValue(email);
                                database.getReference("User").child(mAuth.getCurrentUser().getUid()).child("phone").setValue(phone.getText().toString());
                                database.getReference("User").child(mAuth.getCurrentUser().getUid()).child("password").setValue(password);
                                database.getReference("User").child(mAuth.getCurrentUser().getUid()).child("wallet").setValue("1000");
                                database.getReference("User").child(mAuth.getCurrentUser().getUid()).child("uid").setValue(mAuth.getCurrentUser().getUid());
                                database.getReference().child("User").child(mAuth.getCurrentUser().getUid()).child("url").setValue("https://www.flaticon.com/free-icon/man_236831.jpg");
                            }catch (Exception e){}
                            // [START send_email_verification]
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.d(TAG, "Email sent.");
                                                Toast.makeText(RegistrationActivity.this, "Email Sent.",
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                            // [END send_email_verification]
                            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Account Exists Already.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // [END create_user_with_email]
    }

    /**
     * To check the validity of all the views
     */
    private boolean validateForm() {
        boolean valid = true;
        /**
         * Error display for invalid name
         */
        if (isEmpty(name)) {
            name_layout.setError("Name is required!");
            valid = false;
        } else
            name_layout.setError(null);

        /**
         * Error display for invalid email
         */
        if (isEmail(email) == false) {
            email_layout.setError("Enter valid email!");
            valid = false;
        } else
            email_layout.setError(null);

        /**
         * Error display for invalid phone
         */
        if (isValidMobile(phone) == false) {
            phone_layout.setError("Phone Number must have 10 digits!");
            valid = false;
        } else
            phone_layout.setError(null);

        /**
         * Error display for invalid password
         */
        if (isValidPassword(password) == false) {
            password_layout.setError("Password must be at least 8 characters!");
            valid = false;
        } else
            password_layout.setError(null);

        /**
         * Password entered in confirm password view is checked for a match ,
         * with already entered password
         */
        if (!password.getText().toString().equals(confirmpassword.getText().toString())) {
            confirm_layout.setError("Password did not match!");
            valid = false;
        } else
            confirm_layout.setError(null);

        return valid;
    }

    /**
     * To check the validation of Email address
     *
     * @param text
     * @return '1' for accepted email and '0' for not accepted email
     */
    private boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * To Check if EditText View is empty
     *
     * @param text
     * @return '1' for empty and '0' for not empty
     */
    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    /**
     * To check the validation of Phone Number
     *
     * @param text
     * @return '1' if valid '0' if not valid
     */
    private boolean isValidMobile(EditText text) {
        CharSequence phone = text.getText().toString();
        return phone.length() == 10;

    }

    /**
     * To  check the validation of Password
     *
     * @param text
     * @return '1' if valid '0' if not valid
     */
    public static boolean isValidPassword(EditText text) {
        CharSequence password = text.getText().toString();
        return password.length() >= 8;
    }

    /**
     * openTerms() is evaluated when user clicks <u>terms</u>,
     * Terms and conditions are displayed
     *
     * @param view
     */
    public void openTerms(View view) {
        Intent i = new Intent(this, TermsActivity.class);
        startActivity(i);
    }


    /**
     * To check the status of radio button
     *
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_create_account:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_login:
                if (checked) {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    break;
                }
        }
    }


    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }


}
