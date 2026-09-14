package com.rohit.mybank.activities.kyc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
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

    // True when opened from ReviewActivity for editing
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        initializeViews();

        // Check whether this screen was opened for editing
        editMode = getIntent().getBooleanExtra("edit_mode", false);

        /*
         * NORMAL MODE:
         * Create a completely new KycRequest.
         *
         * EDIT MODE:
         * Get the existing KycRequest from ReviewActivity.
         */
        if (editMode) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                request = getIntent().getSerializableExtra(
                        "kyc",
                        KycRequest.class
                );

            } else {

                request = (KycRequest) getIntent()
                        .getSerializableExtra("kyc");
            }

            if (request == null) {

                Toast.makeText(
                        this,
                        "KYC data not found",
                        Toast.LENGTH_LONG
                ).show();

                finish();
                return;
            }

        } else {

            request = new KycRequest();
        }

        setupGenderSpinner();

        setupDatePicker();

        // Populate existing values when editing
        if (editMode) {
            populateExistingData();
        }

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
                                                String.format(
                                                        "%02d",
                                                        m + 1
                                                ) +
                                                "-" +
                                                String.format(
                                                        "%02d",
                                                        d
                                                );

                                etDob.setText(date);
                            },
                            year,
                            month,
                            day
                    );

            // Do not allow selecting a future date
            dialog.getDatePicker().setMaxDate(
                    System.currentTimeMillis()
            );

            dialog.show();
        });
    }

    /**
     * Populate previously saved personal details
     * when the user opens this screen from ReviewActivity.
     */
    private void populateExistingData() {

        if (request.getFirstName() != null) {
            etFirstName.setText(request.getFirstName());
        }

        if (request.getMiddleName() != null) {
            etMiddleName.setText(request.getMiddleName());
        }

        if (request.getLastName() != null) {
            etLastName.setText(request.getLastName());
        }

        if (request.getDateOfBirth() != null) {
            etDob.setText(request.getDateOfBirth());
        }

        if (request.getGender() != null) {

            String existingGender = request.getGender();

            ArrayAdapter adapter =
                    (ArrayAdapter) spGender.getAdapter();

            int position = adapter.getPosition(existingGender);

            if (position >= 0) {
                spGender.setSelection(position);
            }
        }
    }

    private void validateAndContinue() {

        String firstName =
                etFirstName.getText()
                        .toString()
                        .trim();

        String middleName =
                etMiddleName.getText()
                        .toString()
                        .trim();

        String lastName =
                etLastName.getText()
                        .toString()
                        .trim();

        String dob =
                etDob.getText()
                        .toString()
                        .trim();

        String gender = "";

        if (spGender.getSelectedItem() != null) {
            gender = spGender
                    .getSelectedItem()
                    .toString()
                    .trim();
        }

        // First name validation
        if (firstName.isEmpty()) {

            etFirstName.setError("Required");
            etFirstName.requestFocus();
            return;
        }

        // Last name validation
        if (lastName.isEmpty()) {

            etLastName.setError("Required");
            etLastName.requestFocus();
            return;
        }

        // Date of birth validation
        if (dob.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select Date of Birth",
                    Toast.LENGTH_SHORT
            ).show();

            etDob.requestFocus();
            return;
        }

        // Gender validation
        if (gender.equals("Select Gender")
                || gender.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select Gender",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * Update the SAME KycRequest object.
         */
        request.setFirstName(firstName);
        request.setMiddleName(middleName);
        request.setLastName(lastName);
        request.setDateOfBirth(dob);
        request.setGender(gender);

        /*
         * EDIT MODE
         *
         * Return the updated request directly to ReviewActivity.
         */
        if (editMode) {

            Intent resultIntent = new Intent();

            resultIntent.putExtra(
                    "kyc",
                    request
            );

            setResult(
                    RESULT_OK,
                    resultIntent
            );

            finish();

            return;
        }

        /*
         * NORMAL REGISTRATION FLOW
         *
         * Personal Details → Contact Details
         */
        Intent intent =
                new Intent(
                        PersonalDetailsActivity.this,
                        ContactDetailsActivity.class
                );

        intent.putExtra(
                "kyc",
                request
        );

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        /*
         * When editing from ReviewActivity,
         * simply return to ReviewActivity.
         */
        if (editMode) {

            finish();
            return;
        }

        super.onBackPressed();
    }
}