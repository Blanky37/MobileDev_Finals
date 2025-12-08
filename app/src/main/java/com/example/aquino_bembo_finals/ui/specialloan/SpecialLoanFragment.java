package com.example.aquino_bembo_finals.ui.specialloan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;

import com.example.aquino_bembo_finals.MainActivity;
import com.example.aquino_bembo_finals.R;
import com.example.aquino_bembo_finals.LoanComputation;
import com.example.aquino_bembo_finals.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecialLoanFragment extends Fragment {

    private TextInputEditText etLoanAmount, etMonths;
    private MaterialButton btnCalculate, btnApply;
    private View cardResults;
    private DatabaseHelper databaseHelper;

    private android.widget.TextView tvResultLoanAmount, tvResultMonths, tvResultInterestRate,
            tvResultInterest, tvResultTotal, tvResultMonthly;

    private String currentEmployeeId;
    private boolean hasAccess = false;
    private boolean hasPendingLoan = false;

    public SpecialLoanFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_special_loan, container, false);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Get current employee ID from arguments
        if (getArguments() != null) {
            currentEmployeeId = getArguments().getString("EMPLOYEE_ID", "");
        }

        // If not from arguments, try to get from MainActivity
        if ((currentEmployeeId == null || currentEmployeeId.isEmpty()) && getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            currentEmployeeId = mainActivity.getCurrentEmployeeId();
        }

        // Check if employee has access (5+ years of service)
        checkEmployeeEligibility();

        if (hasAccess) {
            checkPendingLoans();
        }

        initializeViews(view);

        setupClickListeners();

        return view;
    }

    private void checkPendingLoans() {
        if (currentEmployeeId == null || currentEmployeeId.isEmpty()) {
            return;
        }

        Cursor cursor = databaseHelper.ViewUserLoans(currentEmployeeId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String loanType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOAN_TYPE));
                String loanStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOAN_STATUS));

                if (loanType.equals("Special Loan") && loanStatus.equals("Pending")) {
                    hasPendingLoan = true;
                    break;
                }
            }
            cursor.close();
        }
    }

    private void checkEmployeeEligibility() {
        if (currentEmployeeId == null || currentEmployeeId.isEmpty()) {
            showMessage("Access Denied", "Employee information not found. Please log in again.");
            hasAccess = false;
            return;
        }

        // Get employee details
        boolean found = databaseHelper.GetUserDetails(currentEmployeeId);
        if (!found) {
            showMessage("Access Denied", "Employee record not found.");
            hasAccess = false;
            return;
        }

        // Get hire date
        String hireDateStr = databaseHelper.getDateHired();
        if (hireDateStr == null || hireDateStr.isEmpty()) {
            showMessage("Access Denied", "Hire date information is missing.");
            hasAccess = false;
            return;
        }

        try {
            // Parse hire date - try multiple formats
            SimpleDateFormat sdf;
            Date hireDate = null;

            // Try MM/dd/yyyy format first (from registration)
            try {
                sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                hireDate = sdf.parse(hireDateStr);
            } catch (Exception e1) {
                // If that fails, try yyyy-MM-dd format
                try {
                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    hireDate = sdf.parse(hireDateStr);
                } catch (Exception e2) {
                    // Try other possible formats
                    try {
                        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        hireDate = sdf.parse(hireDateStr);
                    } catch (Exception e3) {
                        // Last try with default locale
                        sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        hireDate = sdf.parse(hireDateStr);
                    }
                }
            }

            if (hireDate == null) {
                showMessage("Access Denied", "Invalid hire date format: " + hireDateStr);
                hasAccess = false;
                return;
            }

            // Calculate years of service
            Calendar hireCal = Calendar.getInstance();
            hireCal.setTime(hireDate);

            Calendar currentCal = Calendar.getInstance();

            int yearsOfService = currentCal.get(Calendar.YEAR) - hireCal.get(Calendar.YEAR);

            // Adjust if haven't reached the hire month/day yet this year
            if (currentCal.get(Calendar.MONTH) < hireCal.get(Calendar.MONTH) ||
                    (currentCal.get(Calendar.MONTH) == hireCal.get(Calendar.MONTH) &&
                            currentCal.get(Calendar.DAY_OF_MONTH) < hireCal.get(Calendar.DAY_OF_MONTH))) {
                yearsOfService--;
            }

            // Check if employee has 5 or more years of service
            if (yearsOfService >= 5) {
                hasAccess = true;
                // Show welcome message for eligible employees
                showMessage("Eligibility Verified",
                        "Welcome " + databaseHelper.getFullName() + "!\n" +
                                "You have " + yearsOfService + " years of service.\n" +
                                "You are eligible for Special Loan.");
            } else {
                hasAccess = false;
                showMessage("Access Denied",
                        "Special Loan is only available to employees with 5 or more years of service.\n\n" +
                                "Your years of service: " + yearsOfService + " years\n" +
                                "Required: 5 years or more\n\n" +
                                "Please consider other loan types.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error", "Unable to verify eligibility. Please try again. Error: " + e.getMessage());
            hasAccess = false;
        }
    }

    private void initializeViews(View view) {
        etLoanAmount = view.findViewById(R.id.et_loan_amount);
        etMonths = view.findViewById(R.id.et_months);

        btnCalculate = view.findViewById(R.id.btn_calculate);
        btnApply = view.findViewById(R.id.btn_apply);

        cardResults = view.findViewById(R.id.card_results);
        tvResultLoanAmount = view.findViewById(R.id.tv_result_loan_amount);
        tvResultMonths = view.findViewById(R.id.tv_result_months);
        tvResultInterestRate = view.findViewById(R.id.tv_result_interest_rate);
        tvResultInterest = view.findViewById(R.id.tv_result_interest);
        tvResultTotal = view.findViewById(R.id.tv_result_total);
        tvResultMonthly = view.findViewById(R.id.tv_result_monthly);

        if (!hasAccess) {
            etLoanAmount.setEnabled(false);
            etMonths.setEnabled(false);
            btnCalculate.setEnabled(false);
            btnApply.setEnabled(false);

            // Show message that inputs are disabled and set text to show they need eligibility
            etLoanAmount.setText("");
            etMonths.setText("");
        } else if (hasPendingLoan) {
            etLoanAmount.setEnabled(false);
            etMonths.setEnabled(false);
            btnCalculate.setEnabled(false);
            btnApply.setEnabled(false);

            showMessage("Pending Application",
                    "You already have a pending Special Loan application.\n\n" +
                            "You cannot apply for another Special Loan until your current application is reviewed by the administrator.");
        }
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check access before calculating
                if (!hasAccess) {
                    showMessage("Access Denied", "You are not eligible for Special Loan. This loan type requires 5 or more years of service.");
                    return;
                }
                // Check if has pending loan
                if (hasPendingLoan) {
                    showMessage("Pending Application",
                            "You already have a pending Special Loan application.\n\n" +
                                    "You cannot apply for another Special Loan until your current application is reviewed by the administrator.");
                    return;
                }
                calculateLoan();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check access before applying
                if (!hasAccess) {
                    showMessage("Access Denied", "You are not eligible for Special Loan. This loan type requires 5 or more years of service.");
                    return;
                }
                // Check if has pending loan
                if (hasPendingLoan) {
                    showMessage("Pending Application",
                            "You already have a pending Special Loan application.\n\n" +
                                    "You cannot apply for another Special Loan until your current application is reviewed by the administrator.");
                    return;
                }
                applyForLoan();
            }
        });
    }

    private void calculateLoan() {
        try {
            // Get loan amount from input
            String amountStr = etLoanAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                showMessage("Input Required", "Please enter loan amount");
                hideResultsCard();
                return;
            }

            // Get months from input
            String monthsStr = etMonths.getText().toString().trim();
            if (monthsStr.isEmpty()) {
                showMessage("Input Required", "Please enter number of months");
                hideResultsCard();
                return;
            }

            double loanAmount = Double.parseDouble(amountStr);
            int months = Integer.parseInt(monthsStr);

            // Perform calculation
            LoanComputation.SpecialLoanResult result = LoanComputation.calculateSpecialLoan(
                    loanAmount,
                    months,
                    new LoanComputation.LoanErrorListener() {
                        @Override
                        public void onLoanError(String title, String message) {
                            showMessage(title, message);
                            hideResultsCard();
                        }
                    }
            );

            if (result == null) {
                hideResultsCard();
                return;
            }

            updateResultsUI(result);

            cardResults.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            showMessage("Invalid Input", "Please enter valid numeric values");
            hideResultsCard();
        } catch (Exception e) {
            showMessage("Calculation Error", "An error occurred while calculating the loan. Please try again.");
            hideResultsCard();
        }
    }

    private void hideResultsCard() {
        if (cardResults.getVisibility() == View.VISIBLE) {
            cardResults.setVisibility(View.GONE);
        }
    }

    private void updateResultsUI(LoanComputation.SpecialLoanResult result) {
        // Update all result TextViews with formatted values
        tvResultLoanAmount.setText(LoanComputation.formatCurrency(result.loanAmount));
        tvResultMonths.setText(String.valueOf(result.months));
        tvResultInterestRate.setText(LoanComputation.formatPercent(result.interestRate));
        tvResultInterest.setText(LoanComputation.formatCurrency(result.interest));
        tvResultTotal.setText(LoanComputation.formatCurrency(result.totalAmount));
        tvResultMonthly.setText(LoanComputation.formatCurrency(result.monthlyPayment));
    }

    private void applyForLoan() {
        if (cardResults.getVisibility() != View.VISIBLE) {
            showMessage("Calculation Required", "Please calculate the loan first before applying");
            return;
        }

        // Get the calculated values
        double loanAmount = 0;
        int months = 0;

        try {
            String amountStr = etLoanAmount.getText().toString().trim();
            String monthsStr = etMonths.getText().toString().trim();

            if (!amountStr.isEmpty()) {
                loanAmount = Double.parseDouble(amountStr);
            }
            if (!monthsStr.isEmpty()) {
                months = Integer.parseInt(monthsStr);
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid Data", "Please recalculate the loan before applying");
            return;
        }

        // Calculate again to get all values
        LoanComputation.SpecialLoanResult result = LoanComputation.calculateSpecialLoan(
                loanAmount,
                months,
                new LoanComputation.LoanErrorListener() {
                    @Override
                    public void onLoanError(String title, String message) {
                        showMessage(title, message);
                    }
                }
        );

        if (result == null) {
            return;
        }

        showConfirmationDialog(loanAmount, months, result);
    }

    private void showConfirmationDialog(double loanAmount, int months, LoanComputation.SpecialLoanResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm Loan Application");
        builder.setMessage(String.format("Are you sure you want to apply for this Special Loan?\n\n" +
                        "Loan Amount: %s\n" +
                        "Duration: %d months\n" +
                        "Interest Rate: %s\n" +
                        "Interest: %s\n" +
                        "Total Amount: %s\n" +
                        "Monthly Payment: %s\n\n" +
                        "This application will be submitted for review.",
                LoanComputation.formatCurrency(loanAmount),
                months,
                LoanComputation.formatPercent(result.interestRate),
                LoanComputation.formatCurrency(result.interest),
                LoanComputation.formatCurrency(result.totalAmount),
                LoanComputation.formatCurrency(result.monthlyPayment)));

        builder.setPositiveButton("Yes, Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Save loan application to database
                boolean isSaved = databaseHelper.SaveLoanApplication(
                        currentEmployeeId,
                        "Special Loan",
                        result.loanAmount,
                        result.months,
                        result.interestRate,
                        0.00,
                        result.totalAmount,
                        result.monthlyPayment,
                        "Pending"
                );

                if (isSaved) {
                    showMessage("Application Submitted",
                            "Your Special Loan application has been submitted successfully!\n\n" +
                                    "Status: Pending Review\n" +
                                    "You will be notified once your application is reviewed by the administrator.");

                    resetForm();
                    // Update pending loan status
                    hasPendingLoan = true;
                    disableForm();
                } else {
                    showMessage("Submission Failed",
                            "Failed to submit loan application. Please try again.");
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User cancelled, do nothing
            }
        });

        builder.show();
    }

    private void disableForm() {
        etLoanAmount.setEnabled(false);
        etMonths.setEnabled(false);
        btnCalculate.setEnabled(false);
        btnApply.setEnabled(false);
    }

    private void resetForm() {
        etLoanAmount.setText("");
        etMonths.setText("");

        cardResults.setVisibility(View.GONE);

        tvResultLoanAmount.setText("₱0.00");
        tvResultMonths.setText("0");
        tvResultInterestRate.setText("0.00%");
        tvResultInterest.setText("₱0.00");
        tvResultTotal.setText("₱0.00");
        tvResultMonthly.setText("₱0.00");
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing, just close the dialog
            }
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}