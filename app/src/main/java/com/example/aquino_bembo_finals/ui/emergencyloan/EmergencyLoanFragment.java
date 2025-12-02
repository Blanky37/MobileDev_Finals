package com.example.aquino_bembo_finals.ui.emergencyloan;

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
public class EmergencyLoanFragment extends Fragment {

    private TextInputEditText etLoanAmount;
    private android.widget.RadioGroup rgPaymentOption;
    private MaterialButton btnCalculate, btnApply;
    private View cardResults;
    private DatabaseHelper databaseHelper;
    private String currentEmployeeId;
    private boolean hasPendingLoan = false;

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

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Get current employee ID from MainActivity
        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            currentEmployeeId = mainActivity.getCurrentEmployeeId();
        }

        checkPendingLoans();

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

                if (loanType.equals("Emergency Loan") && loanStatus.equals("Pending")) {
                    hasPendingLoan = true;
                    break;
                }
            }
            cursor.close();
        }
    }

    private void initializeViews(View view) {
        etLoanAmount = view.findViewById(R.id.et_loan_amount);
        rgPaymentOption = view.findViewById(R.id.rg_payment_option);

        btnCalculate = view.findViewById(R.id.btn_calculate);
        btnApply = view.findViewById(R.id.btn_apply);

        cardResults = view.findViewById(R.id.card_results);
        tvResultLoanAmount = view.findViewById(R.id.tv_result_loan_amount);
        tvResultServiceCharge = view.findViewById(R.id.tv_result_service_charge);
        tvResultInterest = view.findViewById(R.id.tv_result_interest);
        tvResultTotal = view.findViewById(R.id.tv_result_total);
        tvResultMonthly = view.findViewById(R.id.tv_result_monthly);

        layoutInterest = view.findViewById(R.id.layout_interest);
        layoutMonthly = view.findViewById(R.id.layout_monthly);

        if (hasPendingLoan) {
            etLoanAmount.setEnabled(false);
            rgPaymentOption.setEnabled(false);
            btnCalculate.setEnabled(false);
            btnApply.setEnabled(false);
            showMessage("Pending Application",
                    "You already have a pending Emergency Loan application.\n\n" +
                            "You cannot apply for another Emergency Loan until your current application is reviewed by the administrator.");
        }
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPendingLoan) {
                    showMessage("Pending Application",
                            "You already have a pending Emergency Loan application.\n\n" +
                                    "You cannot apply for another Emergency Loan until your current application is reviewed by the administrator.");
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
                            "You already have a pending Emergency Loan application.\n\n" +
                                    "You cannot apply for another Emergency Loan until your current application is reviewed by the administrator.");
                    return;
                }
                applyForLoan();
            }
        });
    }

    private void calculateLoan() {
        try {
            String amountStr = etLoanAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                showMessage("Input Required", "Please enter loan amount");
                hideResultsCard(); // Hide results if previously shown
                return;
            }

            double loanAmount = Double.parseDouble(amountStr);

            int selectedId = rgPaymentOption.getCheckedRadioButtonId();
            boolean isCashPayment = (selectedId == R.id.rb_cash);

            // Do calculation
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

            updateResultsUI(result);

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
            layoutInterest.setVisibility(View.GONE);
            layoutMonthly.setVisibility(View.GONE);
            tvResultMonthly.setText("N/A");
        } else {
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

        // Calculate loan details again for database
        LoanComputation.EmergencyLoanResult result = LoanComputation.calculateEmergencyLoan(
                loanAmount,
                isCashPayment,
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

        showConfirmationDialog(loanAmount, paymentOption, result);
    }

    private void showConfirmationDialog(double loanAmount, String paymentOption, LoanComputation.EmergencyLoanResult result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm Loan Application");
        builder.setMessage(String.format("Are you sure you want to apply for this Emergency Loan?\n\n" +
                        "Loan Amount: %s\n" +
                        "Payment Option: %s\n" +
                        "Service Charge: %s\n" +
                        "Interest: %s\n" +
                        "Total Payment: %s\n\n" +
                        "This application will be submitted for review.",
                LoanComputation.formatCurrency(loanAmount),
                paymentOption,
                LoanComputation.formatCurrency(result.serviceCharge),
                LoanComputation.formatCurrency(result.interest),
                LoanComputation.formatCurrency(result.totalPayment)));

        builder.setPositiveButton("Yes, Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean isSaved = databaseHelper.SaveLoanApplication(
                        currentEmployeeId,
                        "Emergency Loan",
                        result.loanAmount,
                        result.months,
                        result.isCashPayment ? 0.00 : 0.006,
                        result.serviceCharge,
                        result.totalPayment,
                        result.monthlyPayment,
                        "Pending"
                );

                if (isSaved) {
                    showMessage("Application Submitted",
                            "Your Emergency Loan application has been submitted successfully!\n\n" +
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
        rgPaymentOption.setEnabled(false);
        btnCalculate.setEnabled(false);
        btnApply.setEnabled(false);
    }

    private void resetForm() {
        etLoanAmount.setText("");

        rgPaymentOption.check(R.id.rb_installment);

        cardResults.setVisibility(View.GONE);

        tvResultLoanAmount.setText("₱0.00");
        tvResultServiceCharge.setText("₱0.00");
        tvResultInterest.setText("₱0.00");
        tvResultTotal.setText("₱0.00");
        tvResultMonthly.setText("₱0.00");

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}