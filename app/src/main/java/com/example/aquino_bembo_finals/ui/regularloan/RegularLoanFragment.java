package com.example.aquino_bembo_finals.ui.regularloan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;

import com.example.aquino_bembo_finals.R;
import com.example.aquino_bembo_finals.LoanComputation;
import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegularLoanFragment extends Fragment {

    private TextInputEditText etMonths;
    private MaterialButton btnCalculate, btnApply;
    private View cardResults;
    private DatabaseHelper databaseHelper;
    private String currentEmployeeId;
    private boolean hasPendingLoan = false;

    private android.widget.TextView tvUserBasicSalary, tvLoanableAmount, tvResultBasicSalary, tvResultLoanAmount,
            tvResultMonths, tvResultInterestRate, tvResultInterest, tvResultServiceCharge,
            tvResultTakeHome, tvResultMonthly;

    public RegularLoanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_regular_loan, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        // Get current employee ID from MainActivity
        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            currentEmployeeId = mainActivity.getCurrentEmployeeId();
        }

        checkPendingLoans();
        initializeViews(view);
        loadUserBasicSalary();
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

                if (loanType.equals("Regular Loan") && loanStatus.equals("Pending")) {
                    hasPendingLoan = true;
                    break;
                }
            }
            cursor.close();
        }
    }

    private void loadUserBasicSalary() {
        if (currentEmployeeId == null || currentEmployeeId.isEmpty()) {
            return;
        }

        boolean found = databaseHelper.GetUserDetails(currentEmployeeId);
        if (found) {
            double basicSalary = databaseHelper.getBasicSalary();
            tvUserBasicSalary.setText(LoanComputation.formatCurrency(basicSalary));

            // Calculate and display loanable amount
            double loanableAmount = basicSalary * 2.5;
            tvLoanableAmount.setText(LoanComputation.formatCurrency(loanableAmount));
        }
    }

    private void initializeViews(View view) {
        etMonths = view.findViewById(R.id.et_months);

        btnCalculate = view.findViewById(R.id.btn_calculate);
        btnApply = view.findViewById(R.id.btn_apply);

        cardResults = view.findViewById(R.id.card_results);

        tvUserBasicSalary = view.findViewById(R.id.tv_user_basic_salary);
        tvLoanableAmount = view.findViewById(R.id.tv_loanable_amount);

        tvResultBasicSalary = view.findViewById(R.id.tv_result_basic_salary);
        tvResultLoanAmount = view.findViewById(R.id.tv_result_loan_amount);
        tvResultMonths = view.findViewById(R.id.tv_result_months);
        tvResultInterestRate = view.findViewById(R.id.tv_result_interest_rate);
        tvResultInterest = view.findViewById(R.id.tv_result_interest);
        tvResultServiceCharge = view.findViewById(R.id.tv_result_service_charge);
        tvResultTakeHome = view.findViewById(R.id.tv_result_take_home);
        tvResultMonthly = view.findViewById(R.id.tv_result_monthly);

        // Disable inputs if has pending loan
        if (hasPendingLoan) {
            etMonths.setEnabled(false);
            btnCalculate.setEnabled(false);
            btnApply.setEnabled(false);

            showMessage("Pending Application",
                    "You already have a pending Regular Loan application.\n\n" +
                            "You cannot apply for another Regular Loan until your current application is reviewed by the administrator.");
        }
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPendingLoan) {
                    showMessage("Pending Application",
                            "You already have a pending Regular Loan application.\n\n" +
                                    "You cannot apply for another Regular Loan until your current application is reviewed by the administrator.");
                    return;
                }
                calculateLoan();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPendingLoan) {
                    showMessage("Pending Application",
                            "You already have a pending Regular Loan application.\n\n" +
                                    "You cannot apply for another Regular Loan until your current application is reviewed by the administrator.");
                    return;
                }
                applyForLoan();
            }
        });
    }

    private void calculateLoan() {
        try {
            // Get basic salary from database
            boolean found = databaseHelper.GetUserDetails(currentEmployeeId);
            if (!found) {
                showMessage("Error", "Unable to retrieve your salary information. Please contact administrator.");
                hideResultsCard();
                return;
            }

            double basicSalary = databaseHelper.getBasicSalary();
            if (basicSalary <= 0) {
                showMessage("Salary Not Set", "Your basic salary is not set or is 0. Please contact administrator to update your salary information.");
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

            int months = Integer.parseInt(monthsStr);

            // Do calculation
            LoanComputation.RegularLoanResult result = LoanComputation.calculateRegularLoan(
                    basicSalary,
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
            showMessage("Invalid Input", "Please enter valid number of months");
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

    private void updateResultsUI(LoanComputation.RegularLoanResult result) {
        // Update all result TextViews with formatted values
        tvResultBasicSalary.setText(LoanComputation.formatCurrency(result.basicSalary));
        tvResultLoanAmount.setText(LoanComputation.formatCurrency(result.loanAmount));
        tvResultMonths.setText(String.valueOf(result.months));
        tvResultInterestRate.setText(LoanComputation.formatPercent(result.interestRate));
        tvResultInterest.setText(LoanComputation.formatCurrency(result.interest));
        tvResultServiceCharge.setText(LoanComputation.formatCurrency(result.serviceCharge));
        tvResultTakeHome.setText(LoanComputation.formatCurrency(result.takeHomeLoan));
        tvResultMonthly.setText(LoanComputation.formatCurrency(result.monthlyPayment));
    }

    private void applyForLoan() {
        if (cardResults.getVisibility() != View.VISIBLE) {
            showMessage("Calculation Required", "Please calculate the loan first before applying");
            return;
        }

        // Get basic salary from database
        boolean found = databaseHelper.GetUserDetails(currentEmployeeId);
        if (!found) {
            showMessage("Error", "Unable to retrieve your salary information.");
            return;
        }

        double basicSalary = databaseHelper.getBasicSalary();
        int months = 0;

        try {
            String monthsStr = etMonths.getText().toString().trim();
            if (!monthsStr.isEmpty()) {
                months = Integer.parseInt(monthsStr);
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid Data", "Please recalculate the loan before applying");
            return;
        }

        // Calculate loan details again for database
        LoanComputation.RegularLoanResult result = LoanComputation.calculateRegularLoan(
                basicSalary,
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

        // Show confirmation dialog
        showConfirmationDialog(basicSalary, result);
    }

    private void showConfirmationDialog(double basicSalary, LoanComputation.RegularLoanResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm Loan Application");
        builder.setMessage(String.format("Are you sure you want to apply for this Regular Loan?\n\n" +
                        "Basic Salary: %s\n" +
                        "Loan Amount: %s\n" +
                        "Duration: %d months\n" +
                        "Interest Rate: %s\n" +
                        "Interest: %s\n" +
                        "Service Charge: %s\n" +
                        "Take Home Loan: %s\n" +
                        "Monthly Payment: %s\n\n" +
                        "This application will be submitted for review.",
                LoanComputation.formatCurrency(basicSalary),
                LoanComputation.formatCurrency(result.loanAmount),
                result.months,
                LoanComputation.formatPercent(result.interestRate),
                LoanComputation.formatCurrency(result.interest),
                LoanComputation.formatCurrency(result.serviceCharge),
                LoanComputation.formatCurrency(result.takeHomeLoan),
                LoanComputation.formatCurrency(result.monthlyPayment)));

        builder.setPositiveButton("Yes, Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Save loan application to database
                boolean isSaved = databaseHelper.SaveLoanApplication(
                        currentEmployeeId,
                        "Regular Loan",
                        result.loanAmount,
                        result.months,
                        result.interestRate,
                        result.serviceCharge,
                        result.takeHomeLoan,
                        result.monthlyPayment,
                        "Pending"
                );

                if (isSaved) {
                    showMessage("Application Submitted",
                            "Your Regular Loan application has been submitted successfully!\n\n" +
                                    "Status: Pending Review\n" +
                                    "You will be notified once your application is reviewed by the administrator.");

                    resetForm();
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
            }
        });

        builder.show();
    }

    private void disableForm() {
        etMonths.setEnabled(false);
        btnCalculate.setEnabled(false);
        btnApply.setEnabled(false);
    }

    private void resetForm() {
        etMonths.setText("");

        cardResults.setVisibility(View.GONE);

        tvResultBasicSalary.setText("₱0.00");
        tvResultLoanAmount.setText("₱0.00");
        tvResultMonths.setText("0");
        tvResultInterestRate.setText("0.00%");
        tvResultInterest.setText("₱0.00");
        tvResultServiceCharge.setText("₱0.00");
        tvResultTakeHome.setText("₱0.00");
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