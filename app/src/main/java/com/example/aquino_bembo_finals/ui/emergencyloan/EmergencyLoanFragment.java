package com.example.aquino_bembo_finals.ui.emergencyloan;

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
public class EmergencyLoanFragment extends Fragment {

    private TextInputEditText etLoanAmount;
    private android.widget.RadioGroup rgPaymentOption;
    private MaterialButton btnCalculate, btnApply;
    private View cardResults;

    // Result TextViews
    private android.widget.TextView tvResultLoanAmount, tvResultServiceCharge, tvResultInterest,
            tvResultTotal, tvResultMonthly;
    private android.widget.LinearLayout layoutInterest, layoutMonthly;

    public EmergencyLoanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_loan, container, false);

        // Initialize views
        initializeViews(view);

        // Set up click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        // Input fields
        etLoanAmount = view.findViewById(R.id.et_loan_amount);
        rgPaymentOption = view.findViewById(R.id.rg_payment_option);

        // Buttons
        btnCalculate = view.findViewById(R.id.btn_calculate);
        btnApply = view.findViewById(R.id.btn_apply);

        // Results card and TextViews
        cardResults = view.findViewById(R.id.card_results);
        tvResultLoanAmount = view.findViewById(R.id.tv_result_loan_amount);
        tvResultServiceCharge = view.findViewById(R.id.tv_result_service_charge);
        tvResultInterest = view.findViewById(R.id.tv_result_interest);
        tvResultTotal = view.findViewById(R.id.tv_result_total);
        tvResultMonthly = view.findViewById(R.id.tv_result_monthly);

        // Layouts
        layoutInterest = view.findViewById(R.id.layout_interest);
        layoutMonthly = view.findViewById(R.id.layout_monthly);
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
                hideResultsCard(); // Hide results if previously shown
                return;
            }

            double loanAmount = Double.parseDouble(amountStr);

            // Get payment option
            int selectedId = rgPaymentOption.getCheckedRadioButtonId();
            boolean isCashPayment = (selectedId == R.id.rb_cash);

            // Perform calculation
            LoanComputation.EmergencyLoanResult result = LoanComputation.calculateEmergencyLoan(
                    loanAmount,
                    isCashPayment,
                    new LoanComputation.LoanErrorListener() {
                        @Override
                        public void onLoanError(String title, String message) {
                            showMessage(title, message);
                            hideResultsCard(); // Hide results on error
                        }
                    }
            );

            if (result == null) {
                hideResultsCard(); // Hide results if calculation failed
                return;
            }

            // Update UI with results
            updateResultsUI(result);

            // Show results card
            cardResults.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            showMessage("Invalid Input", "Please enter a valid numeric loan amount");
            hideResultsCard(); // Hide results on error
        } catch (Exception e) {
            showMessage("Calculation Error", "An error occurred while calculating the loan. Please try again.");
            hideResultsCard(); // Hide results on error
        }
    }

    private void hideResultsCard() {
        if (cardResults.getVisibility() == View.VISIBLE) {
            cardResults.setVisibility(View.GONE);
        }
    }

    private void updateResultsUI(LoanComputation.EmergencyLoanResult result) {
        // Update all result TextViews with formatted values
        tvResultLoanAmount.setText(LoanComputation.formatCurrency(result.loanAmount));
        tvResultServiceCharge.setText(LoanComputation.formatCurrency(result.serviceCharge));
        tvResultInterest.setText(LoanComputation.formatCurrency(result.interest));
        tvResultTotal.setText(LoanComputation.formatCurrency(result.totalPayment));

        // Show/hide interest and monthly payment based on payment option
        if (result.isCashPayment) {
            // For cash payment, hide interest and monthly payment
            layoutInterest.setVisibility(View.GONE);
            layoutMonthly.setVisibility(View.GONE);
            tvResultMonthly.setText("N/A");
        } else {
            // For installment, show interest and monthly payment
            layoutInterest.setVisibility(View.VISIBLE);
            layoutMonthly.setVisibility(View.VISIBLE);
            tvResultMonthly.setText(LoanComputation.formatCurrency(result.monthlyPayment));
        }
    }

    private void applyForLoan() {
        if (cardResults.getVisibility() != View.VISIBLE) {
            showMessage("Calculation Required", "Please calculate the loan first before applying");
            return;
        }

        // Get the calculated values
        double loanAmount = 0;
        String amountStr = etLoanAmount.getText().toString().trim();
        if (!amountStr.isEmpty()) {
            try {
                loanAmount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                showMessage("Invalid Data", "Please recalculate the loan before applying");
                return;
            }
        }

        int selectedId = rgPaymentOption.getCheckedRadioButtonId();
        boolean isCashPayment = (selectedId == R.id.rb_cash);
        String paymentOption = isCashPayment ? "Cash after 6 months" : "6 months installment";

        // Show confirmation dialog
        showConfirmationDialog(loanAmount, paymentOption);
    }

    private void showConfirmationDialog(double loanAmount, String paymentOption) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm Loan Application");
        builder.setMessage(String.format("Are you sure you want to apply for this Emergency Loan?\n\n" +
                        "Loan Amount: %s\n" +
                        "Payment Option: %s\n\n" +
                        "This application will be submitted for review.",
                LoanComputation.formatCurrency(loanAmount),
                paymentOption));

        builder.setPositiveButton("Yes, Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO: Add database submission logic here
                showMessage("Application Submitted",
                        "Your Emergency Loan application has been submitted successfully!\n\n" +
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
        // Clear input field
        etLoanAmount.setText("");

        // Reset to default radio button (installment)
        rgPaymentOption.check(R.id.rb_installment);

        // Hide results card
        cardResults.setVisibility(View.GONE);

        // Reset results display
        tvResultLoanAmount.setText("₱0.00");
        tvResultServiceCharge.setText("₱0.00");
        tvResultInterest.setText("₱0.00");
        tvResultTotal.setText("₱0.00");
        tvResultMonthly.setText("₱0.00");

        // Show interest and monthly layouts
        layoutInterest.setVisibility(View.VISIBLE);
        layoutMonthly.setVisibility(View.VISIBLE);
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