package com.example.aquino_bembo_finals.ui.regularloan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aquino_bembo_finals.R;
import com.example.aquino_bembo_finals.LoanComputation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegularLoanFragment extends Fragment {

    private TextInputEditText etBasicSalary, etMonths;
    private MaterialButton btnCalculate, btnApply;
    private View cardResults, cardLoanableAmount;

    // Result TextViews
    private android.widget.TextView tvLoanableAmount, tvResultBasicSalary, tvResultLoanAmount,
            tvResultMonths, tvResultInterestRate, tvResultInterest, tvResultServiceCharge,
            tvResultTakeHome, tvResultMonthly;

    public RegularLoanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_regular_loan, container, false);

        // Initialize views
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        // Input fields
        etBasicSalary = view.findViewById(R.id.et_basic_salary);
        etMonths = view.findViewById(R.id.et_months);

        // Buttons
        btnCalculate = view.findViewById(R.id.btn_calculate);
        btnApply = view.findViewById(R.id.btn_apply);

        // Cards
        cardResults = view.findViewById(R.id.card_results);
        cardLoanableAmount = view.findViewById(R.id.card_loanable_amount);

        // Loanable amount TextView
        tvLoanableAmount = view.findViewById(R.id.tv_loanable_amount);

        // Result TextViews
        tvResultBasicSalary = view.findViewById(R.id.tv_result_basic_salary);
        tvResultLoanAmount = view.findViewById(R.id.tv_result_loan_amount);
        tvResultMonths = view.findViewById(R.id.tv_result_months);
        tvResultInterestRate = view.findViewById(R.id.tv_result_interest_rate);
        tvResultInterest = view.findViewById(R.id.tv_result_interest);
        tvResultServiceCharge = view.findViewById(R.id.tv_result_service_charge);
        tvResultTakeHome = view.findViewById(R.id.tv_result_take_home);
        tvResultMonthly = view.findViewById(R.id.tv_result_monthly);
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLoan();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyForLoan();
            }
        });
    }

    private void calculateLoan() {
        try {
            // Get basic salary from input
            String salaryStr = etBasicSalary.getText().toString().trim();
            if (salaryStr.isEmpty()) {
                showMessage("Input Required", "Please enter basic salary");
                hideResultsCard();
                cardLoanableAmount.setVisibility(View.GONE);
                return;
            }

            // Get months from input
            String monthsStr = etMonths.getText().toString().trim();
            if (monthsStr.isEmpty()) {
                showMessage("Input Required", "Please enter number of months");
                hideResultsCard();
                cardLoanableAmount.setVisibility(View.GONE);
                return;
            }

            double basicSalary = Double.parseDouble(salaryStr);
            int months = Integer.parseInt(monthsStr);

            // Calculate and show loanable amount (2.5x basic salary)
            double loanableAmount = basicSalary * 2.5;
            tvLoanableAmount.setText(LoanComputation.formatCurrency(loanableAmount));
            cardLoanableAmount.setVisibility(View.VISIBLE);

            // Perform full calculation
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

            // Update UI with results
            updateResultsUI(result);

            // Show results card
            cardResults.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            showMessage("Invalid Input", "Please enter valid numeric values");
            hideResultsCard();
            cardLoanableAmount.setVisibility(View.GONE);
        } catch (Exception e) {
            showMessage("Calculation Error", "An error occurred while calculating the loan. Please try again.");
            hideResultsCard();
            cardLoanableAmount.setVisibility(View.GONE);
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

        // Get the calculated values
        double basicSalary = 0;
        int months = 0;

        try {
            String salaryStr = etBasicSalary.getText().toString().trim();
            String monthsStr = etMonths.getText().toString().trim();

            if (!salaryStr.isEmpty()) {
                basicSalary = Double.parseDouble(salaryStr);
            }
            if (!monthsStr.isEmpty()) {
                months = Integer.parseInt(monthsStr);
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid Data", "Please recalculate the loan before applying");
            return;
        }

        // Calculate loan amount for confirmation
        double loanAmount = basicSalary * 2.5;

        // Show confirmation dialog
        showConfirmationDialog(basicSalary, loanAmount, months);
    }

    private void showConfirmationDialog(double basicSalary, double loanAmount, int months) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm Loan Application");
        builder.setMessage(String.format("Are you sure you want to apply for this Regular Loan?\n\n" +
                        "Basic Salary: %s\n" +
                        "Loan Amount: %s\n" +
                        "Duration: %d months\n\n" +
                        "This application will be submitted for review.",
                LoanComputation.formatCurrency(basicSalary),
                LoanComputation.formatCurrency(loanAmount),
                months));

        builder.setPositiveButton("Yes, Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO: Add database submission logic here
                showMessage("Application Submitted",
                        "Your Regular Loan application has been submitted successfully!\n\n" +
                                "Status: Pending Review\n" +
                                "You will be notified once your application is reviewed by the administrator.");

                // Reset form after submission
                resetForm();
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

    private void resetForm() {
        // Clear input fields
        etBasicSalary.setText("");
        etMonths.setText("");

        // Hide cards
        cardResults.setVisibility(View.GONE);
        cardLoanableAmount.setVisibility(View.GONE);

        // Reset results display
        tvLoanableAmount.setText("₱0.00");
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
                // Do nothing, just close the dialog
            }
        });
        builder.show();
    }
}