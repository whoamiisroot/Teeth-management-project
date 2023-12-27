package com.ensaj.opencv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private TextView signupRedirectText;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);



        Button loginButton = findViewById(R.id.login_button);
// Inside the onClick method of your loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredEmail = emailEditText.getText().toString().trim();
                String enteredPassword = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(enteredEmail) || TextUtils.isEmpty(enteredPassword)) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Make a POST request to your Spring Boot backend for authentication using Volley
                    authenticateUser(enteredEmail, enteredPassword);
                }
            }
        });
    }
    private void authenticateUser(String email, String password) {
        String url = "http://10.0.2.2:8080/api/v1/students/login";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("login", email);
            requestBody.put("password", password);
            Log.e("teeeeeest" , email+password + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log the entire response for inspection
                        Log.e("Response", response.toString());

                        // Handle the authentication response
                        boolean success = response.optBoolean("success", false);
                        String message = response.optString("message", "");

                        if (success) {
                            // Authentication successful, extract user information
                            JSONObject userObject = response.optJSONObject("user");

                            if (userObject != null) {
                                // Extract user information
                                String userId = userObject.optString("id", "");
                                String firstName = userObject.optString("firstName", "");
                                String lastName = userObject.optString("lastName", "");
                                String login = userObject.optString("login", "");
                                String password = userObject.optString("password", "");
                                String role = userObject.optString("role", "");
                                String number = userObject.optString("number", "");
                                String group = userObject.optString("group", "");

                                // Pass user information to MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("firstName", firstName);
                                intent.putExtra("lastName", lastName);
                                intent.putExtra("role", role);
                                intent.putExtra("login", login);
                                intent.putExtra("password", password);
                                intent.putExtra("number", number);
                                intent.putExtra("group", group);

                                Log.e("User Information", "ID: " + userId);
                                Log.e("User Information", "First Name: " + firstName);
                                Log.e("User Information", "Last Name: " + lastName);
                                Log.e("User Information", "Role: " + role);
                                Log.e("User Information", "Login: " + login);
                                Log.e("User Information", "Password: " + password);
                                Log.e("User Information", "Number: " + number);
                                Log.e("User Information", "Group: " + group);

                                startActivity(intent);
                                finish(); // Optional: Close LoginActivity
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error during authentication", error);
                        Toast.makeText(LoginActivity.this, "Error during authentication", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(request);
    }


}
