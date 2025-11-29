package com.example.aquino_bembo_finals;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    DatabaseHelper myData = new DatabaseHelper(this);
    TextInputEditText txtEmployeeID, txtPassword;
    TextInputLayout tilEmployeeID, tilPassword;
    CheckBox cbRememberMe;
    TextView tvRegisterLink, tvForgotPassword;

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

        // Initialize UI components
        txtEmployeeID = (TextInputEditText) findViewById(R.id.et_employee_id);
        txtPassword = (TextInputEditText) findViewById(R.id.et_password);
        tilEmployeeID = (TextInputLayout) findViewById(R.id.til_employee_id);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);
        cbRememberMe = (CheckBox) findViewById(R.id.cb_remember_me);
        tvRegisterLink = (TextView) findViewById(R.id.tv_register_link);
        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);

        // Set up click listeners
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Regisration.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMessageWindow("Forgot Password", "Please contact system administrator to reset your password.");
            }
        });
    }

    //Message Window Method (exactly like professor's)
    public void myMessageWindow(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    // Login Method (following professor's onClick pattern)
    public void onClickLogin(View view) {
        // Clear previous errors
        tilEmployeeID.setError(null);
        tilPassword.setError(null);

        String employeeID = txtEmployeeID.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        // Validate inputs
        if(employeeID.isEmpty())
        {
            tilEmployeeID.setError("Employee ID is required");
            txtEmployeeID.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            tilPassword.setError("Password is required");
            txtPassword.requestFocus();
            return;
        }

        // Check if user exists and credentials are correct
        if(myData.UserLogin(employeeID, password))
        {
            String welcomeMessage;

            // Check if user is ADMIN001
            if (employeeID.equals("ADMIN001")) {
                welcomeMessage = "Welcome System Administrator!";
                myMessageWindow("Admin Login Success", welcomeMessage);

                // Redirect to admin page (using slideshow fragment as admin page)
                Intent intent = new Intent(Login.this, MainActivity.class);

                // Pass admin data to MainActivity
                intent.putExtra("EMPLOYEE_ID", myData.getEmployeeID());
                intent.putExtra("EMPLOYEE_NAME", myData.getFullName());
                intent.putExtra("IS_ADMIN", myData.getIsAdmin());
                intent.putExtra("FIRST_NAME", myData.getFirstName());
                intent.putExtra("LAST_NAME", myData.getLastName());
                intent.putExtra("REDIRECT_TO_ADMIN", true); // Flag to redirect to admin page

                startActivity(intent);
                finish(); // Close login activity so user can't go back
            }
            else
            {
                welcomeMessage = "Welcome " + myData.getFullName() + "!";
                myMessageWindow("Login Success", welcomeMessage);

                // Navigate to MainActivity (navigation drawer)
                Intent intent = new Intent(Login.this, MainActivity.class);

                // Pass user data to MainActivity
                intent.putExtra("EMPLOYEE_ID", myData.getEmployeeID());
                intent.putExtra("EMPLOYEE_NAME", myData.getFullName());
                intent.putExtra("IS_ADMIN", myData.getIsAdmin());
                intent.putExtra("FIRST_NAME", myData.getFirstName());
                intent.putExtra("LAST_NAME", myData.getLastName());

                startActivity(intent);
                finish(); // Close login activity so user can't go back
            }
        }
        else
        {
            myMessageWindow("Login Failed", "Invalid Employee ID or Password");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myData != null) {
            myData.close();
        }
    }

    public void onClickRegisterLink(View view) {
        Intent intent = new Intent(Login.this, Regisration.class);
        startActivity(intent);
    }
}