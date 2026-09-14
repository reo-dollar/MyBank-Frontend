package com.rohit.mybank.activities.qr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.model.qr.QrResolveResponse;
import com.rohit.mybank.api.RetrofitClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * =========================================================
 * QR SCANNER ACTIVITY
 * =========================================================
 *
 * MyBank internal QR Scan & Pay scanner.
 *
 * Flow:
 *
 * Dashboard
 *      ↓
 * Scan QR
 *      ↓
 * Camera Scanner
 *      ↓
 * Validate MyBank QR
 *      ↓
 * Show detected Payment ID
 *      ↓
 * Continue
 *      ↓
 * Resolve recipient from backend
 *      ↓
 * Recipient Confirmation
 *
 * Expected QR format:
 *
 * MYBANK://PAY/PAY_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 *
 * Payment ID format:
 *
 * PAY_ + 36 hexadecimal characters
 *
 * =========================================================
 */
@ExperimentalGetImage
public class QRScannerActivity extends AppCompatActivity {

    // =========================================================
    // CONSTANTS
    // =========================================================

    public static final String EXTRA_PAYMENT_ID =
            "extra_payment_id";

    public static final String EXTRA_QR_PAYLOAD =
            "extra_qr_payload";

    private static final String QR_PREFIX =
            "MYBANK://PAY/";

    private static final String PAYMENT_ID_REGEX =
            "^PAY_[A-Fa-f0-9]{36}$";


    // =========================================================
    // UI
    // =========================================================

    private PreviewView cameraPreview;

    private ImageButton btnBack;
    private ImageButton btnTorch;

    private LinearLayout btnGallery;
    private LinearLayout btnTorchBottom;

    private TextView tvTorchBottom;

    private FrameLayout processingOverlay;

    private LinearLayout resultPanel;

    private TextView tvPaymentId;

    private Button btnContinue;


    // =========================================================
    // CAMERA
    // =========================================================

    private ProcessCameraProvider cameraProvider;

    private Camera camera;

    private ExecutorService cameraExecutor;


    // =========================================================
    // BARCODE SCANNER
    // =========================================================

    private BarcodeScanner barcodeScanner;


    // =========================================================
    // API
    // =========================================================

    private ApiService apiService;


    // =========================================================
    // STATE
    // =========================================================

    private final AtomicBoolean isProcessing =
            new AtomicBoolean(false);

    private boolean torchEnabled = false;

    private String detectedPaymentId;

    private String detectedPayload;


    // =========================================================
    // CAMERA PERMISSION LAUNCHER
    // =========================================================

    private final ActivityResultLauncher<String>
            cameraPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {

                        if (granted) {

                            startCamera();

                        } else {

                            showCameraPermissionDenied();
                        }
                    }
            );


    // =========================================================
    // GALLERY LAUNCHER
    // =========================================================

    private final ActivityResultLauncher<String>
            galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        scanGalleryImage(uri);
                    }
            );


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);


        // -----------------------------------------------------
        // LAYOUT
        // -----------------------------------------------------

        setContentView(
                R.layout.activity_qr_scanner
        );


        // -----------------------------------------------------
        // STATUS BAR
        // -----------------------------------------------------

        getWindow().setStatusBarColor(
                Color.rgb(
                        21,
                        101,
                        192
                )
        );


        // -----------------------------------------------------
        // NAVIGATION BAR
        // -----------------------------------------------------

        getWindow().setNavigationBarColor(
                Color.BLACK
        );


        // -----------------------------------------------------
        // INITIALIZE VIEWS
        // -----------------------------------------------------

        initializeViews();


        // -----------------------------------------------------
        // INITIALIZE API
        // -----------------------------------------------------

        initializeApi();


        // -----------------------------------------------------
        // INITIALIZE SCANNER
        // -----------------------------------------------------

        initializeScanner();


        // -----------------------------------------------------
        // INITIALIZE EXECUTOR
        // -----------------------------------------------------

        initializeExecutor();


        // -----------------------------------------------------
        // LISTENERS
        // -----------------------------------------------------

        setupListeners();


        // -----------------------------------------------------
        // CAMERA PERMISSION
        // -----------------------------------------------------

        checkCameraPermission();
    }


    // =========================================================
    // INITIALIZE API
    // =========================================================

    private void initializeApi() {

        apiService =
                RetrofitClient
                        .getClient(this)
                        .create(ApiService.class);
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        cameraPreview =
                findViewById(
                        R.id.cameraPreview
                );


        btnBack =
                findViewById(
                        R.id.btnBack
                );


        btnTorch =
                findViewById(
                        R.id.btnTorch
                );


        btnGallery =
                findViewById(
                        R.id.btnGallery
                );


        btnTorchBottom =
                findViewById(
                        R.id.btnTorchBottom
                );


        tvTorchBottom =
                findViewById(
                        R.id.tvTorchBottom
                );


        processingOverlay =
                findViewById(
                        R.id.processingOverlay
                );


        resultPanel =
                findViewById(
                        R.id.resultPanel
                );


        tvPaymentId =
                findViewById(
                        R.id.tvPaymentId
                );


        btnContinue =
                findViewById(
                        R.id.btnContinue
                );


        // -----------------------------------------------------
        // INITIAL VISIBILITY
        // -----------------------------------------------------

        if (processingOverlay != null) {

            processingOverlay.setVisibility(
                    View.GONE
            );
        }


        if (resultPanel != null) {

            resultPanel.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // INITIALIZE BARCODE SCANNER
    // =========================================================

    private void initializeScanner() {

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE
                        )
                        .build();


        barcodeScanner =
                BarcodeScanning.getClient(
                        options
                );
    }


    // =========================================================
    // INITIALIZE EXECUTOR
    // =========================================================

    private void initializeExecutor() {

        cameraExecutor =
                Executors.newSingleThreadExecutor();
    }


    // =========================================================
    // SETUP LISTENERS
    // =========================================================

    private void setupListeners() {

        // -----------------------------------------------------
        // BACK
        // -----------------------------------------------------

        if (btnBack != null) {

            btnBack.setOnClickListener(
                    view -> finish()
            );
        }


        // -----------------------------------------------------
        // GALLERY
        // -----------------------------------------------------

        if (btnGallery != null) {

            btnGallery.setOnClickListener(
                    view -> openGallery()
            );
        }


        // -----------------------------------------------------
        // TOP TORCH
        // -----------------------------------------------------

        if (btnTorch != null) {

            btnTorch.setOnClickListener(
                    view -> toggleTorch()
            );
        }


        // -----------------------------------------------------
        // BOTTOM TORCH
        // -----------------------------------------------------

        if (btnTorchBottom != null) {

            btnTorchBottom.setOnClickListener(
                    view -> toggleTorch()
            );
        }


        // -----------------------------------------------------
        // CONTINUE
        // -----------------------------------------------------

        if (btnContinue != null) {

            btnContinue.setOnClickListener(
                    view -> resolveRecipient()
            );
        }
    }


    // =========================================================
    // RESOLVE RECIPIENT
    // =========================================================

    /**
     * Sends the scanned Payment ID to the backend.
     *
     * IMPORTANT:
     *
     * This does NOT transfer money.
     *
     * It only resolves the recipient.
     */
    private void resolveRecipient() {

        // -----------------------------------------------------
        // VALIDATE PAYMENT ID
        // -----------------------------------------------------

        if (
                detectedPaymentId == null
                        || detectedPaymentId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "No valid payment QR was scanned.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String paymentId =
                detectedPaymentId.trim();


        // -----------------------------------------------------
        // PREVENT DOUBLE CLICK
        // -----------------------------------------------------

        if (
                !isProcessing.compareAndSet(
                        false,
                        true
                )
        ) {

            return;
        }


        // -----------------------------------------------------
        // SHOW PROCESSING
        // -----------------------------------------------------

        showResolvingRecipient();


        // -----------------------------------------------------
        // API CALL
        // -----------------------------------------------------

        apiService
                .resolveQrRecipient(
                        paymentId
                )
                .enqueue(
                        new Callback<QrResolveResponse>() {

                            @Override
                            public void onResponse(
                                    @NonNull Call<QrResolveResponse> call,
                                    @NonNull Response<QrResolveResponse> response
                            ) {

                                if (isFinishing()) {
                                    return;
                                }


                                // -------------------------------------------------
                                // SUCCESS
                                // -------------------------------------------------

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                ) {

                                    QrResolveResponse recipient =
                                            response.body();


                                    if (
                                            recipient.isSuccess()
                                                    && recipient.getPaymentId() != null
                                                    && recipient.getCustomerName() != null
                                    ) {

                                        openRecipientConfirmation(
                                                recipient
                                        );

                                        return;
                                    }


                                    showResolveError(
                                            "Unable to verify this payment QR."
                                    );

                                    return;
                                }


                                // -------------------------------------------------
                                // SERVER ERROR
                                // -------------------------------------------------

                                showResolveError(
                                        "Unable to find this MyBank recipient."
                                );
                            }


                            @Override
                            public void onFailure(
                                    @NonNull Call<QrResolveResponse> call,
                                    @NonNull Throwable t
                            ) {

                                if (isFinishing()) {
                                    return;
                                }


                                showResolveError(
                                        "Network error. Please check your connection."
                                );
                            }
                        }
                );
    }


    // =========================================================
    // SHOW RESOLVING RECIPIENT
    // =========================================================

    private void showResolvingRecipient() {

        // -----------------------------------------------------
        // HIDE RESULT PANEL
        // -----------------------------------------------------

        if (resultPanel != null) {

            resultPanel.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // SHOW PROCESSING OVERLAY
        // -----------------------------------------------------

        if (processingOverlay != null) {

            processingOverlay.setVisibility(
                    View.VISIBLE
            );
        }


        // -----------------------------------------------------
        // DISABLE CONTINUE
        // -----------------------------------------------------

        if (btnContinue != null) {

            btnContinue.setEnabled(
                    false
            );
        }
    }


    // =========================================================
    // OPEN RECIPIENT CONFIRMATION
    // =========================================================

    private void openRecipientConfirmation(
            QrResolveResponse recipient
    ) {

        // -----------------------------------------------------
        // RESET PROCESSING
        // -----------------------------------------------------

        isProcessing.set(false);


        // -----------------------------------------------------
        // CREATE INTENT
        // -----------------------------------------------------

        Intent intent =
                new Intent(
                        QRScannerActivity.this,
                        QrRecipientActivity.class
                );


        // -----------------------------------------------------
        // PAYMENT ID
        // -----------------------------------------------------

        intent.putExtra(
                QrRecipientActivity.EXTRA_PAYMENT_ID,
                recipient.getPaymentId()
        );


        // -----------------------------------------------------
        // CUSTOMER NAME
        // -----------------------------------------------------

        intent.putExtra(
                QrRecipientActivity.EXTRA_CUSTOMER_NAME,
                recipient.getCustomerName()
        );


        // -----------------------------------------------------
        // MASKED ACCOUNT
        // -----------------------------------------------------

        intent.putExtra(
                QrRecipientActivity.EXTRA_MASKED_ACCOUNT,
                recipient.getMaskedAccountNumber()
        );


        // -----------------------------------------------------
        // ORIGINAL QR PAYLOAD
        // -----------------------------------------------------

        if (detectedPayload != null) {

            intent.putExtra(
                    QrRecipientActivity.EXTRA_QR_PAYLOAD,
                    detectedPayload
            );
        }


        // -----------------------------------------------------
        // OPEN CONFIRMATION SCREEN
        // -----------------------------------------------------

        startActivity(
                intent
        );


        // -----------------------------------------------------
        // CLOSE SCANNER
        // -----------------------------------------------------

        finish();
    }


    // =========================================================
    // SHOW RESOLVE ERROR
    // =========================================================

    private void showResolveError(
            String message
    ) {

        if (isFinishing()) {
            return;
        }


        isProcessing.set(false);


        // -----------------------------------------------------
        // HIDE PROCESSING
        // -----------------------------------------------------

        if (processingOverlay != null) {

            processingOverlay.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // SHOW RESULT AGAIN
        // -----------------------------------------------------

        if (resultPanel != null) {

            resultPanel.setVisibility(
                    View.VISIBLE
            );
        }


        // -----------------------------------------------------
        // ENABLE CONTINUE
        // -----------------------------------------------------

        if (btnContinue != null) {

            btnContinue.setEnabled(
                    true
            );
        }


        // -----------------------------------------------------
        // MESSAGE
        // -----------------------------------------------------

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // CHECK CAMERA PERMISSION
    // =========================================================

    private void checkCameraPermission() {

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_GRANTED
        ) {

            startCamera();

            return;
        }


        cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
        );
    }


    // =========================================================
    // START CAMERA
    // =========================================================

    private void startCamera() {

        if (isFinishing()) {
            return;
        }


        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                )
                        != PackageManager.PERMISSION_GRANTED
        ) {

            return;
        }


        ListenableFuture<ProcessCameraProvider>
                cameraProviderFuture =
                ProcessCameraProvider.getInstance(
                        this
                );


        cameraProviderFuture.addListener(
                () -> {

                    if (isFinishing()) {
                        return;
                    }


                    try {

                        cameraProvider =
                                cameraProviderFuture.get();


                        bindCameraUseCases();

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Unable to start camera.",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                },
                ContextCompat.getMainExecutor(
                        this
                )
        );
    }


    // =========================================================
    // BIND CAMERA USE CASES
    // =========================================================

    @ExperimentalGetImage
    private void bindCameraUseCases() {

        if (cameraProvider == null) {
            return;
        }


        if (cameraPreview == null) {
            return;
        }


        if (cameraExecutor == null) {
            return;
        }


        // -----------------------------------------------------
        // PREVIEW
        // -----------------------------------------------------

        Preview preview =
                new Preview.Builder()
                        .build();


        preview.setSurfaceProvider(
                cameraPreview.getSurfaceProvider()
        );


        // -----------------------------------------------------
        // IMAGE ANALYSIS
        // -----------------------------------------------------

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                                ImageAnalysis
                                        .STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build();


        imageAnalysis.setAnalyzer(
                cameraExecutor,
                this::analyzeImage
        );


        // -----------------------------------------------------
        // CAMERA SELECTOR
        // -----------------------------------------------------

        CameraSelector cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA;


        // -----------------------------------------------------
        // UNBIND PREVIOUS CAMERA
        // -----------------------------------------------------

        cameraProvider.unbindAll();


        // -----------------------------------------------------
        // BIND CAMERA
        // -----------------------------------------------------

        camera =
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );


        // -----------------------------------------------------
        // UPDATE TORCH
        // -----------------------------------------------------

        updateTorchUI();
    }


    // =========================================================
    // ANALYZE CAMERA IMAGE
    // =========================================================

    @ExperimentalGetImage
    private void analyzeImage(
            @NonNull ImageProxy imageProxy
    ) {

        // -----------------------------------------------------
        // IGNORE FRAME WHEN PROCESSING
        // -----------------------------------------------------

        if (
                isProcessing.get()
                        || barcodeScanner == null
        ) {

            imageProxy.close();

            return;
        }


        // -----------------------------------------------------
        // GET MEDIA IMAGE
        // -----------------------------------------------------

        android.media.Image mediaImage =
                imageProxy.getImage();


        if (mediaImage == null) {

            imageProxy.close();

            return;
        }


        // -----------------------------------------------------
        // CREATE ML KIT INPUT IMAGE
        // -----------------------------------------------------

        InputImage inputImage =
                InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy
                                .getImageInfo()
                                .getRotationDegrees()
                );


        // -----------------------------------------------------
        // PROCESS IMAGE
        // -----------------------------------------------------

        barcodeScanner
                .process(inputImage)

                .addOnSuccessListener(
                        barcodes ->
                                handleBarcodes(
                                        barcodes
                                )
                )

                .addOnFailureListener(
                        exception -> {
                            // Ignore individual frame errors.
                        }
                )

                .addOnCompleteListener(
                        task ->
                                imageProxy.close()
                );
    }


    // =========================================================
    // HANDLE CAMERA BARCODES
    // =========================================================

    private void handleBarcodes(
            List<Barcode> barcodes
    ) {

        // -----------------------------------------------------
        // IGNORE EMPTY RESULTS
        // -----------------------------------------------------

        if (
                barcodes == null
                        || barcodes.isEmpty()
                        || isProcessing.get()
        ) {

            return;
        }


        // -----------------------------------------------------
        // CHECK ALL DETECTED BARCODES
        // -----------------------------------------------------

        for (Barcode barcode : barcodes) {

            String rawValue =
                    barcode.getRawValue();


            if (rawValue == null) {
                continue;
            }


            rawValue =
                    rawValue.trim();


            // -------------------------------------------------
            // CHECK MYBANK QR PREFIX
            // -------------------------------------------------

            if (
                    !rawValue.startsWith(
                            QR_PREFIX
                    )
            ) {

                continue;
            }


            // -------------------------------------------------
            // EXTRACT PAYMENT ID
            // -------------------------------------------------

            String paymentId =
                    rawValue.substring(
                            QR_PREFIX.length()
                    ).trim();


            // -------------------------------------------------
            // VALIDATE PAYMENT ID
            // -------------------------------------------------

            if (
                    !paymentId.matches(
                            PAYMENT_ID_REGEX
                    )
            ) {

                runOnUiThread(
                        this::showInvalidQr
                );

                continue;
            }


            // -------------------------------------------------
            // VALID MYBANK QR FOUND
            // -------------------------------------------------

            if (
                    isProcessing.compareAndSet(
                            false,
                            true
                    )
            ) {

                detectedPaymentId =
                        paymentId;


                detectedPayload =
                        rawValue;


                runOnUiThread(
                        this::showCameraProcessing
                );
            }


            break;
        }
    }


    // =========================================================
    // SHOW CAMERA PROCESSING
    // =========================================================

    private void showCameraProcessing() {

        if (isFinishing()) {
            return;
        }


        // -----------------------------------------------------
        // STOP CAMERA
        // -----------------------------------------------------

        if (cameraProvider != null) {

            cameraProvider.unbindAll();
        }


        // -----------------------------------------------------
        // HIDE SCANNER CONTROLS
        // -----------------------------------------------------

        if (btnGallery != null) {

            btnGallery.setVisibility(
                    View.GONE
            );
        }


        if (btnTorchBottom != null) {

            btnTorchBottom.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // SHOW PROCESSING
        // -----------------------------------------------------

        if (processingOverlay != null) {

            processingOverlay.setVisibility(
                    View.VISIBLE
            );
        }


        // -----------------------------------------------------
        // SHOW RESULT
        // -----------------------------------------------------

        if (cameraPreview != null) {

            cameraPreview.postDelayed(
                    this::showDetectedResult,
                    700
            );
        }
    }


    // =========================================================
    // SHOW DETECTED RESULT
    // =========================================================

    private void showDetectedResult() {

        if (isFinishing()) {
            return;
        }


        // -----------------------------------------------------
        // HIDE PROCESSING
        // -----------------------------------------------------

        if (processingOverlay != null) {

            processingOverlay.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // SHOW RESULT PANEL
        // -----------------------------------------------------

        if (resultPanel != null) {

            resultPanel.setVisibility(
                    View.VISIBLE
            );
        }


        // -----------------------------------------------------
        // DISPLAY PAYMENT ID
        // -----------------------------------------------------

        if (
                tvPaymentId != null
                        && detectedPaymentId != null
        ) {

            tvPaymentId.setText(
                    "Payment ID\n"
                            + detectedPaymentId
            );
        }


        // -----------------------------------------------------
        // ENABLE CONTINUE
        // -----------------------------------------------------

        if (btnContinue != null) {

            btnContinue.setEnabled(
                    true
            );
        }


        // -----------------------------------------------------
        // READY FOR RECIPIENT RESOLUTION
        // -----------------------------------------------------

        // The QR has already been detected and the camera is
        // unbound, so release the processing lock here.
        // This is required because Continue calls
        // resolveRecipient(), which acquires the same lock.
        isProcessing.set(false);
    }


    // =========================================================
    // SHOW INVALID QR
    // =========================================================

    private void showInvalidQr() {

        if (isFinishing()) {
            return;
        }


        Toast.makeText(
                this,
                "This is not a valid MyBank payment QR.",
                Toast.LENGTH_SHORT
        ).show();
    }


    // =========================================================
    // OPEN GALLERY
    // =========================================================

    private void openGallery() {

        if (isProcessing.get()) {
            return;
        }


        galleryLauncher.launch(
                "image/*"
        );
    }


    // =========================================================
    // SCAN GALLERY IMAGE
    // =========================================================

    private void scanGalleryImage(
            Uri uri
    ) {

        if (isProcessing.get()) {
            return;
        }


        if (barcodeScanner == null) {

            Toast.makeText(
                    this,
                    "QR scanner is not ready.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        try {

            // -------------------------------------------------
            // CREATE INPUT IMAGE
            // -------------------------------------------------

            InputImage image =
                    InputImage.fromFilePath(
                            this,
                            uri
                    );


            // -------------------------------------------------
            // SET PROCESSING STATE
            // -------------------------------------------------

            isProcessing.set(
                    true
            );


            // -------------------------------------------------
            // STOP CAMERA
            // -------------------------------------------------

            if (cameraProvider != null) {

                cameraProvider.unbindAll();
            }


            // -------------------------------------------------
            // HIDE CONTROLS
            // -------------------------------------------------

            if (btnGallery != null) {

                btnGallery.setVisibility(
                        View.GONE
                );
            }


            if (btnTorchBottom != null) {

                btnTorchBottom.setVisibility(
                        View.GONE
                );
            }


            // -------------------------------------------------
            // SHOW PROCESSING
            // -------------------------------------------------

            if (processingOverlay != null) {

                processingOverlay.setVisibility(
                        View.VISIBLE
                );
            }


            // -------------------------------------------------
            // PROCESS IMAGE
            // -------------------------------------------------

            barcodeScanner
                    .process(image)

                    .addOnSuccessListener(
                            this::handleGalleryBarcodes
                    )

                    .addOnFailureListener(
                            exception ->
                                    resetAfterGalleryFailure(
                                            "Unable to read the QR image."
                                    )
                    );

        } catch (IOException e) {

            resetAfterGalleryFailure(
                    "Unable to open selected image."
            );
        }
    }


    // =========================================================
    // HANDLE GALLERY BARCODES
    // =========================================================

    private void handleGalleryBarcodes(
            List<Barcode> barcodes
    ) {

        if (isFinishing()) {
            return;
        }


        // -----------------------------------------------------
        // NO QR FOUND
        // -----------------------------------------------------

        if (
                barcodes == null
                        || barcodes.isEmpty()
        ) {

            resetAfterGalleryFailure(
                    "No QR code found in the image."
            );

            return;
        }


        // -----------------------------------------------------
        // SEARCH FOR VALID MYBANK QR
        // -----------------------------------------------------

        for (Barcode barcode : barcodes) {

            String rawValue =
                    barcode.getRawValue();


            if (rawValue == null) {
                continue;
            }


            rawValue =
                    rawValue.trim();


            // -------------------------------------------------
            // CHECK PREFIX
            // -------------------------------------------------

            if (
                    !rawValue.startsWith(
                            QR_PREFIX
                    )
            ) {

                continue;
            }


            // -------------------------------------------------
            // EXTRACT PAYMENT ID
            // -------------------------------------------------

            String paymentId =
                    rawValue.substring(
                            QR_PREFIX.length()
                    ).trim();


            // -------------------------------------------------
            // VALIDATE PAYMENT ID
            // -------------------------------------------------

            if (
                    paymentId.matches(
                            PAYMENT_ID_REGEX
                    )
            ) {

                detectedPaymentId =
                        paymentId;


                detectedPayload =
                        rawValue;


                // -------------------------------------------------
                // HIDE PROCESSING
                // -------------------------------------------------

                if (processingOverlay != null) {

                    processingOverlay.setVisibility(
                            View.GONE
                    );
                }


                // -------------------------------------------------
                // SHOW RESULT
                // -------------------------------------------------

                showDetectedResult();

                return;
            }
        }


        // -----------------------------------------------------
        // INVALID QR
        // -----------------------------------------------------

        resetAfterGalleryFailure(
                "This is not a valid MyBank payment QR."
        );
    }


    // =========================================================
    // RESET AFTER GALLERY FAILURE
    // =========================================================

    private void resetAfterGalleryFailure(
            String message
    ) {

        if (isFinishing()) {
            return;
        }


        // -----------------------------------------------------
        // RESET STATE
        // -----------------------------------------------------

        isProcessing.set(
                false
        );


        detectedPaymentId =
                null;


        detectedPayload =
                null;


        // -----------------------------------------------------
        // HIDE PROCESSING
        // -----------------------------------------------------

        if (processingOverlay != null) {

            processingOverlay.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // HIDE RESULT
        // -----------------------------------------------------

        if (resultPanel != null) {

            resultPanel.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // RESTORE CONTROLS
        // -----------------------------------------------------

        if (btnGallery != null) {

            btnGallery.setVisibility(
                    View.VISIBLE
            );
        }


        if (btnTorchBottom != null) {

            btnTorchBottom.setVisibility(
                    View.VISIBLE
            );
        }


        // -----------------------------------------------------
        // RESET TORCH
        // -----------------------------------------------------

        torchEnabled =
                false;


        updateTorchUI();


        // -----------------------------------------------------
        // MESSAGE
        // -----------------------------------------------------

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();


        // -----------------------------------------------------
        // RESTART CAMERA
        // -----------------------------------------------------

        restartCamera();
    }


    // =========================================================
    // RESTART CAMERA
    // =========================================================

    private void restartCamera() {

        if (isFinishing()) {
            return;
        }


        // -----------------------------------------------------
        // RESET PROCESSING
        // -----------------------------------------------------

        isProcessing.set(
                false
        );


        // -----------------------------------------------------
        // RESET RESULT
        // -----------------------------------------------------

        if (resultPanel != null) {

            resultPanel.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // RESET PROCESSING OVERLAY
        // -----------------------------------------------------

        if (processingOverlay != null) {

            processingOverlay.setVisibility(
                    View.GONE
            );
        }


        // -----------------------------------------------------
        // RESTORE CONTROLS
        // -----------------------------------------------------

        if (btnGallery != null) {

            btnGallery.setVisibility(
                    View.VISIBLE
            );
        }


        if (btnTorchBottom != null) {

            btnTorchBottom.setVisibility(
                    View.VISIBLE
            );
        }


        // -----------------------------------------------------
        // RESET TORCH
        // -----------------------------------------------------

        torchEnabled =
                false;


        updateTorchUI();


        // -----------------------------------------------------
        // START CAMERA
        // -----------------------------------------------------

        startCamera();
    }


    // =========================================================
    // TOGGLE TORCH
    // =========================================================

    private void toggleTorch() {

        if (camera == null) {

            Toast.makeText(
                    this,
                    "Camera is not ready yet.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // CHECK FLASH
        // -----------------------------------------------------

        if (
                !camera.getCameraInfo()
                        .hasFlashUnit()
        ) {

            Toast.makeText(
                    this,
                    "Flashlight is not available on this device.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // -----------------------------------------------------
        // TOGGLE
        // -----------------------------------------------------

        torchEnabled =
                !torchEnabled;


        camera.getCameraControl()
                .enableTorch(
                        torchEnabled
                );


        // -----------------------------------------------------
        // UPDATE UI
        // -----------------------------------------------------

        updateTorchUI();
    }


    // =========================================================
    // UPDATE TORCH UI
    // =========================================================

    private void updateTorchUI() {

        if (tvTorchBottom == null) {
            return;
        }


        if (torchEnabled) {

            tvTorchBottom.setText(
                    "Torch On"
            );

        } else {

            tvTorchBottom.setText(
                    "Torch"
            );
        }
    }


    // =========================================================
    // CAMERA PERMISSION DENIED
    // =========================================================

    private void showCameraPermissionDenied() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Camera Permission Required"
                )

                .setMessage(
                        "MyBank needs camera access to scan payment QR codes."
                )

                .setNegativeButton(
                        "Cancel",
                        (dialog, which) ->
                                finish()
                )

                .setPositiveButton(
                        "Settings",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            Settings
                                                    .ACTION_APPLICATION_DETAILS_SETTINGS
                                    );


                            intent.setData(
                                    Uri.parse(
                                            "package:"
                                                    + getPackageName()
                                    )
                            );


                            startActivity(
                                    intent
                            );
                        }
                )

                .show();
    }


    // =========================================================
    // ON RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        /*
         * If the user returned from Android Settings after
         * granting camera permission, start the camera again.
         */

        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_GRANTED
                        && cameraProvider == null
                        && !isProcessing.get()
        ) {

            startCamera();
        }
    }


    // =========================================================
    // ON DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        // -----------------------------------------------------
        // CAMERA
        // -----------------------------------------------------

        if (cameraProvider != null) {

            cameraProvider.unbindAll();
        }


        // -----------------------------------------------------
        // BARCODE SCANNER
        // -----------------------------------------------------

        if (barcodeScanner != null) {

            barcodeScanner.close();
        }


        // -----------------------------------------------------
        // EXECUTOR
        // -----------------------------------------------------

        if (cameraExecutor != null) {

            cameraExecutor.shutdown();
        }


        super.onDestroy();
    }
}
