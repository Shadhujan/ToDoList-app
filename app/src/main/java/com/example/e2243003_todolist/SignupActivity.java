package com.example.e2243003_todolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references to your EditText fields
        EditText nameField = findViewById(R.id.user_name);
//        EditText indexNoField = findViewById(R.id.index_number);
        EditText emailField = findViewById(R.id.signup_email);
//        EditText mobileField = findViewById(R.id.mobile_number);
//        EditText gpaField = findViewById(R.id.gpa);
        EditText passwordField = findViewById(R.id.signup_password);
        EditText confirmPasswordField = findViewById(R.id.signup_confirm);

        // Get reference to your Button
        Button signupButton = findViewById(R.id.signup_button);

        TextView loginRedirectBtn = findViewById(R.id.loginRedirectText);

        loginRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the fields
                String name = nameField.getText().toString();
//                String indexNo = indexNoField.getText().toString();
                String email = emailField.getText().toString();
//                String mobile = mobileField.getText().toString();
//                double gpa = Double.parseDouble(gpaField.getText().toString());
                String password = passwordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();

                // Validate the fields
                if (TextUtils.isEmpty(name) ||  TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if password fields match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If all validations pass, save to database
                DBHelper db = new DBHelper(SignupActivity.this);
                boolean insertSuccess = db.insertData(name, email, password);
                if (!insertSuccess) {
                    Toast.makeText(getApplicationContext(), "Error saving data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                    // If the data is saved successfully, navigate to the login view
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });

        }
    }

