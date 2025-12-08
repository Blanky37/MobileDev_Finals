package com.example.aquino_bembo_finals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    public static final String DATABASE_NAME = "ABCCreditLoanSystem.db";
    public static final int DATABASE_VERSION = 2; // Every change/modification of database, change `database_version`

    // User Table
    public static final String TABLE_USERS = "user_table";
    public static final String COL_USER_ID = "ID";
    public static final String COL_EMPLOYEE_ID = "EmployeeID";
    public static final String COL_FIRST_NAME = "FirstName";
    public static final String COL_MIDDLE_INITIAL = "MiddleInitial";
    public static final String COL_LAST_NAME = "LastName";
    public static final String COL_DATE_HIRED = "DateHired";
    public static final String COL_BASIC_SALARY = "BasicSalary";
    public static final String COL_PASSWORD = "Password";
    public static final String COL_IS_ADMIN = "IsAdmin";

    // Loan Applications Table
    public static final String TABLE_LOANS = "loan_table";
    public static final String COL_LOAN_ID = "LoanID";
    public static final String COL_EMPLOYEE_ID_REF = "EmployeeID";
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
    private String firstName;
    private String middleInitial;
    private String lastName;
    private double basicSalary;
    private String dateHired;
    private String password;
    private int isAdmin;


    private String UserTableDefinition()
    {
        String dbDefinition;
        dbDefinition = "CREATE TABLE " + TABLE_USERS + "(" +
                COL_USER_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                COL_EMPLOYEE_ID + " Text UNIQUE, " +
                COL_FIRST_NAME + " Text, " +
                COL_MIDDLE_INITIAL + " Text, " +
                COL_LAST_NAME + " Text, " +
                COL_DATE_HIRED + " Text, " +
                COL_BASIC_SALARY + " Real, " +
                COL_PASSWORD + " Text, " +
                COL_IS_ADMIN + " Integer)";
        return dbDefinition;
    }

    private String LoanTableDefinition()
    {
        String dbDefinition;
        dbDefinition = "CREATE TABLE " + TABLE_LOANS + "(" +
                COL_LOAN_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                COL_EMPLOYEE_ID_REF + " Text, " +
                COL_LOAN_TYPE + " Text, " +
                COL_LOAN_AMOUNT + " Real, " +
                COL_MONTHS_TO_PAY + " Integer, " +
                COL_INTEREST_RATE + " Real, " +
                COL_SERVICE_CHARGE + " Real, " +
                COL_TOTAL_AMOUNT + " Real, " +
                COL_MONTHLY_AMORTIZATION + " Real, " +
                COL_LOAN_STATUS + " Text DEFAULT 'Pending', " +
                COL_APPLICATION_DATE + " Text, " +
                "FOREIGN KEY(" + COL_EMPLOYEE_ID_REF + ") REFERENCES " + TABLE_USERS + "(" + COL_EMPLOYEE_ID + "))";
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

    // Generate Employee ID: Initial + 5 digit random numbers
    public String GenerateEmployeeID(String firstName, String lastName) {
        String initial = "";
        if (!firstName.isEmpty()) initial += firstName.substring(0, 1).toUpperCase();
        if (!lastName.isEmpty()) initial += lastName.substring(0, 1).toUpperCase();

        Random random = new Random();
        int randomNumber = random.nextInt(90000) + 10000; // 10000-99999
        return initial + randomNumber;
    }

    // Create default admin account
    private void CreateDefaultAdmin(SQLiteDatabase db)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMPLOYEE_ID, "ADMIN001");
        contentValues.put(COL_FIRST_NAME, "System");
        contentValues.put(COL_MIDDLE_INITIAL, "A");
        contentValues.put(COL_LAST_NAME, "Administrator");
        contentValues.put(COL_DATE_HIRED, "01-01-2025");
        contentValues.put(COL_BASIC_SALARY, 50000.00);
        contentValues.put(COL_PASSWORD, "admin123");
        contentValues.put(COL_IS_ADMIN, 1);
        db.insert(TABLE_USERS, null, contentValues);
    }

    // User Registration
    public boolean RegisterUser(String empID, String firstName, String middleInitial, String lastName,
                                String dateHired, double basicSalary, String password, int isAdmin)
    {
        SQLiteDatabase saveCmd = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMPLOYEE_ID, empID);
        contentValues.put(COL_FIRST_NAME, firstName);
        contentValues.put(COL_MIDDLE_INITIAL, middleInitial);
        contentValues.put(COL_LAST_NAME, lastName);
        contentValues.put(COL_DATE_HIRED, dateHired);
        contentValues.put(COL_BASIC_SALARY, basicSalary);
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
            firstName = resultSet.getString(2);
            middleInitial = resultSet.getString(3);
            lastName = resultSet.getString(4);
            dateHired = resultSet.getString(5);
            basicSalary = resultSet.getDouble(6);
            this.password = resultSet.getString(7);
            isAdmin = resultSet.getInt(8);
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

        // Format application date as MM/dd/yyyy, American date format
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        contentValues.put(COL_APPLICATION_DATE, currentDate);

        long result = saveCmd.insert(TABLE_LOANS, null, contentValues);
        return result != -1;
    }

    // View User's Loan Applications
    public Cursor ViewUserLoans(String empID)
    {
        SQLiteDatabase viewCmd = this.getReadableDatabase();
        Cursor resultSet;
        resultSet = viewCmd.rawQuery("SELECT " + COL_LOAN_ID + ", " + COL_LOAN_TYPE + ", " + COL_LOAN_AMOUNT + ", " +
                COL_MONTHS_TO_PAY + ", " + COL_LOAN_STATUS + ", " +
                COL_APPLICATION_DATE + " FROM " + TABLE_LOANS +
                " WHERE " + COL_EMPLOYEE_ID + " = ? ORDER BY " + COL_LOAN_ID + " DESC", new String[] {empID});
        return resultSet;
    }

    // View All Loan Applications
    public Cursor ViewAllLoans()
    {
        SQLiteDatabase viewCmd = this.getReadableDatabase();
        Cursor resultSet;
        resultSet = viewCmd.rawQuery("SELECT " + COL_LOAN_ID + ", " + COL_EMPLOYEE_ID + ", " +
                COL_LOAN_TYPE + ", " + COL_LOAN_AMOUNT + ", " +
                COL_MONTHS_TO_PAY + ", " + COL_LOAN_STATUS + ", " +
                COL_APPLICATION_DATE + " FROM " + TABLE_LOANS +
                " ORDER BY " + COL_LOAN_ID + " DESC", null);
        return resultSet;
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

    // Get User Details by ID
    public boolean GetUserDetails(String empID)
    {
        boolean found = false;
        SQLiteDatabase readCmd = this.getReadableDatabase();
        Cursor resultSet;
        resultSet = readCmd.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMPLOYEE_ID + " = ?",
                new String[] {empID});
        while (resultSet.moveToNext())
        {
            employeeID = resultSet.getString(1);
            firstName = resultSet.getString(2);
            middleInitial = resultSet.getString(3);
            lastName = resultSet.getString(4);
            dateHired = resultSet.getString(5);
            basicSalary = resultSet.getDouble(6);
            password = resultSet.getString(7);
            isAdmin = resultSet.getInt(8);
            found = true;
            break;
        }
        resultSet.close();
        return found;
    }

    // Get all users (for AllRecordsView)
    public Cursor GetAllUsers() {
        SQLiteDatabase viewCmd = this.getReadableDatabase();
        Cursor resultSet;
        resultSet = viewCmd.rawQuery(
                "SELECT " + COL_EMPLOYEE_ID + ", " +
                        COL_FIRST_NAME + ", " +
                        COL_LAST_NAME + ", " +
                        COL_DATE_HIRED + ", " +
                        COL_BASIC_SALARY + ", " +
                        COL_IS_ADMIN +
                        " FROM " + TABLE_USERS +
                        " ORDER BY " + COL_EMPLOYEE_ID,
                null
        );
        return resultSet;
    }

    // Get all loans with all details
    public Cursor GetAllLoansDetailed() {
        SQLiteDatabase viewCmd = this.getReadableDatabase();
        Cursor resultSet;
        resultSet = viewCmd.rawQuery(
                "SELECT " + COL_LOAN_ID + ", " +
                        COL_EMPLOYEE_ID + ", " +
                        COL_LOAN_TYPE + ", " +
                        COL_LOAN_AMOUNT + ", " +
                        COL_MONTHS_TO_PAY + ", " +
                        COL_LOAN_STATUS + ", " +
                        COL_APPLICATION_DATE +
                        " FROM " + TABLE_LOANS +
                        " ORDER BY " + COL_LOAN_ID + " DESC",
                null
        );
        return resultSet;
    }

    // Getter Methods
    public String getEmployeeID() {
        return employeeID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (middleInitial != null && !middleInitial.isEmpty()) {
            return firstName + " " + middleInitial + ". " + lastName;
        } else {
            return firstName + " " + lastName;
        }
    }

    public String getDateHired() {
        return dateHired;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public int getIsAdmin() {
        return isAdmin;
    }
    
}