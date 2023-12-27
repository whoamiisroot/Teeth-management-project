package com.ensaj.opencv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateEtudiantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_etudiant);

        // Retrieve user information from the intent
        String userId = getIntent().getStringExtra("userId");
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");
        String number = getIntent().getStringExtra("number");
        String group = getIntent().getStringExtra("group");
        Log.e("paswoooooooooord", password);

        // Populate the form fields with user information
        EditText firstNameEditText = findViewById(R.id.textFirstName);
        EditText lastNameEditText = findViewById(R.id.textLastName);
        EditText loginEditText = findViewById(R.id.textLogin);
        EditText passwordEditText = findViewById(R.id.textPassword);
        EditText numberEditText = findViewById(R.id.textNumber);

        // Set the retrieved values
        firstNameEditText.setText(firstName);
        lastNameEditText.setText(lastName);
        loginEditText.setText(login);
        passwordEditText.setText(password);
        numberEditText.setText(number);

        // Handle other fields as needed

        // Button to update the profile
        Button updateButton = findViewById(R.id.update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve updated data from EditText fields
                String updatedFirstName = firstNameEditText.getText().toString();
                String updatedLastName = lastNameEditText.getText().toString();
                String updatedLogin = loginEditText.getText().toString();
                String updatedPassword = passwordEditText.getText().toString();
                String updatedNumber = numberEditText.getText().toString();

                // Update the profile using Volley
                updateProfile(userId, updatedFirstName, updatedLastName, updatedLogin, updatedPassword, updatedNumber);
            }
        });
    }

    private void updateProfile(String userId, String updatedFirstName, String updatedLastName,
                               String updatedLogin, String updatedPassword, String updatedNumber) {
        String url = "http://10.0.2.2:8080/api/v1/students/" + userId;

        // Create the request parameters
        Map<String, Object> params = new HashMap<>();
        params.put("firstName", updatedFirstName);
        params.put("lastName", updatedLastName);
        params.put("login", updatedLogin);
        params.put("password", updatedPassword);
        params.put("number", updatedNumber);
        params.put("role", "student");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                new JSONObject(params),  // Convert params to a JSONObject
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showToast("Profile updated successfully!");

                        // Redirect to UserProfile activity
                        Intent userProfileIntent = new Intent(UpdateEtudiantActivity.this, UserProfile.class);
                        userProfileIntent.putExtra("userId", userId);
                        userProfileIntent.putExtra("firstName", updatedFirstName);
                        userProfileIntent.putExtra("lastName", updatedLastName);
                        userProfileIntent.putExtra("role", "student");
                        userProfileIntent.putExtra("login", updatedLogin);
                        userProfileIntent.putExtra("password", updatedPassword);
                        userProfileIntent.putExtra("number", updatedNumber);
                        userProfileIntent.putExtra("group", ""); // Add group information if needed

                        startActivity(userProfileIntent);
                        finish(); // Optional: Close UpdateEtudiantActivity
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Error updating profile. Please try again.");
                        Log.e("UpdateProfileError", "Error: " + error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Set the content type to JSON
                return headers;
            }
        };

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
