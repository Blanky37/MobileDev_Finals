package com.example.aquino_bembo_finals.ui.specialloan;

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
public class SpecialLoanFragment extends Fragment {

    private TextInputEditText etLoanAmount, etMonths;
    private MaterialButton btnCalculate, btnApply;
    private View cardResults;

    // Result TextViews
    private android.widget.TextView tvResultLoanAmount, tvResultMonths, tvResultInterestRate,
            tvResultInterest, tvResultTotal, tvResultMonthly;

    public SpecialLoanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_special_loan, container, false);

        // Initialize views
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        // Input fields
        etLoanAmount = view.findViewById(R.id.et_loan_amount);
        etMonths = view.findViewById(R.id.et_months);

        // Buttons
        btnCalculate = view.findViewById(R.id.btn_calculate);
        btnApply = view.findViewById(R.id.btn_apply);

        // Results card and TextViews
        cardResults = view.findViewById(R.id.card_results);
        tvResultLoanAmount = view.findViewById(R.id.tv_result_loan_amount);
        tvResultMonths = view.findViewById(R.id.tv_result_months);
        tvResultInterestRate = view.findViewById(R.id.tv_result_interest_rate);
        tvResultInterest = view.findViewById(R.id.tv_result_interest);
        tvResultTotal = view.findViewById(R.id.tv_result_total);
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

            // Update UI with results
            updateResultsUI(result);

            // Show results card
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

        // Show confirmation dialog
        showConfirmationDialog(loanAmount, months);
    }

    private void showConfirmationDialog(double loanAmount, int months) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm Loan Application");
        builder.setMessage(String.format("Are you sure you want to apply for this Special Loan?\n\n" +
                        "Loan Amount: %s\n" +
                        "Duration: %d months\n" +
                        "This application will be submitted for review.",
                LoanComputation.formatCurrency(loanAmount),
                months));

        builder.setPositiveButton("Yes, Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO: Add database submission logic here
                showMessage("Application Submitted",
                        "Your Special Loan application has been submitted successfully!\n\n" +
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
        etLoanAmount.setText("");
        etMonths.setText("");

        // Hide results card
        cardResults.setVisibility(View.GONE);

        // Reset results display
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
}