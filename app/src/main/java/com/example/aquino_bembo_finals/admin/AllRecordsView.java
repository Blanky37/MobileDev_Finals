package com.example.aquino_bembo_finals.admin;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.android.material.button.MaterialButton;

public class AllRecordsView extends AppCompatActivity {

    DatabaseHelper myData;
    LinearLayout ll_records_container, ll_empty_state, ll_loading;
    TextView tv_section_title;
    MaterialButton btn_view_users, btn_view_loans, btn_view_both;

    // Track current view mode
    private String currentViewMode = "users"; // "users", "loans", "both"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_records_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DatabaseHelper
        myData = new DatabaseHelper(this);

        // Initialize UI elements
        ll_records_container = findViewById(R.id.ll_records_container);
        ll_empty_state = findViewById(R.id.ll_empty_state);
        ll_loading = findViewById(R.id.ll_loading);
        tv_section_title = findViewById(R.id.tv_section_title);
        btn_view_users = findViewById(R.id.btn_view_users);
        btn_view_loans = findViewById(R.id.btn_view_loans);
        btn_view_both = findViewById(R.id.btn_view_both);

        // Set initial button colors
        setActiveButton(btn_view_users);

        // Set button click listeners
        btn_view_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentViewMode = "users";
                setActiveButton(btn_view_users);
                tv_section_title.setText("👥 All Users");
                loadUsers();
            }
        });

        btn_view_loans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentViewMode = "loans";
                setActiveButton(btn_view_loans);
                tv_section_title.setText("All Loan Applications");
                loadLoans();
            }
        });

        btn_view_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentViewMode = "both";
                setActiveButton(btn_view_both);
                tv_section_title.setText("All Records");
                loadBoth();
            }
        });

        // Load initial data (users by default)
        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        refreshCurrentView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database connection if needed
        if (myData != null) {
            myData.close();
        }
    }

    public void myMessageWindow(String title, String message) {
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

    private void setActiveButton(MaterialButton activeButton) {
        // Reset all buttons to default color
        btn_view_users.setBackgroundColor(Color.parseColor("#E0E0E0"));
        btn_view_loans.setBackgroundColor(Color.parseColor("#E0E0E0"));
        btn_view_both.setBackgroundColor(Color.parseColor("#E0E0E0"));

        btn_view_users.setTextColor(Color.parseColor("#2C3E50"));
        btn_view_loans.setTextColor(Color.parseColor("#2C3E50"));
        btn_view_both.setTextColor(Color.parseColor("#2C3E50"));

        // Set active button color based on which button it is
        if (activeButton == btn_view_users) {
            activeButton.setBackgroundColor(Color.parseColor("#3498DB"));
            activeButton.setTextColor(Color.WHITE);
        } else if (activeButton == btn_view_loans) {
            activeButton.setBackgroundColor(Color.parseColor("#9B59B6"));
            activeButton.setTextColor(Color.WHITE);
        } else if (activeButton == btn_view_both) {
            activeButton.setBackgroundColor(Color.parseColor("#27AE60"));
            activeButton.setTextColor(Color.WHITE);
        }
    }

    private void loadUsers() {
        // Show loading state
        showLoadingState();

        // Clear existing records
        ll_records_container.removeAllViews();

        // Get all users from database using the new method
        Cursor cursor = null;
        try {
            cursor = myData.GetAllUsers();

            if (cursor.getCount() == 0) {
                showEmptyState("No users found in the system.");
            } else {
                while (cursor.moveToNext()) {
                    String employeeID = cursor.getString(0);
                    String firstName = cursor.getString(1);
                    String lastName = cursor.getString(2);
                    String dateHired = cursor.getString(3);
                    double basicSalary = cursor.getDouble(4);
                    int isAdmin = cursor.getInt(5);

                    View userCard = createUserCard(employeeID, firstName, lastName,
                            dateHired, basicSalary, isAdmin);
                    ll_records_container.addView(userCard);
                }
                showRecordsContainer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            myMessageWindow("Error", "Failed to load users: " + e.getMessage());
            showEmptyState("Error loading users.");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loadLoans() {
        // Show loading state
        showLoadingState();

        // Clear existing records
        ll_records_container.removeAllViews();

        // Get all loans from database using the new method
        Cursor cursor = null;
        try {
            cursor = myData.GetAllLoansDetailed();

            if (cursor.getCount() == 0) {
                showEmptyState("No loan applications found.");
            } else {
                while (cursor.moveToNext()) {
                    int loanID = cursor.getInt(0);
                    String employeeID = cursor.getString(1);
                    String loanType = cursor.getString(2);
                    double loanAmount = cursor.getDouble(3);
                    int monthsToPay = cursor.getInt(4);
                    String status = cursor.getString(5);
                    String applicationDate = cursor.getString(6);

                    View loanCard = createLoanCard(loanID, employeeID, loanType,
                            loanAmount, monthsToPay, status, applicationDate);
                    ll_records_container.addView(loanCard);
                }
                showRecordsContainer();
            }
        } catch (Exception e) {
            e.printStackTrace();
            myMessageWindow("Error", "Failed to load loans: " + e.getMessage());
            showEmptyState("Error loading loans.");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loadBoth() {
        // Show loading state
        showLoadingState();

        // Clear existing records
        ll_records_container.removeAllViews();

        // Load users first
        Cursor usersCursor = null;
        boolean hasRecords = false;

        try {
            usersCursor = myData.GetAllUsers();

            if (usersCursor.getCount() > 0) {
                hasRecords = true;
                while (usersCursor.moveToNext()) {
                    String employeeID = usersCursor.getString(0);
                    String firstName = usersCursor.getString(1);
                    String lastName = usersCursor.getString(2);
                    String dateHired = usersCursor.getString(3);
                    double basicSalary = usersCursor.getDouble(4);
                    int isAdmin = usersCursor.getInt(5);

                    View userCard = createUserCard(employeeID, firstName, lastName,
                            dateHired, basicSalary, isAdmin);
                    ll_records_container.addView(userCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (usersCursor != null) {
                usersCursor.close();
            }
        }

        // Load loans
        Cursor loansCursor = null;
        try {
            loansCursor = myData.GetAllLoansDetailed();
            if (loansCursor.getCount() > 0) {
                hasRecords = true;
                while (loansCursor.moveToNext()) {
                    int loanID = loansCursor.getInt(0);
                    String employeeID = loansCursor.getString(1);
                    String loanType = loansCursor.getString(2);
                    double loanAmount = loansCursor.getDouble(3);
                    int monthsToPay = loansCursor.getInt(4);
                    String status = loansCursor.getString(5);
                    String applicationDate = loansCursor.getString(6);

                    View loanCard = createLoanCard(loanID, employeeID, loanType,
                            loanAmount, monthsToPay, status, applicationDate);
                    ll_records_container.addView(loanCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (loansCursor != null) {
                loansCursor.close();
            }
        }

        if (!hasRecords) {
            showEmptyState("No records found in the system.");
        } else {
            showRecordsContainer();
        }
    }

    private View createUserCard(String employeeID, String firstName, String lastName,
                                String dateHired, double basicSalary, int isAdmin) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.card_record_user, null);

        TextView tvEmployeeID = cardView.findViewById(R.id.tv_employee_id);
        TextView tvName = cardView.findViewById(R.id.tv_name);
        TextView tvDateHired = cardView.findViewById(R.id.tv_date_hired);
        TextView tvSalary = cardView.findViewById(R.id.tv_salary);
        TextView tvRole = cardView.findViewById(R.id.tv_role);

        tvEmployeeID.setText("ID: " + employeeID);
        tvName.setText(firstName + " " + lastName);
        tvDateHired.setText("Hired: " + dateHired);
        tvSalary.setText("Salary: ₱" + String.format("%,.2f", basicSalary));

        if (isAdmin == 1) {
            tvRole.setText("Administrator");
            tvRole.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            tvRole.setTextColor(Color.WHITE);
        } else {
            tvRole.setText("Employee");
            tvRole.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvRole.setTextColor(Color.WHITE);
        }

        return cardView;
    }

    private View createLoanCard(int loanID, String employeeID, String loanType,
                                double loanAmount, int monthsToPay, String status,
                                String applicationDate) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.card_record_loan, null);

        TextView tvLoanID = cardView.findViewById(R.id.tv_loan_id);
        TextView tvEmployeeID = cardView.findViewById(R.id.tv_employee_id);
        TextView tvLoanType = cardView.findViewById(R.id.tv_loan_type);
        TextView tvAmount = cardView.findViewById(R.id.tv_amount);
        TextView tvMonths = cardView.findViewById(R.id.tv_months);
        TextView tvStatus = cardView.findViewById(R.id.tv_status);
        TextView tvDate = cardView.findViewById(R.id.tv_date);

        tvLoanID.setText("Loan #" + loanID);
        tvEmployeeID.setText("Employee: " + employeeID);
        tvLoanType.setText("Type: " + loanType);
        tvAmount.setText("Amount: ₱" + String.format("%,.2f", loanAmount));
        tvMonths.setText("Term: " + monthsToPay + " months");
        tvStatus.setText("Status: " + status);
        tvDate.setText("Applied: " + applicationDate);

        // Set status color
        if (status.equalsIgnoreCase("Pending")) {
            tvStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
            tvStatus.setTextColor(Color.WHITE);
        } else if (status.equalsIgnoreCase("Approved")) {
            tvStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            tvStatus.setTextColor(Color.WHITE);
        } else if (status.equalsIgnoreCase("Denied")) {
            tvStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            tvStatus.setTextColor(Color.WHITE);
        }

        return cardView;
    }

    private void showLoadingState() {
        ll_loading.setVisibility(View.VISIBLE);
        ll_records_container.setVisibility(View.GONE);
        ll_empty_state.setVisibility(View.GONE);
    }

    private void showRecordsContainer() {
        ll_loading.setVisibility(View.GONE);
        ll_records_container.setVisibility(View.VISIBLE);
        ll_empty_state.setVisibility(View.GONE);
    }

    private void showEmptyState(String message) {
        ll_loading.setVisibility(View.GONE);
        ll_records_container.setVisibility(View.GONE);
        ll_empty_state.setVisibility(View.VISIBLE);

        // Update the empty state message
        TextView tvEmptyMessage = ll_empty_state.findViewById(R.id.tv_empty_message);
        if (tvEmptyMessage != null) {
            tvEmptyMessage.setText(message);
        } else {
            // If the TextView doesn't exist, try to find the first TextView
            if (ll_empty_state.getChildCount() > 1 && ll_empty_state.getChildAt(1) instanceof TextView) {
                ((TextView) ll_empty_state.getChildAt(1)).setText(message);
            }
        }
    }

    private void refreshCurrentView() {
        switch (currentViewMode) {
            case "users":
                loadUsers();
                break;
            case "loans":
                loadLoans();
                break;
            case "both":
                loadBoth();
                break;
        }
    }
}