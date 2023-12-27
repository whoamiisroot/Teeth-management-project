package com.ensaj.opencv;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Retrieve user information from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("userId");
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String role = intent.getStringExtra("role");
            String login = intent.getStringExtra("login");
            String password = intent.getStringExtra("password");
            String number = intent.getStringExtra("number");
            String group = intent.getStringExtra("group");

            // Log user information
            Log.d("User Information", "ID: " + userId);
            Log.d("User Information", "First Name: " + firstName);
            Log.d("User Information", "Last Name: " + lastName);
            Log.d("User Information", "Role: " + role);
            Log.d("User Information", "Login: " + login);
            Log.d("User Information", "Password: " + password);
            Log.d("User Information", "Number: " + number);
            Log.d("User Information", "Group: " + group);

            // Update the UI with user information
            TextView fullNameTextView = findViewById(R.id.full_name);
            TextView usernameTextView = findViewById(R.id.username);
            TextView roleTextView = findViewById(R.id.role);
            TextView numberTextView = findViewById(R.id.number);
            TextView groupeTextView = findViewById(R.id.groupe);
            @SuppressLint("WrongViewCast") ImageView updateImageView = findViewById(R.id.update);
            ImageView homeImageView = findViewById(R.id.home);

            fullNameTextView.setText(firstName + " " + lastName);
            usernameTextView.setText("Username: " + login);
            roleTextView.setText("Role: " + role);
            numberTextView.setText("Number: " + number);
            groupeTextView.setText("Groupe: " + group);
            homeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the MainActivity
                    Intent mainIntent = new Intent(UserProfile.this, MainActivity.class);
                    mainIntent.putExtra("login", login);
                    startActivity(mainIntent);
                    finish();
                }
            });
            updateImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent updateIntent = new Intent(UserProfile.this, UpdateEtudiantActivity.class);
                    updateIntent.putExtra("userId", userId);
                    updateIntent.putExtra("firstName", firstName);
                    updateIntent.putExtra("lastName", lastName);
                    updateIntent.putExtra("login", login);
                    updateIntent.putExtra("password", password);
                    Log.e("password dial profile",password);
                    updateIntent.putExtra("number", number);
                    updateIntent.putExtra("group", group);
                    startActivity(updateIntent);
                }
            });
        }
    }
}
