package com.example.aquino_bembo_finals;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aquino_bembo_finals.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private String currentEmployeeId;
    private String currentEmployeeName;
    private int currentIsAdmin;
    private boolean redirectToAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get user data from Login activity
        Intent intent = getIntent();
        if (intent != null) {
            currentEmployeeId = intent.getStringExtra("EMPLOYEE_ID");
            currentEmployeeName = intent.getStringExtra("EMPLOYEE_NAME");
            currentIsAdmin = intent.getIntExtra("IS_ADMIN", 0);
            redirectToAdmin = intent.getBooleanExtra("REDIRECT_TO_ADMIN", false);

            // You can use these values as needed
            if (currentIsAdmin == 1) {
                // Enable admin features
            }
        }

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Update the navigation header with user info
        updateNavigationHeader();

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_emergencyloan, R.id.nav_specialloan, R.id.nav_regularloan,  R.id.nav_slideshow, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Redirect to admin page if flag is set (for ADMIN001)
        if (redirectToAdmin) {
            // Navigate to slideshow fragment (admin dashboard)
            navController.navigate(R.id.nav_slideshow);
        }
    }

    private void updateNavigationHeader() {
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        TextView tvUserName = headerView.findViewById(R.id.tv_nav_header_name);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_nav_header_id);

        if (tvUserName != null && currentEmployeeName != null) {
            tvUserName.setText(currentEmployeeName);
        }
        if (tvUserEmail != null && currentEmployeeId != null) {
            // Add admin badge for admin users
            if (currentEmployeeId.equals("ADMIN001")) {
                tvUserEmail.setText("ID: " + currentEmployeeId + " (Administrator)");
            } else {
                tvUserEmail.setText("ID: " + currentEmployeeId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public String getCurrentEmployeeId() {
        return currentEmployeeId;
    }
}