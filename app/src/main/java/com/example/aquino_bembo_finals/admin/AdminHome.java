package com.example.aquino_bembo_finals.admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.R;

public class AdminHome extends AppCompatActivity {

    DatabaseHelper myData = new DatabaseHelper(this);
    TextView tv_pending_count, tv_approved_count;

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

        tv_pending_count = (TextView) findViewById(R.id.tv_pending_count);
        tv_approved_count = (TextView) findViewById(R.id.tv_approved_count);

        // Update counts when activity starts
        updateApplicationCounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update counts every time
        updateApplicationCounts();
    }

    private void updateApplicationCounts() {
        // Get all loans from database
        Cursor resultSet = myData.ViewAllLoans();
        int pendingCount = 0;
        int approvedCount = 0;

        if (resultSet.getCount() == 0) {
            // No applications found
            tv_pending_count.setText("0");
            tv_approved_count.setText("0");
        } else {
            while (resultSet.moveToNext()) {
                String status = resultSet.getString(5); // Column 5 is LoanStatus

                if (status.equalsIgnoreCase("Pending")) {
                    pendingCount++;
                } else if (status.equalsIgnoreCase("Approved")) {
                    approvedCount++;
                }
            }

            // Update the TextViews with counts
            tv_pending_count.setText(String.valueOf(pendingCount));
            tv_approved_count.setText(String.valueOf(approvedCount));
        }

        resultSet.close();
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

    public void onClickToLogout(View view) {
        Intent intent = new Intent(AdminHome.this, AdminLogout.class);
        startActivity(intent);
    }
}