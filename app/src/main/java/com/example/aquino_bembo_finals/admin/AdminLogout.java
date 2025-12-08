package com.example.aquino_bembo_finals.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aquino_bembo_finals.initial.Login;
import com.example.aquino_bembo_finals.R;

public class AdminLogout extends AppCompatActivity {

    private Button btnCancel, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_logout);

        btnCancel = findViewById(R.id.btn_cancel);
        btnLogout = findViewById(R.id.btn_logout);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish the current activity to go back to the previous one (AdminHome)
                finish();
            }
        });

        // Set OnClickListener for the Logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmation();
            }
        });

        // Modern approach for handling back button press
        setupBackPressedHandler();
    }

    private void setupBackPressedHandler() {
        // Create a callback that will be triggered when the back button is pressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button press
                finish();
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Yes", proceed logout
                performLogout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "No", do nothing and dismiss the dialog
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void myMessageWindow(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Just close the dialog when OK is clicked
            }
        });
        builder.show();
    }

    private void performLogout() {
        myMessageWindow("Logout Success", "You have been logged out successfully.");

        // Navigate to the Login activity after a short delay
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AdminLogout.this, Login.class);
                // These flags clear the activity stack, so the user can't go back to the admin panel
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}