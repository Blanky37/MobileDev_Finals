package com.example.aquino_bembo_finals.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aquino_bembo_finals.R;

public class AdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickToPendingApplications(View view) {
        Intent intent = new Intent(AdminHome.this, PendingApplicationsView.class);
        startActivity(intent);
    }

    public void onClickToAllApplications(View view) {
        Intent intent = new Intent(AdminHome.this, AllApplicationsView.class);
        startActivity(intent);
    }

    public void onClickToAllRecords(View view) {
        Intent intent = new Intent(AdminHome.this, AllRecordsView.class);
        startActivity(intent);

    }
}