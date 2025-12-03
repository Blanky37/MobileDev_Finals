package com.example.aquino_bembo_finals.admin;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.R;

public class AllApplicationsView extends AppCompatActivity {

    DatabaseHelper myData = new DatabaseHelper(this);
    LinearLayout ll_applications_container, ll_empty_state, ll_loading;
    TextView tv_total_count, tv_pending_count, tv_approved_count, tv_denied_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_applications_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ll_applications_container = (LinearLayout) findViewById(R.id.ll_applications_container);
        ll_empty_state = (LinearLayout) findViewById(R.id.ll_empty_state);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_total_count = (TextView) findViewById(R.id.tv_total_count);
        tv_pending_count = (TextView) findViewById(R.id.tv_pending_count);
        tv_approved_count = (TextView) findViewById(R.id.tv_approved_count);
        tv_denied_count = (TextView) findViewById(R.id.tv_denied_count);

        // Load all applications initially
        loadAllApplications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadAllApplications();
    }

    public void myMessageWindow(String title, String message) {
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

    private void loadAllApplications() {
        // Show loading state
        ll_loading.setVisibility(View.VISIBLE);
        ll_applications_container.setVisibility(View.GONE);
        ll_empty_state.setVisibility(View.GONE);

        // Clear existing applications
        ll_applications_container.removeAllViews();

        // Get all applications from database
        Cursor resultSet = myData.ViewAllLoans();
        int totalCount = 0;
        int pendingCount = 0;
        int approvedCount = 0;
        int deniedCount = 0;

        if (resultSet.getCount() == 0) {
            // No applications found
            ll_loading.setVisibility(View.GONE);
            ll_empty_state.setVisibility(View.VISIBLE);
            tv_total_count.setText("Total: 0 applications");
            tv_pending_count.setText("0");
            tv_approved_count.setText("0");
            tv_denied_count.setText("0");
        } else {
            while (resultSet.moveToNext()) {
                totalCount++;
                String status = resultSet.getString(5); // Column 5 is LoanStatus

                // Count by status
                if (status.equalsIgnoreCase("Pending")) {
                    pendingCount++;
                } else if (status.equalsIgnoreCase("Approved")) {
                    approvedCount++;
                } else if (status.equalsIgnoreCase("Denied")) {
                    deniedCount++;
                }

                // Create application card for ALL applications (not just pending)
                View applicationCard = createApplicationCard(
                        resultSet.getInt(0),           // LoanID
                        resultSet.getString(1),        // EmployeeID
                        resultSet.getString(2),        // LoanType
                        resultSet.getDouble(3),        // LoanAmount
                        resultSet.getInt(4),           // MonthsToPay
                        status,                        // LoanStatus
                        resultSet.getString(6)         // ApplicationDate
                );

                ll_applications_container.addView(applicationCard);
            }

            // Update statistics
            tv_total_count.setText("Total: " + totalCount + " application(s)");
            tv_pending_count.setText(String.valueOf(pendingCount));
            tv_approved_count.setText(String.valueOf(approvedCount));
            tv_denied_count.setText(String.valueOf(deniedCount));

            // Update UI based on results
            ll_loading.setVisibility(View.GONE);

            if (totalCount > 0) {
                ll_applications_container.setVisibility(View.VISIBLE);
            } else {
                ll_empty_state.setVisibility(View.VISIBLE);
            }
        }

        resultSet.close();
    }

    private View createApplicationCard(int loanID, String employeeID, String loanType,
                                       double loanAmount, int monthsToPay, String status,
                                       String applicationDate) {
        // Inflate the card layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.card_application, null);

        // Set data to card views
        TextView tvEmployeeID = (TextView) cardView.findViewById(R.id.tv_employee_id);
        TextView tvLoanType = (TextView) cardView.findViewById(R.id.tv_loan_type);
        TextView tvStatus = (TextView) cardView.findViewById(R.id.tv_status);
        TextView tvAmount = (TextView) cardView.findViewById(R.id.tv_amount);
        TextView tvMonths = (TextView) cardView.findViewById(R.id.tv_months);
        TextView tvDateApplied = (TextView) cardView.findViewById(R.id.tv_date_applied);
        TextView tvLoanID = (TextView) cardView.findViewById(R.id.tv_loan_id);
        LinearLayout llActionButtons = (LinearLayout) cardView.findViewById(R.id.ll_action_buttons);
        Button btnApprove = (Button) cardView.findViewById(R.id.btn_approve);
        Button btnDeny = (Button) cardView.findViewById(R.id.btn_deny);

        tvEmployeeID.setText(employeeID);
        tvLoanType.setText(loanType);
        tvAmount.setText("₱" + String.format("%,.2f", loanAmount));
        tvMonths.setText(monthsToPay + " months");
        tvDateApplied.setText(applicationDate);
        tvLoanID.setText("LOAN" + loanID);

        // Set status with appropriate color
        tvStatus.setText(status);
        if (status.equalsIgnoreCase("Pending")) {
            tvStatus.setBackgroundResource(android.R.color.holo_orange_dark);
        } else if (status.equalsIgnoreCase("Approved")) {
            tvStatus.setBackgroundResource(android.R.color.holo_green_dark);
        } else if (status.equalsIgnoreCase("Denied")) {
            tvStatus.setBackgroundResource(android.R.color.holo_red_dark);
        }

        // Show action buttons only for pending applications
        if (status.equalsIgnoreCase("Pending")) {
            llActionButtons.setVisibility(View.VISIBLE);

            // Set click listeners for approve and deny buttons
            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showApproveConfirmation(loanID, employeeID);
                }
            });

            btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDenyConfirmation(loanID, employeeID);
                }
            });
        } else {
            llActionButtons.setVisibility(View.GONE);
        }

        return cardView;
    }

    private void showApproveConfirmation(int loanID, String employeeID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Approve Application");
        builder.setMessage("Are you sure you want to approve loan #" + loanID + " for employee " + employeeID + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                approveLoanApplication(loanID);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void showDenyConfirmation(int loanID, String employeeID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Deny Application");
        builder.setMessage("Are you sure you want to deny loan #" + loanID + " for employee " + employeeID + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                denyLoanApplication(loanID);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void approveLoanApplication(int loanID) {
        boolean isUpdated = myData.UpdateLoanStatus(loanID, "Approved");

        if (isUpdated) {
            myMessageWindow("Success", "Loan application #" + loanID + " has been approved.");
            loadAllApplications(); // Refresh the list
        } else {
            myMessageWindow("Error", "Failed to approve loan application.");
        }
    }

    private void denyLoanApplication(int loanID) {
        boolean isUpdated = myData.UpdateLoanStatus(loanID, "Denied");

        if (isUpdated) {
            myMessageWindow("Success", "Loan application #" + loanID + " has been denied.");
            loadAllApplications(); // Refresh the list
        } else {
            myMessageWindow("Error", "Failed to deny loan application.");
        }
    }
}