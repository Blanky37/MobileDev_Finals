package com.example.aquino_bembo_finals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    public static final String DATABASE_NAME = "ABCCreditLoanSystem.db";
    public static final int DATABASE_VERSION = 1;


    // User Table
    public static final String TABLE_USERS = "user_table";
    public static final String COL_USER_ID = "ID";
    public static final String COL_EMPLOYEE_ID = "EmployeeID";
    public static final String COL_EMPLOYEE_NAME = "EmployeeName";
    public static final String COL_DATE_HIRED = "DateHired";
    public static final String COL_PASSWORD = "Password";
    public static final String COL_IS_ADMIN = "IsAdmin";


    // Loan Applications Table
    public static final String TABLE_LOANS = "loan_table";
    public static final String COL_LOAN_ID = "LoanID";
    public static final String COL_LOAN_TYPE = "LoanType";
    public static final String COL_LOAN_AMOUNT = "LoanAmount";
    public static final String COL_MONTHS_TO_PAY = "MonthsToPay";
    public static final String COL_INTEREST_RATE = "InterestRate";
    public static final String COL_SERVICE_CHARGE = "ServiceCharge";
    public static final String COL_TOTAL_AMOUNT = "TotalAmount";
    public static final String COL_MONTHLY_AMORTIZATION = "MonthlyAmortization";
    public static final String COL_LOAN_STATUS = "LoanStatus";
    public static final String COL_APPLICATION_DATE = "ApplicationDate";

    // Properties for current record
    private String employeeID;
    private String employeeName;
    private String dateHired;
    private String password;
    private int isAdmin;

    private String loanType;
    private double loanAmount;
    private int monthsToPay;
    private double interestRate;
    private double serviceCharge;
    private double totalAmount;
    private double monthlyAmortization;
    private String loanStatus;
    private String applicationDate;


    // Database Definition Methods
    private String UserTableDefinition()
    {
        String dbDefinition;
        dbDefinition = "CREATE TABLE " + TABLE_USERS + "(" +
                COL_USER_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                COL_EMPLOYEE_ID + " Text UNIQUE, " +
                COL_EMPLOYEE_NAME + " Text, " +
                COL_DATE_HIRED + " Text, " +
                COL_PASSWORD + " Text, " +
                COL_IS_ADMIN + " Integer)";
        return dbDefinition;
    }

    private String LoanTableDefinition()
    {
        String dbDefinition;
        dbDefinition = "CREATE TABLE " + TABLE_LOANS + "(" +
                COL_LOAN_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                COL_EMPLOYEE_ID + " Text, " +
                COL_LOAN_TYPE + " Text, " +
                COL_LOAN_AMOUNT + " Real, " +
                COL_MONTHS_TO_PAY + " Integer, " +
                COL_INTEREST_RATE + " Real, " +
                COL_SERVICE_CHARGE + " Real, " +
                COL_TOTAL_AMOUNT + " Real, " +
                COL_MONTHLY_AMORTIZATION + " Real, " +
                COL_LOAN_STATUS + " Text, " +
                COL_APPLICATION_DATE + " Text, " +
                "FOREIGN KEY(" + COL_EMPLOYEE_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_EMPLOYEE_ID + "))";
        return dbDefinition;
    }

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserTableDefinition());
        sqLiteDatabase.execSQL(LoanTableDefinition());

        // Create default admin account
        CreateDefaultAdmin(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }

    // Create default admin account
    private void CreateDefaultAdmin(SQLiteDatabase db)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMPLOYEE_ID, "ADMIN001");
        contentValues.put(COL_EMPLOYEE_NAME, "System Administrator");
        contentValues.put(COL_DATE_HIRED, "2025-01-01");
        contentValues.put(COL_PASSWORD, "admin123");
        contentValues.put(COL_IS_ADMIN, 1);
        db.insert(TABLE_USERS, null, contentValues);
    }

    // User Registration
    public boolean RegisterUser(String empID, String empName, String dateHired, String password, int isAdmin)
    {
        SQLiteDatabase saveCmd = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMPLOYEE_ID, empID);
        contentValues.put(COL_EMPLOYEE_NAME, empName);
        contentValues.put(COL_DATE_HIRED, dateHired);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_IS_ADMIN, isAdmin);
        long result = saveCmd.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    // User Login
    public boolean UserLogin(String empID, String password)
    {
        boolean found = false;
        SQLiteDatabase readCmd = this.getReadableDatabase();
        Cursor resultSet;
        resultSet = readCmd.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                        COL_EMPLOYEE_ID + " = ? AND " + COL_PASSWORD + " = ?",
                new String[] {empID, password});
        while (resultSet.moveToNext())
        {
            employeeID = resultSet.getString(1);
            employeeName = resultSet.getString(2);
            dateHired = resultSet.getString(3);
            this.password = resultSet.getString(4);
            isAdmin = resultSet.getInt(5);
            found = true;
            break;
        }
        resultSet.close();
        return found;
    }

    // Check if Employee ID exists
    public boolean IsEmployeeIDExist(String empID)
    {
        boolean found = false;
        SQLiteDatabase readCmd = this.getReadableDatabase();
        Cursor resultSet;
        resultSet = readCmd.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMPLOYEE_ID + " = ?",
                new String[] {empID});
        while (resultSet.moveToNext())
        {
            found = true;
            break;
        }
        resultSet.close();
        return found;
    }

    // Save Loan Application
    public boolean SaveLoanApplication(String empID, String loanType, double loanAmount,
                                       int monthsToPay, double interestRate, double serviceCharge,
                                       double totalAmount, double monthlyAmort, String status)
    {
        SQLiteDatabase saveCmd = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMPLOYEE_ID, empID);
        contentValues.put(COL_LOAN_TYPE, loanType);
        contentValues.put(COL_LOAN_AMOUNT, loanAmount);
        contentValues.put(COL_MONTHS_TO_PAY, monthsToPay);
        contentValues.put(COL_INTEREST_RATE, interestRate);
        contentValues.put(COL_SERVICE_CHARGE, serviceCharge);
        contentValues.put(COL_TOTAL_AMOUNT, totalAmount);
        contentValues.put(COL_MONTHLY_AMORTIZATION, monthlyAmort);
        contentValues.put(COL_LOAN_STATUS, status);
        contentValues.put(COL_APPLICATION_DATE, String.valueOf(System.currentTimeMillis()));

        long result = saveCmd.insert(TABLE_LOANS, null, contentValues);
        return result != -1;
    }

    // View User(Specific user) Loan Applications
    public Cursor ViewUserLoans(String empID)
    {
        SQLiteDatabase viewCmd = this.getWritableDatabase();
        Cursor resultSet;
        resultSet = viewCmd.rawQuery("SELECT " + COL_LOAN_TYPE + ", " + COL_LOAN_AMOUNT + ", " +
                COL_MONTHS_TO_PAY + ", " + COL_LOAN_STATUS + ", " +
                COL_APPLICATION_DATE + " FROM " + TABLE_LOANS +
                " WHERE " + COL_EMPLOYEE_ID + " = ?", new String[] {empID});
        return resultSet;
    }

    // View All Loan Applications
    public Cursor ViewAllLoans()
    {
        SQLiteDatabase viewCmd = this.getWritableDatabase();
        Cursor resultSet;
        resultSet = viewCmd.rawQuery("SELECT " + COL_LOAN_ID + ", " + COL_EMPLOYEE_ID + ", " +
                COL_LOAN_TYPE + ", " + COL_LOAN_AMOUNT + ", " +
                COL_MONTHS_TO_PAY + ", " + COL_LOAN_STATUS + ", " +
                COL_APPLICATION_DATE + " FROM " + TABLE_LOANS, null);
        return resultSet;
    }

    // Search Loan Application by ID
    public boolean SearchLoanByID(int loanID)
    {
        boolean found = false;
        SQLiteDatabase searchCmd = this.getReadableDatabase();
        Cursor cursorSet;
        cursorSet = searchCmd.rawQuery("SELECT * FROM " + TABLE_LOANS + " WHERE " + COL_LOAN_ID + " = ?",
                new String[] {String.valueOf(loanID)});
        while (cursorSet.moveToNext()){
            employeeID = cursorSet.getString(1);
            loanType = cursorSet.getString(2);
            loanAmount = cursorSet.getDouble(3);
            monthsToPay = cursorSet.getInt(4);
            interestRate = cursorSet.getDouble(5);
            serviceCharge = cursorSet.getDouble(6);
            totalAmount = cursorSet.getDouble(7);
            monthlyAmortization = cursorSet.getDouble(8);
            loanStatus = cursorSet.getString(9);
            applicationDate = cursorSet.getString(10);
            found = true;
            break;
        }
        cursorSet.close();
        return found;
    }

    // Update Loan Status (Approve/Disapprove)
    public boolean UpdateLoanStatus(int loanID, String status)
    {
        SQLiteDatabase updateCmd = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LOAN_STATUS, status);
        int result = updateCmd.update(TABLE_LOANS, contentValues, COL_LOAN_ID + " = ?",
                new String[] {String.valueOf(loanID)});
        return result > 0;
    }

    // Delete Loan Application
    public int DeleteLoanApplication(int loanID)
    {
        SQLiteDatabase deleteCmd = this.getWritableDatabase();
        int deletedRecord;
        deletedRecord = deleteCmd.delete(TABLE_LOANS, COL_LOAN_ID + " = ?",
                new String[]{String.valueOf(loanID)});
        return deletedRecord;
    }


    // Getter Methods
    public String getEmployeeID() {
        return employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDateHired() {
        return dateHired;
    }

    public String getPassword() {
        return password;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public String getLoanType() {
        return loanType;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public int getMonthsToPay() {
        return monthsToPay;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getMonthlyAmortization() {
        return monthlyAmortization;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    // Setter Methods
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setDateHired(String dateHired) {
        this.dateHired = dateHired;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }
}