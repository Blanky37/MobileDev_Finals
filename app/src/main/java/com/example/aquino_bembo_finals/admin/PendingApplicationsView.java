package com.example.aquino_bembo_finals.admin;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.R;

public class PendingApplicationsView extends AppCompatActivity {

    DatabaseHelper myData = new DatabaseHelper(this);
    LinearLayout ll_applications_container, ll_empty_state, ll_loading;
    TextView tv_pending_count;
    Button btn_refresh, btn_approve_all, btn_deny_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_applications_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ll_applications_container = (LinearLayout) findViewById(R.id.ll_applications_container);
        ll_empty_state = (LinearLayout) findViewById(R.id.ll_empty_state);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_pending_count = (TextView) findViewById(R.id.tv_pending_count);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_approve_all = (Button) findViewById(R.id.btn_approve_all);
        btn_deny_all = (Button) findViewById(R.id.btn_deny_all);

        // Set button click listeners
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPendingApplications();
            }
        });

        btn_approve_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApproveAllConfirmation();
            }
        });

        btn_deny_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDenyAllConfirmation();
            }
        });

        // Load applications initially
        loadPendingApplications();
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

    private void loadPendingApplications() {
        // Show loading state
        ll_loading.setVisibility(View.VISIBLE);
        ll_applications_container.setVisibility(View.GONE);
        ll_empty_state.setVisibility(View.GONE);

        // Clear existing applications
        ll_applications_container.removeAllViews();

        // Get pending applications from database
        Cursor resultSet = myData.ViewAllLoans();
        int pendingCount = 0;

        if (resultSet.getCount() == 0) {
            // No applications found
            ll_loading.setVisibility(View.GONE);
            ll_empty_state.setVisibility(View.VISIBLE);
            tv_pending_count.setText("0 applications pending review");
        } else {
            while (resultSet.moveToNext()) {
                String status = resultSet.getString(5); // Column 5 is LoanStatus

                if (status.equalsIgnoreCase("Pending")) {
                    pendingCount++;

                    // Create application card (dynamic)
                    View applicationCard = createApplicationCard(
                            resultSet.getInt(0),           // LoanID
                            resultSet.getString(1),        // EmployeeID
                            resultSet.getString(2),        // LoanType
                            resultSet.getDouble(3),        // LoanAmount
                            resultSet.getInt(4),           // MonthsToPay
                            resultSet.getString(6)         // ApplicationDate
                    );

                    ll_applications_container.addView(applicationCard);
                }
            }

            // Update UI based on results
            ll_loading.setVisibility(View.GONE);

            if (pendingCount > 0) {
                ll_applications_container.setVisibility(View.VISIBLE);
                tv_pending_count.setText(pendingCount + " application(s) pending review");
            } else {
                ll_empty_state.setVisibility(View.VISIBLE);
                tv_pending_count.setText("0 applications pending review");
            }
        }

        resultSet.close();
    }

    private View createApplicationCard(int loanID, String employeeID, String loanType,
                                       double loanAmount, int monthsToPay, String applicationDate) {
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
        Button btnApprove = (Button) cardView.findViewById(R.id.btn_approve);
        Button btnDeny = (Button) cardView.findViewById(R.id.btn_deny);

        tvEmployeeID.setText(employeeID);
        tvLoanType.setText(loanType);
        tvStatus.setText("Pending"); // Always "Pending" since we filter by pending status

        // Format currency with peso sign
        tvAmount.setText("₱" + String.format("%,.2f", loanAmount));
        tvMonths.setText(monthsToPay + " months");
        tvDateApplied.setText(applicationDate);
        tvLoanID.setText("LOAN" + loanID); // Store loan ID in hidden field

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

    private void showApproveAllConfirmation() {
        // Count pending applications
        int pendingCount = countPendingApplications();
        if (pendingCount == 0) {
            myMessageWindow("No Applications", "There are no pending applications to approve.");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Approve All Applications");
        builder.setMessage("Are you sure you want to approve all " + pendingCount + " pending applications?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                approveAllApplications();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void showDenyAllConfirmation() {
        // Count pending applications first
        int pendingCount = countPendingApplications();

        if (pendingCount == 0) {
            myMessageWindow("No Applications", "There are no pending applications to deny.");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Deny All Applications");
        builder.setMessage("Are you sure you want to deny all " + pendingCount + " pending applications?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                denyAllApplications();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private int countPendingApplications() {
        Cursor resultSet = myData.ViewAllLoans();
        int pendingCount = 0;

        while (resultSet.moveToNext()) {
            String status = resultSet.getString(5); // Column 5 is LoanStatus
            if (status.equalsIgnoreCase("Pending")) {
                pendingCount++;
            }
        }

        resultSet.close();
        return pendingCount;
    }

    private void approveLoanApplication(int loanID) {
        boolean isUpdated = myData.UpdateLoanStatus(loanID, "Approved");

        if (isUpdated) {
            myMessageWindow("Success", "Loan application #" + loanID + " has been approved.");
            loadPendingApplications(); // Refresh the list
        } else {
            myMessageWindow("Error", "Failed to approve loan application.");
        }
    }

    private void denyLoanApplication(int loanID) {
        boolean isUpdated = myData.UpdateLoanStatus(loanID, "Denied");

        if (isUpdated) {
            myMessageWindow("Success", "Loan application #" + loanID + " has been denied.");
            loadPendingApplications(); // Refresh the list
        } else {
            myMessageWindow("Error", "Failed to deny loan application.");
        }
    }

    private void approveAllApplications() {
        Cursor resultSet = myData.ViewAllLoans();
        int approvedCount = 0;

        while (resultSet.moveToNext()) {
            String status = resultSet.getString(5); // Column 5 is LoanStatus
            if (status.equalsIgnoreCase("Pending")) {
                int loanID = resultSet.getInt(0);
                boolean isUpdated = myData.UpdateLoanStatus(loanID, "Approved");
                if (isUpdated) {
                    approvedCount++;
                }
            }
        }

        resultSet.close();

        if (approvedCount > 0) {
            myMessageWindow("Success", approvedCount + " loan application(s) have been approved.");
            loadPendingApplications(); // Refresh the list
        } else {
            myMessageWindow("Info", "No applications were approved.");
        }
    }

    private void denyAllApplications() {
        Cursor resultSet = myData.ViewAllLoans();
        int deniedCount = 0;

        while (resultSet.moveToNext()) {
            String status = resultSet.getString(5); // Column 5 is LoanStatus
            if (status.equalsIgnoreCase("Pending")) {
                int loanID = resultSet.getInt(0);
                boolean isUpdated = myData.UpdateLoanStatus(loanID, "Denied");
                if (isUpdated) {
                    deniedCount++;
                }
            }
        }

        resultSet.close();

        if (deniedCount > 0) {
            myMessageWindow("Success", deniedCount + " loan application(s) have been denied.");
            loadPendingApplications(); // Refresh the list
        } else {
            myMessageWindow("Info", "No applications were denied.");
        }
    }
}