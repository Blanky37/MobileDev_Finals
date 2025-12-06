package com.example.aquino_bembo_finals.initial;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aquino_bembo_finals.DatabaseHelper;
import com.example.aquino_bembo_finals.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Regisration extends AppCompatActivity {

    DatabaseHelper myData = new DatabaseHelper(this);
    TextInputEditText txtFirstName, txtMiddleInitial, txtLastName, txtDateHired, txtBasicSalary, txtPassword, txtConfirmPassword;
    TextInputLayout tilFirstName, tilMiddleInitial, tilLastName, tilDateHired, tilBasicSalary, tilPassword, tilConfirmPassword;
    CheckBox cbTerms;
    TextView tvLoginLink;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regisration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtFirstName = (TextInputEditText) findViewById(R.id.et_first_name);
        txtMiddleInitial = (TextInputEditText) findViewById(R.id.et_middle_initial);
        txtLastName = (TextInputEditText) findViewById(R.id.et_last_name);
        txtDateHired = (TextInputEditText) findViewById(R.id.et_date_hired);
        txtBasicSalary = (TextInputEditText) findViewById(R.id.et_basic_salary);
        txtPassword = (TextInputEditText) findViewById(R.id.et_password);
        txtConfirmPassword = (TextInputEditText) findViewById(R.id.et_confirm_password);

        tilFirstName = (TextInputLayout) findViewById(R.id.til_first_name);
        tilMiddleInitial = (TextInputLayout) findViewById(R.id.til_middle_initial);
        tilLastName = (TextInputLayout) findViewById(R.id.til_last_name);
        tilDateHired = (TextInputLayout) findViewById(R.id.til_date_hired);
        tilBasicSalary = (TextInputLayout) findViewById(R.id.til_basic_salary);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.til_confirm_password);

        cbTerms = (CheckBox) findViewById(R.id.cb_terms);
        tvLoginLink = (TextView) findViewById(R.id.tv_login_link);
        calendar = Calendar.getInstance();
        setupDatePicker();

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Go back to login
            }
        });
    }

    // For date hired field
    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }
        };

        txtDateHired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Regisration.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    // Update date hired field
    private void updateDateLabel() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtDateHired.setText(sdf.format(calendar.getTime()));
    }

    public void myMessageWindow(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void onClickRegister(View view) {

        // Field Validation
        tilFirstName.setError(null);
        tilMiddleInitial.setError(null);
        tilLastName.setError(null);
        tilDateHired.setError(null);
        tilBasicSalary.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String firstName = txtFirstName.getText().toString().trim();
        String middleInitial = txtMiddleInitial.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();
        String dateHired = txtDateHired.getText().toString().trim();
        String basicSalaryStr = txtBasicSalary.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        // Validate fields
        if(firstName.isEmpty())
        {
            tilFirstName.setError("First name is required");
            txtFirstName.requestFocus();
            return;
        }

        if(lastName.isEmpty())
        {
            tilLastName.setError("Last name is required");
            txtLastName.requestFocus();
            return;
        }

        if(dateHired.isEmpty())
        {
            tilDateHired.setError("Date hired is required");
            txtDateHired.requestFocus();
            return;
        }

        if(basicSalaryStr.isEmpty())
        {
            tilBasicSalary.setError("Basic salary is required");
            txtBasicSalary.requestFocus();
            return;
        }

        double basicSalary = 0.0;
        try {
            basicSalary = Double.parseDouble(basicSalaryStr);
            if(basicSalary <= 0) {
                tilBasicSalary.setError("Basic salary must be greater than 0");
                txtBasicSalary.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            tilBasicSalary.setError("Please enter a valid salary amount");
            txtBasicSalary.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            tilPassword.setError("Password is required");
            txtPassword.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            tilPassword.setError("Password must be at least 6 characters");
            txtPassword.requestFocus();
            return;
        }

        if(confirmPassword.isEmpty())
        {
            tilConfirmPassword.setError("Please confirm your password");
            txtConfirmPassword.requestFocus();
            return;
        }

        if(!password.equals(confirmPassword))
        {
            tilConfirmPassword.setError("Passwords do not match");
            txtConfirmPassword.requestFocus();
            return;
        }

        if(!cbTerms.isChecked())
        {
            myMessageWindow("Terms and Conditions", "Please agree to the Terms and Conditions");
            return;
        }

        // Generate Employee ID
        String employeeID = myData.GenerateEmployeeID(firstName, lastName);

        // Check if employee ID already exists
        if(myData.IsEmployeeIDExist(employeeID))
        {
            myMessageWindow("Registration Failed", "Employee ID already exists. Please try again.");
            return;
        }

        // Register user; 0(false) for regular user, not admin
        boolean isRecordSaved = myData.RegisterUser(employeeID, firstName, middleInitial, lastName, dateHired, basicSalary, password, 0);

        if(isRecordSaved)
        {
            String successMessage = "Registration successful!\n\nYour Employee ID: " + employeeID + "\n\nPlease save this ID for login.";
            myMessageWindow("Registration Success", successMessage);

            txtFirstName.setText("");
            txtMiddleInitial.setText("");
            txtLastName.setText("");
            txtDateHired.setText("");
            txtBasicSalary.setText("");
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            cbTerms.setChecked(false);
        }
        else
        {
            myMessageWindow("Registration Failed", "Failed to create account. Please try again.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myData != null) {
            myData.close();
        }
    }
    public void onClickLoginLink(View view) {
        Intent intent = new Intent(Regisration.this, Login.class);
        startActivity(intent);
    }
}