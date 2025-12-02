package com.example.aquino_bembo_finals;

import java.text.DecimalFormat;

public class LoanComputation {
    private static final DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");
    private static final DecimalFormat percentFormat = new DecimalFormat("0.00%");
    private static final DecimalFormat numberFormat = new DecimalFormat("#,##0");

    // Interface for error handling callback
    public interface LoanErrorListener {
        void onLoanError(String title, String message);
    }

    // Emergency Loan Computation
    public static class EmergencyLoanResult {
        public double loanAmount;
        public double serviceCharge;
        public double interest;
        public double totalPayment;
        public double monthlyPayment;
        public int months;
        public boolean isCashPayment;

        public EmergencyLoanResult(double loanAmount, double serviceCharge, double interest,
                                   double totalPayment, double monthlyPayment, int months, boolean isCashPayment) {
            this.loanAmount = loanAmount;
            this.serviceCharge = serviceCharge;
            this.interest = interest;
            this.totalPayment = totalPayment;
            this.monthlyPayment = monthlyPayment;
            this.months = months;
            this.isCashPayment = isCashPayment;
        }
    }

    public static EmergencyLoanResult calculateEmergencyLoan(double loanAmount, boolean isCashPayment, LoanErrorListener errorListener) {
        // Validate loan amount range
        if (loanAmount < 5000) {
            if (errorListener != null) {
                errorListener.onLoanError("Minimum Amount Required",
                        "Emergency Loan minimum amount is ₱5,000.\n\n" +
                                "Please enter an amount of ₱5,000 or more.");
            }
            return null;
        }

        if (loanAmount > 25000) {
            if (errorListener != null) {
                errorListener.onLoanError("Maximum Amount Exceeded",
                        "Emergency Loan maximum amount is ₱25,000.\n\n" +
                                "Please enter an amount of ₱25,000 or less.");
            }
            return null;
        }

        double serviceCharge = loanAmount * 0.01;
        double interest = 0;
        double totalPayment;
        double monthlyPayment = 0;
        int months = 6;

        if (isCashPayment) {
            // Cash after 6 months: Loan Amount + Service Charge
            totalPayment = loanAmount + serviceCharge;
        } else {
            // Payable in 6 months: (Loan Amount + Service Charge + Interest) / 6
            interest = loanAmount * 0.006;
            totalPayment = loanAmount + serviceCharge + interest;
            monthlyPayment = totalPayment / months;
        }

        return new EmergencyLoanResult(loanAmount, serviceCharge, interest, totalPayment, monthlyPayment, months, isCashPayment);
    }

    // Special Loan Computation
    public static class SpecialLoanResult {
        public double loanAmount;
        public int months;
        public double interestRate;
        public double interest;
        public double totalAmount;
        public double monthlyPayment;

        public SpecialLoanResult(double loanAmount, int months, double interestRate,
                                 double interest, double totalAmount, double monthlyPayment) {
            this.loanAmount = loanAmount;
            this.months = months;
            this.interestRate = interestRate;
            this.interest = interest;
            this.totalAmount = totalAmount;
            this.monthlyPayment = monthlyPayment;
        }
    }

    public static SpecialLoanResult calculateSpecialLoan(double loanAmount, int months, LoanErrorListener errorListener) {
        // Validate loan amount range
        if (loanAmount < 50000) {
            if (errorListener != null) {
                errorListener.onLoanError("Minimum Amount Required",
                        "Special Loan minimum amount is ₱50,000.\n\n" +
                                "Please enter an amount of ₱50,000 or more.");
            }
            return null;
        }

        if (loanAmount > 100000) {
            if (errorListener != null) {
                errorListener.onLoanError("Maximum Amount Exceeded",
                        "Special Loan maximum amount is ₱100,000.\n\n" +
                                "Please enter an amount of ₱100,000 or less.");
            }
            return null;
        }

        // Validate months range
        if (months < 1) {
            if (errorListener != null) {
                errorListener.onLoanError("Invalid Duration",
                        "Special Loan must be paid in at least 1 month.\n\n" +
                                "Please enter a duration of 1 month or more.");
            }
            return null;
        }

        if (months > 18) {
            if (errorListener != null) {
                errorListener.onLoanError("Maximum Duration Exceeded",
                        "Special Loan maximum duration is 18 months.\n\n" +
                                "Please enter a duration of 18 months or less.");
            }
            return null;
        }

        double interestRate = getSpecialLoanInterestRate(months);

        double interest = loanAmount * months * interestRate;

        double totalAmount = loanAmount + interest;

        double monthlyPayment = totalAmount / months;

        return new SpecialLoanResult(loanAmount, months, interestRate, interest, totalAmount, monthlyPayment);
    }

    private static double getSpecialLoanInterestRate(int months) {
        if (months >= 1 && months <= 6) {
            return 0.006;
        } else if (months >= 7 && months <= 12) {
            return 0.0062;
        } else if (months >= 13 && months <= 18) {
            return 0.0065;
        } else {
            return 0.006;
        }
    }

    // Regular Loan Computation
    public static class RegularLoanResult {
        public double basicSalary;
        public double loanAmount;
        public int months;
        public double interestRate;
        public double interest;
        public double serviceCharge;
        public double takeHomeLoan;
        public double monthlyPayment;

        public RegularLoanResult(double basicSalary, double loanAmount, int months, double interestRate,
                                 double interest, double serviceCharge, double takeHomeLoan, double monthlyPayment) {
            this.basicSalary = basicSalary;
            this.loanAmount = loanAmount;
            this.months = months;
            this.interestRate = interestRate;
            this.interest = interest;
            this.serviceCharge = serviceCharge;
            this.takeHomeLoan = takeHomeLoan;
            this.monthlyPayment = monthlyPayment;
        }
    }

    public static RegularLoanResult calculateRegularLoan(double basicSalary, int months, LoanErrorListener errorListener) {
        // Validate basic salary
        if (basicSalary <= 0) {
            if (errorListener != null) {
                errorListener.onLoanError("Invalid Salary",
                        "Please enter a valid basic salary amount.\n\n" +
                                "Salary must be greater than ₱0.00.");
            }
            return null;
        }

        // Validate months range
        if (months < 1) {
            if (errorListener != null) {
                errorListener.onLoanError("Invalid Duration",
                        "Regular Loan must be paid in at least 1 month.\n\n" +
                                "Please enter a duration of 1 month or more.");
            }
            return null;
        }

        if (months > 24) {
            if (errorListener != null) {
                errorListener.onLoanError("Maximum Duration Exceeded",
                        "Regular Loan maximum duration is 24 months.\n\n" +
                                "Please enter a duration of 24 months or less.");
            }
            return null;
        }

        double loanAmount = basicSalary * 2.5;

        double interestRate = getRegularLoanInterestRate(months);

        double interest = loanAmount * months * interestRate;

        double serviceCharge = loanAmount * 0.02;

        double takeHomeLoan = loanAmount - (interest + serviceCharge);

        double monthlyPayment = takeHomeLoan / months;

        return new RegularLoanResult(basicSalary, loanAmount, months, interestRate, interest, serviceCharge, takeHomeLoan, monthlyPayment);
    }

    private static double getRegularLoanInterestRate(int months) {
        if (months >= 1 && months <= 5) {
            return 0.0062;
        } else if (months >= 6 && months <= 10) {
            return 0.0065;
        } else if (months >= 11 && months <= 15) {
            return 0.0068;
        } else if (months >= 16 && months <= 20) {
            return 0.0075;
        } else if (months >= 21 && months <= 24) {
            return 0.0080;
        } else {
            return 0.0062;
        }
    }

    // Formatting methods
    public static String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    public static String formatPercent(double rate) {
        return percentFormat.format(rate);
    }

    public static String formatNumber(int number) {
        return numberFormat.format(number);
    }
}