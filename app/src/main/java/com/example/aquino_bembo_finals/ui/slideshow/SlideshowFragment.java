package com.example.aquino_bembo_finals.ui.slideshow;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.R;

public class SlideshowFragment extends Fragment {

    private DatabaseHelper myData;
    private TextView tvPendingCount, tvApprovedCount;
    private View cardViewPending, cardViewAll, cardViewRecords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Initialize database helper
        myData = new DatabaseHelper(getActivity());

        // Initialize views
        tvPendingCount = (TextView) root.findViewById(R.id.tv_pending_count);
        tvApprovedCount = (TextView) root.findViewById(R.id.tv_approved_count);
        cardViewPending = root.findViewById(R.id.card_view_pending);
        cardViewAll = root.findViewById(R.id.card_view_all);
        cardViewRecords = root.findViewById(R.id.card_view_records);

        // Load statistics
        loadStatistics();

        // Set up click listeners
        setupClickListeners();

        return root;
    }

    private void loadStatistics() {
        // Get pending applications count
        int pendingCount = 0;
        int approvedCount = 0;

        Cursor allLoans = myData.ViewAllLoans();
        if (allLoans != null) {
            while (allLoans.moveToNext()) {
                String status = allLoans.getString(5); // Assuming status is at index 5
                if ("Pending".equals(status)) {
                    pendingCount++;
                } else if ("Approved".equals(status)) {
                    approvedCount++;
                }
            }
            allLoans.close();
        }

        // Update UI
        tvPendingCount.setText(String.valueOf(pendingCount));
        tvApprovedCount.setText(String.valueOf(approvedCount));
    }

    private void setupClickListeners() {
        cardViewPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPendingApplications();
            }
        });

        cardViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllApplications();
            }
        });

        cardViewRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllRecords();
            }
        });
    }

    private void showPendingApplications() {
        StringBuilder pendingList = new StringBuilder();
        Cursor allLoans = myData.ViewAllLoans();

        if (allLoans != null && allLoans.getCount() > 0) {
            while (allLoans.moveToNext()) {
                String status = allLoans.getString(5);
                if ("Pending".equals(status)) {
                    String employeeId = allLoans.getString(1);
                    String loanType = allLoans.getString(2);
                    double amount = allLoans.getDouble(3);
                    int months = allLoans.getInt(4);

                    pendingList.append("Employee: ").append(employeeId)
                            .append("\nLoan: ").append(loanType)
                            .append("\nAmount: ₱").append(String.format("%.2f", amount))
                            .append("\nMonths: ").append(months)
                            .append("\nStatus: ").append(status)
                            .append("\n\n");
                }
            }
            allLoans.close();
        }

        if (pendingList.length() == 0) {
            pendingList.append("No pending applications found.");
        }

        showMessageDialog("Pending Loan Applications", pendingList.toString());
    }

    private void showAllApplications() {
        StringBuilder allApplications = new StringBuilder();
        Cursor allLoans = myData.ViewAllLoans();

        if (allLoans != null && allLoans.getCount() > 0) {
            while (allLoans.moveToNext()) {
                String employeeId = allLoans.getString(1);
                String loanType = allLoans.getString(2);
                double amount = allLoans.getDouble(3);
                int months = allLoans.getInt(4);
                String status = allLoans.getString(5);

                allApplications.append("Employee: ").append(employeeId)
                        .append("\nLoan: ").append(loanType)
                        .append("\nAmount: ₱").append(String.format("%.2f", amount))
                        .append("\nMonths: ").append(months)
                        .append("\nStatus: ").append(status)
                        .append("\n\n");
            }
            allLoans.close();
        }

        if (allApplications.length() == 0) {
            allApplications.append("No loan applications found.");
        }

        showMessageDialog("All Loan Applications", allApplications.toString());
    }

    private void showAllRecords() {
        StringBuilder allRecords = new StringBuilder();
        // Since we don't have a method to get all users, we'll show a message
        allRecords.append("Complete member and loan records access.\n\n");
        allRecords.append("This feature would display:\n");
        allRecords.append("• All registered members\n");
        allRecords.append("• Complete loan history\n");
        allRecords.append("• Payment records\n");
        allRecords.append("• System statistics");

        showMessageDialog("All Records", allRecords.toString());
    }

    //Message Window Method (following professor's style)
    public void myMessageWindow(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Refresh statistics after viewing
                loadStatistics();
            }
        });
        builder.show();
    }

    private void showMessageDialog(String title, String message) {
        myMessageWindow(title, message);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh statistics when fragment becomes visible
        loadStatistics();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myData != null) {
            myData.close();
        }
    }
}