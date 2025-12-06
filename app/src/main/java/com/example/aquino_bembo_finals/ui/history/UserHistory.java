package com.example.aquino_bembo_finals.ui.history;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHistory extends Fragment {

    private DatabaseHelper myData;
    private LinearLayout ll_history_container, ll_empty_state, ll_loading;
    private TextView tv_history_count, tv_pending_count, tv_approved_count, tv_denied_count;
    private String currentEmployeeID;

    public UserHistory() {
        // Required empty public constructor
    }

    public static UserHistory newInstance() {
        return new UserHistory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_history, container, false);

        // Initialize UI elements
        ll_history_container = (LinearLayout) rootView.findViewById(R.id.ll_history_container);
        ll_empty_state = (LinearLayout) rootView.findViewById(R.id.ll_empty_state);
        ll_loading = (LinearLayout) rootView.findViewById(R.id.ll_loading);
        tv_history_count = (TextView) rootView.findViewById(R.id.tv_history_count);
        tv_pending_count = (TextView) rootView.findViewById(R.id.tv_pending_count);
        tv_approved_count = (TextView) rootView.findViewById(R.id.tv_approved_count);
        tv_denied_count = (TextView) rootView.findViewById(R.id.tv_denied_count);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myData = new DatabaseHelper(getActivity());

        // Get current user's employee ID from SharedPreferences(see login.java)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentEmployeeID = sharedPreferences.getString("employeeID", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load user history when fragment becomes visible
        loadUserHistory();
    }

    public void myMessageWindow(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void loadUserHistory() {
        // Check if user is logged in
        if (currentEmployeeID == null || currentEmployeeID.isEmpty()) {
            myMessageWindow("Error", "Please login to view your history.");
            ll_loading.setVisibility(View.GONE);
            ll_empty_state.setVisibility(View.VISIBLE);
            return;
        }

        // Show loading state
        ll_loading.setVisibility(View.VISIBLE);
        ll_history_container.setVisibility(View.GONE);
        ll_empty_state.setVisibility(View.GONE);

        // Clear existing history cards
        ll_history_container.removeAllViews();

        // Get user's loan applications from database
        Cursor resultSet = myData.ViewUserLoans(currentEmployeeID);

        if (resultSet == null) {
            myMessageWindow("Error", "Failed to load history. Please try again.");
            ll_loading.setVisibility(View.GONE);
            ll_empty_state.setVisibility(View.VISIBLE);
            return;
        }

        int totalCount = 0;
        int pendingCount = 0;
        int approvedCount = 0;
        int deniedCount = 0;

        if (resultSet.getCount() == 0) {
            // No applications found
            ll_loading.setVisibility(View.GONE);
            ll_empty_state.setVisibility(View.VISIBLE);
            tv_history_count.setText("0 loan applications");
            tv_pending_count.setText("0");
            tv_approved_count.setText("0");
            tv_denied_count.setText("0");
        } else {
            while (resultSet.moveToNext()) {
                totalCount++;
                // Adjust column indices based on your ViewUserLoans method
                // Based on your DatabaseHelper, columns are:
                // 0: LoanID, 1: LoanType, 2: LoanAmount, 3: MonthsToPay, 4: LoanStatus, 5: ApplicationDate
                String status = resultSet.getString(4); // Column 4 is LoanStatus

                // Count by status
                if (status.equalsIgnoreCase("Pending")) {
                    pendingCount++;
                } else if (status.equalsIgnoreCase("Approved")) {
                    approvedCount++;
                } else if (status.equalsIgnoreCase("Denied")) {
                    deniedCount++;
                }

                // Create history card for each application
                View historyCard = createHistoryCard(
                        resultSet.getInt(0),           // LoanID
                        resultSet.getString(1),        // LoanType
                        resultSet.getDouble(2),        // LoanAmount
                        resultSet.getInt(3),           // MonthsToPay
                        status,                        // LoanStatus
                        resultSet.getString(5)         // ApplicationDate
                );

                ll_history_container.addView(historyCard);
            }

            // Update statistics
            tv_history_count.setText(totalCount + " loan application(s)");
            tv_pending_count.setText(String.valueOf(pendingCount));
            tv_approved_count.setText(String.valueOf(approvedCount));
            tv_denied_count.setText(String.valueOf(deniedCount));

            // Update UI based on results
            ll_loading.setVisibility(View.GONE);

            if (totalCount > 0) {
                ll_history_container.setVisibility(View.VISIBLE);
            } else {
                ll_empty_state.setVisibility(View.VISIBLE);
            }
        }

        resultSet.close();
    }

    private View createHistoryCard(int loanID, String loanType, double loanAmount,
                                   int monthsToPay, String status, String applicationDate) {
        // Inflate the card layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View cardView = inflater.inflate(R.layout.card_history, null);

        // Set data to card views
        TextView tvLoanType = (TextView) cardView.findViewById(R.id.tv_loan_type);
        TextView tvStatus = (TextView) cardView.findViewById(R.id.tv_status);
        TextView tvAmount = (TextView) cardView.findViewById(R.id.tv_amount);
        TextView tvMonths = (TextView) cardView.findViewById(R.id.tv_months);
        TextView tvDate = (TextView) cardView.findViewById(R.id.tv_date);
        TextView tvLoanID = (TextView) cardView.findViewById(R.id.tv_loan_id);

        tvLoanType.setText(loanType);
        tvAmount.setText("₱" + String.format("%,.2f", loanAmount));
        tvMonths.setText(monthsToPay + " months");
        tvDate.setText("Applied: " + applicationDate);
        tvLoanID.setText("ID: " + loanID);

        // Set status with appropriate color
        tvStatus.setText(status);
        int colorResId = 0; // Default value
        if (status.equalsIgnoreCase("Pending")) {
            colorResId = R.color.status_pending;
        } else if (status.equalsIgnoreCase("Approved")) {
            colorResId = R.color.status_approved;
        } else if (status.equalsIgnoreCase("Denied")) {
            colorResId = R.color.status_denied;
        }

        // Only set the color if match
        if (colorResId != 0) {
            // IMPORTANT: convert the ID (R.color.xxx) to the actual Color Integer
            int resolvedColor = ContextCompat.getColor(getContext(), colorResId);
            tvStatus.setBackgroundColor(resolvedColor);
        }

        // onClick to show details
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoanDetails(loanID, loanType, loanAmount, monthsToPay, status, applicationDate);
            }
        });

        return cardView;
    }

    private void showLoanDetails(int loanID, String loanType, double loanAmount,
                                 int monthsToPay, String status, String applicationDate) {
        String details = "Loan Details:\n\n" +
                "Loan ID: " + loanID + "\n" +
                "Type: " + loanType + "\n" +
                "Amount: ₱" + String.format("%,.2f", loanAmount) + "\n" +
                "Term: " + monthsToPay + " months\n" +
                "Status: " + status + "\n" +
                "Applied: " + applicationDate;

        myMessageWindow("Application #" + loanID, details);
    }
}