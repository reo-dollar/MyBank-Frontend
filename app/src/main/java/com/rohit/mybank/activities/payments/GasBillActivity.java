package com.rohit.mybank.activities.payments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.databinding.ActivityGasBillBinding;
import com.rohit.mybank.model.gas.GasBillRequest;
import com.rohit.mybank.model.gas.GasBillResponse;
import com.rohit.mybank.repository.GasBillRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GasBillActivity extends AppCompatActivity {

    private ActivityGasBillBinding binding;

    private GasBillRepository repository;

    private String consumerNumber;

    private String provider;

    private String state;

    private BigDecimal amount;

    private final Map<String, String[]> providerMap =
            new HashMap<>();

    // =====================================================
    // Transaction PIN Result
    // =====================================================

    private final ActivityResultLauncher<Intent> pinLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() != RESULT_OK) {

                            Toast.makeText(
                                    this,
                                    "Authentication cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        if (result.getData() == null) {

                            Toast.makeText(
                                    this,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        boolean verified =
                                result.getData().getBooleanExtra(
                                        VerifyTransactionPinActivity.EXTRA_PIN_VERIFIED,
                                        false
                                );

                        if (verified) {

                            bookCylinder();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction PIN verification failed.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    });

    // =====================================================
    // onCreate
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityGasBillBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        repository = new GasBillRepository(this);

        setupStates();

        setupProviderMapping();

        setupStateListener();

        setupSummaryWatcher();

        binding.btnBookCylinder.setOnClickListener(
                v -> validateInput()
        );

        updateSummary();

    }

    // =====================================================
    // States
    // =====================================================

    private void setupStates() {

        String[] states = {

                "Maharashtra",
                "Delhi",
                "Karnataka",
                "Tamil Nadu",
                "Gujarat",
                "Telangana"

        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(

                        this,

                        android.R.layout.simple_dropdown_item_1line,

                        states

                );

        binding.actState.setAdapter(adapter);

    }

    // =====================================================
    // Provider Mapping
    // =====================================================

    private void setupProviderMapping() {

        providerMap.put(
                "Maharashtra",
                new String[]{
                        "HP Gas",
                        "Bharat Gas",
                        "Indane Gas"
                }
        );

        providerMap.put(
                "Delhi",
                new String[]{
                        "Indane Gas",
                        "Bharat Gas"
                }
        );

        providerMap.put(
                "Karnataka",
                new String[]{
                        "HP Gas",
                        "Indane Gas"
                }
        );

        providerMap.put(
                "Tamil Nadu",
                new String[]{
                        "Indane Gas",
                        "HP Gas"
                }
        );

        providerMap.put(
                "Gujarat",
                new String[]{
                        "Bharat Gas",
                        "HP Gas"
                }
        );

        providerMap.put(
                "Telangana",
                new String[]{
                        "HP Gas",
                        "Indane Gas"
                }
        );

    }

    // =====================================================
    // State Listener
    // =====================================================

    private void setupStateListener() {

        binding.actState.setOnItemClickListener(

                (parent, view, position, id) -> {

                    String selectedState =
                            binding.actState
                                    .getText()
                                    .toString();

                    String[] providers =
                            providerMap.get(selectedState);

                    if (providers != null) {

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(

                                        this,

                                        android.R.layout.simple_dropdown_item_1line,

                                        providers

                                );

                        binding.actProvider.setAdapter(adapter);

                        binding.actProvider.setText(
                                "",
                                false
                        );

                    }

                    updateSummary();

                }

        );

        binding.actProvider.setOnItemClickListener(

                (parent, view, position, id) ->
                        updateSummary()

        );

    }
    // =====================================================
    // Live Summary
    // =====================================================

    private void setupSummaryWatcher() {

        TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                updateSummary();

            }

            @Override
            public void afterTextChanged(
                    Editable s
            ) {
            }

        };

        binding.etConsumerNumber.addTextChangedListener(watcher);
        binding.etAmount.addTextChangedListener(watcher);

    }

    // =====================================================
    // Update Summary
    // =====================================================

    private void updateSummary() {

        String consumer =
                binding.etConsumerNumber
                        .getText()
                        .toString()
                        .trim();

        String provider =
                binding.actProvider
                        .getText()
                        .toString()
                        .trim();

        String state =
                binding.actState
                        .getText()
                        .toString()
                        .trim();

        String amount =
                binding.etAmount
                        .getText()
                        .toString()
                        .trim();

        binding.tvConsumer.setText(
                consumer.isEmpty() ? "-" : consumer
        );

        binding.tvProvider.setText(
                provider.isEmpty() ? "-" : provider
        );

        binding.tvState.setText(
                state.isEmpty() ? "-" : state
        );

        binding.tvAmount.setText(
                amount.isEmpty()
                        ? "₹0"
                        : "₹" + amount
        );

    }

    // =====================================================
    // Validate Input
    // =====================================================

    private void validateInput() {

        consumerNumber =
                binding.etConsumerNumber
                        .getText()
                        .toString()
                        .trim();

        provider =
                binding.actProvider
                        .getText()
                        .toString()
                        .trim();

        state =
                binding.actState
                        .getText()
                        .toString()
                        .trim();

        String amountText =
                binding.etAmount
                        .getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(consumerNumber)) {

            binding.etConsumerNumber
                    .setError("Enter Consumer Number");

            binding.etConsumerNumber.requestFocus();

            return;

        }

        if (consumerNumber.length() < 8) {

            binding.etConsumerNumber
                    .setError("Invalid Consumer Number");

            binding.etConsumerNumber.requestFocus();

            return;

        }

        if (TextUtils.isEmpty(state)) {

            binding.actState.setError("Select State");

            binding.actState.requestFocus();

            return;

        }

        if (TextUtils.isEmpty(provider)) {

            binding.actProvider.setError("Select Gas Provider");

            binding.actProvider.requestFocus();

            return;

        }

        if (TextUtils.isEmpty(amountText)) {

            binding.etAmount.setError("Enter Amount");

            binding.etAmount.requestFocus();

            return;

        }

        try {

            amount = new BigDecimal(amountText);

        } catch (Exception e) {

            binding.etAmount.setError("Invalid Amount");

            binding.etAmount.requestFocus();

            return;

        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            binding.etAmount.setError(
                    "Amount must be greater than 0"
            );

            binding.etAmount.requestFocus();

            return;

        }

        if (amount.compareTo(
                new BigDecimal("10000")) > 0) {

            binding.etAmount.setError(
                    "Maximum booking amount is ₹10000"
            );

            binding.etAmount.requestFocus();

            return;

        }

        showConfirmationDialog();

    }

    // =====================================================
    // Confirmation Dialog
    // =====================================================

    private void showConfirmationDialog() {

        String message =
                "Consumer Number : "
                        + consumerNumber
                        + "\n\nProvider : "
                        + provider
                        + "\n\nState : "
                        + state
                        + "\n\nAmount : ₹"
                        + amount
                        + "\n\nDo you want to continue?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm Gas Cylinder Booking")

                .setMessage(message)

                .setCancelable(false)

                .setPositiveButton(
                        "Continue",
                        (dialog, which) -> {

                            dialog.dismiss();

                            new PaymentSecurityHelper(

                                    GasBillActivity.this,

                                    pinLauncher,

                                    new PaymentCallback() {

                                        @Override
                                        public void onSuccess() {

                                            bookCylinder();

                                        }

                                    }

                            ).verifyPayment();

                        }
                )

                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                dialog.dismiss()
                )

                .show();

    }
    // =====================================================
    // Book Gas Cylinder
    // =====================================================

    private void bookCylinder() {

        // Disable UI
        binding.btnBookCylinder.setEnabled(false);
        binding.btnBookCylinder.setText("Processing...");

        binding.etConsumerNumber.setEnabled(false);
        binding.actState.setEnabled(false);
        binding.actProvider.setEnabled(false);
        binding.etAmount.setEnabled(false);

        GasBillRequest request =
                new GasBillRequest(

                        consumerNumber,

                        provider,

                        state,

                        amount

                );

        repository.bookCylinder(request)

                .enqueue(new Callback<GasBillResponse>() {

                    @Override
                    public void onResponse(
                            Call<GasBillResponse> call,
                            Response<GasBillResponse> response) {

                        restoreViews();

                        if (!response.isSuccessful()) {

                            Toast.makeText(
                                    GasBillActivity.this,
                                    "Server Error : "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        if (response.body() == null) {

                            Toast.makeText(
                                    GasBillActivity.this,
                                    "Empty server response.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;

                        }

                        GasBillResponse billResponse =
                                response.body();

                        if (billResponse.isSuccess()) {

                            showSuccessDialog(

                                    billResponse.getPaymentId(),

                                    billResponse.getMessage()

                            );

                        } else {

                            Toast.makeText(

                                    GasBillActivity.this,

                                    billResponse.getMessage(),

                                    Toast.LENGTH_LONG

                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<GasBillResponse> call,
                            Throwable t) {

                        restoreViews();

                        Toast.makeText(

                                GasBillActivity.this,

                                "Network Error\n\n"
                                        + t.getMessage(),

                                Toast.LENGTH_LONG

                        ).show();

                    }

                });

    }

    // =====================================================
    // Restore Screen
    // =====================================================

    private void restoreViews() {

        binding.btnBookCylinder.setEnabled(true);

        binding.btnBookCylinder.setText(
                "Book Cylinder"
        );

        binding.etConsumerNumber.setEnabled(true);

        binding.actState.setEnabled(true);

        binding.actProvider.setEnabled(true);

        binding.etAmount.setEnabled(true);

    }
    // =====================================================
    // Success Dialog
    // =====================================================

    private void showSuccessDialog(
            String paymentId,
            String message
    ) {

        String successMessage =
                "Gas Cylinder Booking Successful\n\n"

                        + "Payment ID : "
                        + paymentId

                        + "\n\nConsumer Number : "
                        + consumerNumber

                        + "\n\nProvider : "
                        + provider

                        + "\n\nState : "
                        + state

                        + "\n\nAmount : ₹"
                        + amount

                        + "\n\nStatus : SUCCESS"

                        + "\n\n"
                        + message;

        new AlertDialog.Builder(this)

                .setTitle("Booking Successful")

                .setMessage(successMessage)

                .setCancelable(false)

                .setPositiveButton(
                        "Done",
                        (dialog, which) -> {

                            dialog.dismiss();

                            clearForm();

                            finish();

                        })

                .show();

    }

    // =====================================================
    // Clear Form
    // =====================================================

    private void clearForm() {

        binding.etConsumerNumber.setText("");

        binding.actState.setText(
                "",
                false
        );

        binding.actProvider.setText(
                "",
                false
        );

        binding.etAmount.setText("");

        consumerNumber = "";

        provider = "";

        state = "";

        amount = BigDecimal.ZERO;

        updateSummary();

    }

}