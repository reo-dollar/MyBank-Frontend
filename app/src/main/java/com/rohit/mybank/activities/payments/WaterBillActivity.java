package com.rohit.mybank.activities.payments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.activities.pin.VerifyTransactionPinActivity;
import com.rohit.mybank.callback.PaymentCallback;
import com.rohit.mybank.databinding.ActivityWaterBillBinding;
import com.rohit.mybank.model.water.WaterBillRequest;
import com.rohit.mybank.model.water.WaterBillResponse;
import com.rohit.mybank.repository.WaterBillRepository;
import com.rohit.mybank.utils.PaymentSecurityHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaterBillActivity extends AppCompatActivity {

    //=====================================================
    // View Binding
    //=====================================================

    private ActivityWaterBillBinding binding;

    //=====================================================
    // Repository
    //=====================================================

    private WaterBillRepository repository;

    //=====================================================
    // Payment Data
    //=====================================================

    private String consumerNumber;

    private String board;

    private String state;

    private BigDecimal amount;

    //=====================================================
    // State -> Board Mapping
    //=====================================================

    private final Map<String, String[]> boardMap =
            new HashMap<>();

    //=====================================================
    // Transaction PIN Launcher
    //=====================================================

    private final ActivityResultLauncher<Intent>
            pinLauncher =

            registerForActivityResult(

                    new ActivityResultContracts.StartActivityForResult(),

                    result -> {

                        if (result.getResultCode()
                                != RESULT_OK) {

                            Toast.makeText(
                                    this,
                                    "Payment cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        if (result.getData() == null) {

                            return;

                        }

                        boolean verified =
                                result.getData().getBooleanExtra(

                                        VerifyTransactionPinActivity
                                                .EXTRA_PIN_VERIFIED,

                                        false

                                );

                        if (verified) {

                            performPayment();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Transaction PIN verification failed.",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

            );

    //=====================================================
    // onCreate
    //=====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityWaterBillBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(binding.getRoot());

        repository =
                new WaterBillRepository(this);

        setupStates();

        setupBoardMapping();

        setupStateListener();

        setupSummaryWatcher();

        updateSummary();

        binding.btnPayBill.setOnClickListener(

                v -> validateInput()

        );

        getOnBackPressedDispatcher().addCallback(

                this,

                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        finish();

                    }

                }

        );

    }
    // =====================================================
// Update Payment Summary
// =====================================================

    private void updateSummary() {

        String consumer = "";

        if (binding.etConsumerNumber.getText() != null) {

            consumer =
                    binding.etConsumerNumber
                            .getText()
                            .toString()
                            .trim();

        }

        String selectedBoard =
                binding.actBoard
                        .getText()
                        .toString()
                        .trim();

        String selectedState =
                binding.actState
                        .getText()
                        .toString()
                        .trim();

        String amountText = "";

        if (binding.etAmount.getText() != null) {

            amountText =
                    binding.etAmount
                            .getText()
                            .toString()
                            .trim();

        }

        binding.tvConsumer.setText(

                consumer.isEmpty()
                        ? "-"
                        : consumer

        );

        binding.tvBoard.setText(

                selectedBoard.isEmpty()
                        ? "-"
                        : selectedBoard

        );

        binding.tvState.setText(

                selectedState.isEmpty()
                        ? "-"
                        : selectedState

        );

        binding.tvAmount.setText(

                amountText.isEmpty()
                        ? "₹0.00"
                        : "₹" + amountText

        );

    }

// =====================================================
// Validate Input
// =====================================================

    private void validateInput() {

        binding.layoutConsumerNumber.setError(null);
        binding.layoutBoard.setError(null);
        binding.layoutState.setError(null);
        binding.layoutAmount.setError(null);

        consumerNumber = "";

        if (binding.etConsumerNumber.getText() != null) {

            consumerNumber =
                    binding.etConsumerNumber
                            .getText()
                            .toString()
                            .trim();

        }

        board =
                binding.actBoard
                        .getText()
                        .toString()
                        .trim();

        state =
                binding.actState
                        .getText()
                        .toString()
                        .trim();

        String amountString = "";

        if (binding.etAmount.getText() != null) {

            amountString =
                    binding.etAmount
                            .getText()
                            .toString()
                            .trim();

        }

        // Consumer Number

        if (TextUtils.isEmpty(consumerNumber)) {

            binding.layoutConsumerNumber
                    .setError("Enter Consumer Number");

            binding.etConsumerNumber.requestFocus();

            return;

        }

        if (consumerNumber.length() < 8) {

            binding.layoutConsumerNumber
                    .setError("Invalid Consumer Number");

            binding.etConsumerNumber.requestFocus();

            return;

        }

        // State

        if (TextUtils.isEmpty(state)) {

            binding.layoutState
                    .setError("Select State");

            binding.actState.requestFocus();

            return;

        }

        // Board

        if (TextUtils.isEmpty(board)) {

            binding.layoutBoard
                    .setError("Select Water Board");

            binding.actBoard.requestFocus();

            return;

        }

        // Amount

        if (TextUtils.isEmpty(amountString)) {

            binding.layoutAmount
                    .setError("Enter Bill Amount");

            binding.etAmount.requestFocus();

            return;

        }

        try {

            amount = new BigDecimal(amountString);

        } catch (Exception e) {

            binding.layoutAmount
                    .setError("Invalid Amount");

            binding.etAmount.requestFocus();

            return;

        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            binding.layoutAmount
                    .setError("Amount must be greater than zero");

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

                "Consumer Number : " + consumerNumber +

                        "\n\nState : " + state +

                        "\n\nWater Board : " + board +

                        "\n\nBill Amount : ₹" + amount +

                        "\n\nProceed with payment?";

        new AlertDialog.Builder(this)

                .setTitle("Confirm Water Bill Payment")

                .setMessage(message)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Continue",
                        (dialog, which) -> {

                            new PaymentSecurityHelper(

                                    WaterBillActivity.this,

                                    pinLauncher,

                                    new PaymentCallback() {

                                        @Override
                                        public void onSuccess() {

                                            performPayment();

                                        }

                                    }

                            ).verifyPayment();

                        }

                )

                .show();

    }
    // =====================================================
// Perform Water Bill Payment
// =====================================================

    private void performPayment() {

        binding.btnPayBill.setEnabled(false);

        WaterBillRequest request =
                new WaterBillRequest();

        request.setConsumerNumber(
                consumerNumber
        );

        request.setBoard(
                board
        );

        request.setState(
                state
        );

        request.setAmount(
                amount
        );

        repository
                .payWaterBill(request)

                .enqueue(

                        new Callback<WaterBillResponse>() {

                            @Override
                            public void onResponse(

                                    Call<WaterBillResponse> call,

                                    Response<WaterBillResponse> response) {

                                binding.btnPayBill.setEnabled(true);

                                if (!response.isSuccessful()) {

                                    Toast.makeText(

                                            WaterBillActivity.this,

                                            "HTTP Error : "
                                                    + response.code(),

                                            Toast.LENGTH_LONG

                                    ).show();

                                    return;

                                }

                                if (response.body() == null) {

                                    Toast.makeText(

                                            WaterBillActivity.this,

                                            "Empty server response.",

                                            Toast.LENGTH_LONG

                                    ).show();

                                    return;

                                }

                                WaterBillResponse billResponse =
                                        response.body();

                                if (billResponse.isSuccess()) {

                                    showSuccessDialog(
                                            billResponse
                                    );

                                } else {

                                    Toast.makeText(

                                            WaterBillActivity.this,

                                            billResponse.getMessage(),

                                            Toast.LENGTH_LONG

                                    ).show();

                                }

                            }

                            @Override
                            public void onFailure(

                                    Call<WaterBillResponse> call,

                                    Throwable t) {

                                binding.btnPayBill.setEnabled(true);

                                Toast.makeText(

                                        WaterBillActivity.this,

                                        "Network Error\n"
                                                + t.getMessage(),

                                        Toast.LENGTH_LONG

                                ).show();

                            }

                        }

                );

    }
    // =====================================================
// Payment Success Dialog
// =====================================================

    private void showSuccessDialog(
            WaterBillResponse response
    ) {

        String paymentId = "";

        if (response.getPaymentId() != null) {

            paymentId = response.getPaymentId();

        }

        String message =

                "✅ Water Bill Paid Successfully"

                        + "\n\nConsumer Number : "
                        + consumerNumber

                        + "\n\nWater Board : "
                        + board

                        + "\n\nState : "
                        + state

                        + "\n\nAmount Paid : ₹"
                        + amount

                        + "\n\nPayment ID : "
                        + paymentId

                        + "\n\nStatus : SUCCESS";

        new AlertDialog.Builder(this)

                .setTitle("Payment Successful")

                .setMessage(message)

                .setCancelable(false)

                .setPositiveButton(

                        "Done",

                        (dialog, which) -> {

                            setResult(RESULT_OK);

                            finish();

                        }

                )

                .show();

    }

// =====================================================
// Clear Errors
// =====================================================

    private void clearErrors() {

        binding.layoutConsumerNumber.setError(null);

        binding.layoutBoard.setError(null);

        binding.layoutState.setError(null);

        binding.layoutAmount.setError(null);

    }

// =====================================================
// Toolbar Back
// =====================================================

    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;

    }
    // =====================================================
// Setup States
// =====================================================

    private void setupStates() {

        String[] states = {

                "Andhra Pradesh",
                "Assam",
                "Bihar",
                "Chhattisgarh",
                "Delhi",
                "Goa",
                "Gujarat",
                "Haryana",
                "Himachal Pradesh",
                "Jharkhand",
                "Karnataka",
                "Kerala",
                "Madhya Pradesh",
                "Maharashtra",
                "Odisha",
                "Punjab",
                "Rajasthan",
                "Tamil Nadu",
                "Telangana",
                "Uttar Pradesh",
                "West Bengal"

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
// Setup Water Board Mapping
// =====================================================

    private void setupBoardMapping() {

        boardMap.put(

                "Maharashtra",

                new String[]{

                        "Maharashtra Jeevan Pradhikaran",
                        "Pune Water Supply",
                        "Nagpur Water Works",
                        "Nashik Municipal Water"

                }

        );

        boardMap.put(

                "Delhi",

                new String[]{

                        "Delhi Jal Board"

                }

        );

        boardMap.put(

                "Gujarat",

                new String[]{

                        "Ahmedabad Water Supply",
                        "Surat Municipal Water",
                        "GWSSB"

                }

        );

        boardMap.put(

                "Karnataka",

                new String[]{

                        "BWSSB",
                        "Mangalore Water Supply"

                }

        );

        boardMap.put(

                "Tamil Nadu",

                new String[]{

                        "Chennai Metro Water",
                        "TWAD Board"

                }

        );

        boardMap.put(

                "Telangana",

                new String[]{

                        "Hyderabad Metropolitan Water"

                }

        );

        boardMap.put(

                "Uttar Pradesh",

                new String[]{

                        "Jal Nigam",
                        "Noida Water Supply"

                }

        );

        boardMap.put(

                "West Bengal",

                new String[]{

                        "Kolkata Water Supply"

                }

        );

        boardMap.put(

                "Madhya Pradesh",

                new String[]{

                        "Bhopal Water Supply",
                        "Indore Water Supply"

                }

        );

        boardMap.put(

                "Rajasthan",

                new String[]{

                        "PHED Rajasthan"

                }

        );

    }
    // =====================================================
// Setup State Listener
// =====================================================

    private void setupStateListener() {

        binding.actState.setOnItemClickListener(

                (parent, view, position, id) -> {

                    String selectedState =
                            binding.actState
                                    .getText()
                                    .toString()
                                    .trim();

                    String[] boards =
                            boardMap.get(selectedState);

                    if (boards == null) {

                        boards = new String[0];

                    }

                    ArrayAdapter<String> boardAdapter =
                            new ArrayAdapter<>(

                                    this,

                                    android.R.layout.simple_dropdown_item_1line,

                                    boards

                            );

                    binding.actBoard.setAdapter(boardAdapter);

                    binding.actBoard.setText("", false);

                    updateSummary();

                }

        );

        binding.actBoard.setOnItemClickListener(

                (parent, view, position, id) ->

                        updateSummary()

        );

    }
    // =====================================================
// Setup Summary Watcher
// =====================================================

    private void setupSummaryWatcher() {

        binding.etConsumerNumber.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s,
                                                  int start,
                                                  int count,
                                                  int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s,
                                              int start,
                                              int before,
                                              int count) {

                        updateSummary();

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

        binding.etAmount.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s,
                                                  int start,
                                                  int count,
                                                  int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s,
                                              int start,
                                              int before,
                                              int count) {

                        updateSummary();

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                });

    }
}