package com.rohit.mybank.activities.kyc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

import java.util.Calendar;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText etFirstName;
    private EditText etMiddleName;
    private EditText etLastName;
    private EditText etDob;

    private Spinner spGender;

    private Button btnNext;

    private KycRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        initializeViews();

        request = new KycRequest();

        setupGenderSpinner();

        setupDatePicker();

        btnNext.setOnClickListener(v -> validateAndContinue());

    }

    private void initializeViews() {

        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etDob = findViewById(R.id.etDob);

        spGender = findViewById(R.id.spGender);

        btnNext = findViewById(R.id.btnNext);

    }

    private void setupGenderSpinner() {

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.gender_array,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spGender.setAdapter(adapter);

    }

    private void setupDatePicker() {

        etDob.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            this,
                            (view, y, m, d) -> {

                                String date =
                                        y + "-" +
                                                String.format("%02d", m + 1) +
                                                "-" +
                                                String.format("%02d", d);

                                etDob.setText(date);

                            },
                            year,
                            month,
                            day
                    );

            dialog.show();

        });

    }

    private void validateAndContinue() {

        String firstName =
                etFirstName.getText().toString().trim();

        String middleName =
                etMiddleName.getText().toString().trim();

        String lastName =
                etLastName.getText().toString().trim();

        String dob =
                etDob.getText().toString().trim();

        String gender =
                spGender.getSelectedItem().toString();

        if (firstName.isEmpty()) {

            etFirstName.setError("Required");
            return;

        }

        if (lastName.isEmpty()) {

            etLastName.setError("Required");
            return;

        }

        if (dob.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select Date of Birth",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        if (gender.equals("Select Gender")) {

            Toast.makeText(
                    this,
                    "Select Gender",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        request.setFirstName(firstName);
        request.setMiddleName(middleName);
        request.setLastName(lastName);
        request.setDateOfBirth(dob);
        request.setGender(gender);

        Intent intent =
                new Intent(
                        this,
                        ContactDetailsActivity.class
                );

        intent.putExtra("kyc", request);

        startActivity(intent);

    }

}