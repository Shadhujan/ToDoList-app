// LoginActivity.java
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

public class LoginActivity extends AppCompatActivity {
    String user_name; // Declare the variable here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references to your EditText fields
        EditText userField = findViewById(R.id.login_User);
        EditText passwordField = findViewById(R.id.login_password);

        TextView signupRedirectBtn = findViewById(R.id.signupRedirectText);

        signupRedirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Get reference to your Button
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the fields
                String user = userField.getText().toString();
                String password = passwordField.getText().toString();

                // Validate the fields
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the entered credentials are valid
                DBHelper db = new DBHelper(LoginActivity.this);
                int userid = db.getuserid(user);
                boolean isValid = db.checkEmailPassword(user, password);

                if (!isValid) {
                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                } else {
                    // If the credentials are valid, create a new User instance and set it as the current user
                    String email = db.getemail(userid);
                    User.createNewUser(user, email, password);
                    // If the credentials are valid, navigate to the MainActivity

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", user);
                    intent.putExtra("userid", String.valueOf(userid));
                    startActivity(intent);
                }
            }
        });
    }
}
