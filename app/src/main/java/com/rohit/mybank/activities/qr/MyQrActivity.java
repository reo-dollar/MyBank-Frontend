package com.rohit.mybank.activities.qr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.qr.QrMyResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * =========================================================
 * MY QR ACTIVITY
 * =========================================================
 *
 * Displays the authenticated user's personal MyBank QR.
 *
 * Backend:
 *
 * GET /api/qr/me
 *
 * QR contains only:
 *
 * MYBANK://PAY/PAY_...
 *
 * It does NOT contain:
 *
 * - password
 * - transaction PIN
 * - JWT
 * - account number
 *
 * =========================================================
 */
public class MyQrActivity extends AppCompatActivity {

    // =========================================================
    // UI
    // =========================================================

    private ImageButton btnBack;

    private TextView tvAvatar;

    private TextView tvCustomerName;

    private TextView tvPaymentId;

    private ImageView imgQrCode;

    private ImageButton btnCopyPaymentId;

    private Button btnShareQr;

    private Button btnSaveQr;

    private ProgressBar qrProgress;


    // =========================================================
    // API
    // =========================================================

    private ApiService apiService;


    // =========================================================
    // DATA
    // =========================================================

    private String qrPayload;

    private String paymentId;

    private String customerName;

    private Bitmap qrBitmap;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_my_qr
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
        // INITIALIZE
        // -----------------------------------------------------

        initializeViews();

        initializeApi();

        setupListeners();

        loadMyQr();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        btnBack =
                findViewById(
                        R.id.btnBack
                );

        tvAvatar =
                findViewById(
                        R.id.tvAvatar
                );

        tvCustomerName =
                findViewById(
                        R.id.tvCustomerName
                );

        tvPaymentId =
                findViewById(
                        R.id.tvPaymentId
                );

        imgQrCode =
                findViewById(
                        R.id.imgQrCode
                );

        btnCopyPaymentId =
                findViewById(
                        R.id.btnCopyPaymentId
                );

        btnShareQr =
                findViewById(
                        R.id.btnShareQr
                );

        btnSaveQr =
                findViewById(
                        R.id.btnSaveQr
                );

        qrProgress =
                findViewById(
                        R.id.qrProgress
                );
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
    // LISTENERS
    // =========================================================

    private void setupListeners() {

        btnBack.setOnClickListener(
                view -> finish()
        );


        btnCopyPaymentId.setOnClickListener(
                view -> copyPaymentId()
        );


        btnShareQr.setOnClickListener(
                view -> shareQr()
        );


        btnSaveQr.setOnClickListener(
                view -> saveQr()
        );
    }


    // =========================================================
    // LOAD MY QR
    // =========================================================

    private void loadMyQr() {

        setLoading(true);


        apiService
                .getMyQr()
                .enqueue(
                        new Callback<QrMyResponse>() {

                            @Override
                            public void onResponse(
                                    Call<QrMyResponse> call,
                                    Response<QrMyResponse> response) {

                                if (
                                        response.isSuccessful()
                                                && response.body() != null
                                ) {

                                    QrMyResponse data =
                                            response.body();


                                    if (
                                            !data.isSuccess()
                                                    || data.getQrPayload() == null
                                                    || data.getPaymentId() == null
                                    ) {

                                        showLoadError(
                                                "Unable to load your QR."
                                        );

                                        return;
                                    }


                                    qrPayload =
                                            data.getQrPayload();

                                    paymentId =
                                            data.getPaymentId();

                                    customerName =
                                            safe(
                                                    data.getCustomerName()
                                            );


                                    // ---------------------------------
                                    // NAME
                                    // ---------------------------------

                                    tvCustomerName.setText(
                                            customerName.isEmpty()
                                                    ? "MyBank User"
                                                    : customerName
                                    );


                                    // ---------------------------------
                                    // AVATAR INITIAL
                                    // ---------------------------------

                                    tvAvatar.setText(
                                            getInitials(
                                                    customerName
                                            )
                                    );


                                    // ---------------------------------
                                    // PAYMENT ID
                                    // ---------------------------------

                                    tvPaymentId.setText(
                                            paymentId
                                    );


                                    // ---------------------------------
                                    // GENERATE QR
                                    // ---------------------------------

                                    generateQrCode(
                                            qrPayload
                                    );


                                } else {

                                    showLoadError(
                                            getHttpErrorMessage(
                                                    response.code()
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<QrMyResponse> call,
                                    Throwable throwable) {

                                if (call.isCanceled()) {
                                    return;
                                }

                                showLoadError(
                                        "Network error. Please try again."
                                );
                            }
                        }
                );
    }


    // =========================================================
    // GENERATE QR CODE
    // =========================================================

    private void generateQrCode(
            String payload) {

        if (
                payload == null
                        || payload.trim().isEmpty()
        ) {

            showLoadError(
                    "QR payload is empty."
            );

            return;
        }


        new Thread(
                () -> {

                    try {

                        Map<EncodeHintType, Object> hints =
                                new EnumMap<>(
                                        EncodeHintType.class
                                );

                        hints.put(
                                EncodeHintType.CHARACTER_SET,
                                "UTF-8"
                        );

                        hints.put(
                                EncodeHintType.MARGIN,
                                1
                        );


                        BitMatrix matrix =
                                new MultiFormatWriter()
                                        .encode(
                                                payload,
                                                BarcodeFormat.QR_CODE,
                                                800,
                                                800,
                                                hints
                                        );


                        int width =
                                matrix.getWidth();

                        int height =
                                matrix.getHeight();


                        Bitmap bitmap =
                                Bitmap.createBitmap(
                                        width,
                                        height,
                                        Bitmap.Config.ARGB_8888
                                );


                        for (
                                int x = 0;
                                x < width;
                                x++
                        ) {

                            for (
                                    int y = 0;
                                    y < height;
                                    y++
                            ) {

                                bitmap.setPixel(
                                        x,
                                        y,
                                        matrix.get(
                                                x,
                                                y
                                        )
                                                ? Color.BLACK
                                                : Color.WHITE
                                );
                            }
                        }


                        runOnUiThread(
                                () -> {

                                    qrBitmap =
                                            bitmap;

                                    imgQrCode.setImageBitmap(
                                            bitmap
                                    );

                                    setLoading(false);
                                }
                        );


                    } catch (Exception exception) {

                        runOnUiThread(
                                () -> showLoadError(
                                        "Unable to generate QR code."
                                )
                        );
                    }

                }
        ).start();
    }


    // =========================================================
    // COPY PAYMENT ID
    // =========================================================

    private void copyPaymentId() {

        if (
                paymentId == null
                        || paymentId.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Payment ID is not available.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        ClipboardManager clipboard =
                (ClipboardManager)
                        getSystemService(
                                CLIPBOARD_SERVICE
                        );


        if (clipboard == null) {
            return;
        }


        ClipData clip =
                ClipData.newPlainText(
                        "MyBank Payment ID",
                        paymentId
                );


        clipboard.setPrimaryClip(
                clip
        );


        Toast.makeText(
                this,
                "Payment ID copied.",
                Toast.LENGTH_SHORT
        ).show();
    }


    // =========================================================
    // SHARE QR
    // =========================================================

    private void shareQr() {

        if (qrBitmap == null) {

            Toast.makeText(
                    this,
                    "QR code is not ready yet.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        try {

            File shareDirectory =
                    new File(
                            getCacheDir(),
                            "shared_qr"
                    );


            if (!shareDirectory.exists()) {

                if (!shareDirectory.mkdirs()) {

                    Toast.makeText(
                            this,
                            "Unable to prepare QR for sharing.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }
            }


            File qrFile =
                    new File(
                            shareDirectory,
                            "mybank_qr.png"
                    );


            FileOutputStream outputStream =
                    new FileOutputStream(
                            qrFile
                    );


            qrBitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
            );


            outputStream.flush();

            outputStream.close();


            Uri uri =
                    FileProvider.getUriForFile(
                            this,
                            getPackageName()
                                    + ".fileprovider",
                            qrFile
                    );


            Intent shareIntent =
                    new Intent(
                            Intent.ACTION_SEND
                    );


            shareIntent.setType(
                    "image/png"
            );


            shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    uri
            );


            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Pay me on MyBank using my QR code."
            );


            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );


            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share MyBank QR"
                    )
            );


        } catch (Exception exception) {

            Toast.makeText(
                    this,
                    "Unable to share QR code.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // =========================================================
    // SAVE QR
    // =========================================================

    private void saveQr() {

        if (qrBitmap == null) {

            Toast.makeText(
                    this,
                    "QR code is not ready yet.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        try {

            String fileName =
                    "MyBank_QR_"
                            + System.currentTimeMillis()
                            + ".png";


            if (
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.Q
            ) {

                ContentValues values =
                        new ContentValues();


                values.put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        fileName
                );


                values.put(
                        MediaStore.Images.Media.MIME_TYPE,
                        "image/png"
                );


                values.put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES
                                + "/MyBank"
                );


                Uri uri =
                        getContentResolver()
                                .insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        values
                                );


                if (uri == null) {

                    Toast.makeText(
                            this,
                            "Unable to save QR code.",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }


                OutputStream outputStream =
                        getContentResolver()
                                .openOutputStream(
                                        uri
                                );


                if (outputStream == null) {

                    getContentResolver()
                            .delete(
                                    uri,
                                    null,
                                    null
                            );

                    Toast.makeText(
                            this,
                            "Unable to save QR code.",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }


                qrBitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        outputStream
                );


                outputStream.flush();

                outputStream.close();


                Toast.makeText(
                        this,
                        "QR saved to Pictures/MyBank.",
                        Toast.LENGTH_LONG
                ).show();


            } else {

                File directory =
                        new File(
                                getExternalFilesDir(
                                        Environment.DIRECTORY_PICTURES
                                ),
                                "MyBank"
                        );


                if (!directory.exists()) {

                    directory.mkdirs();
                }


                File file =
                        new File(
                                directory,
                                fileName
                        );


                FileOutputStream outputStream =
                        new FileOutputStream(
                                file
                        );


                qrBitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        outputStream
                );


                outputStream.flush();

                outputStream.close();


                Toast.makeText(
                        this,
                        "QR saved successfully.",
                        Toast.LENGTH_LONG
                ).show();
            }


        } catch (Exception exception) {

            Toast.makeText(
                    this,
                    "Unable to save QR code.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    // =========================================================
    // LOADING STATE
    // =========================================================

    private void setLoading(
            boolean loading) {

        if (qrProgress != null) {

            qrProgress.setVisibility(
                    loading
                            ? View.VISIBLE
                            : View.GONE
            );
        }


        if (btnShareQr != null) {

            btnShareQr.setEnabled(
                    !loading
            );
        }


        if (btnSaveQr != null) {

            btnSaveQr.setEnabled(
                    !loading
            );
        }


        if (btnCopyPaymentId != null) {

            btnCopyPaymentId.setEnabled(
                    !loading
            );
        }
    }


    // =========================================================
    // LOAD ERROR
    // =========================================================

    private void showLoadError(
            String message) {

        setLoading(false);


        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();


        if (tvCustomerName != null) {

            tvCustomerName.setText(
                    "Unable to load My QR"
            );
        }
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        if (
                value == null
                        || value.trim().isEmpty()
        ) {

            return "";
        }

        return value.trim();
    }


    // =========================================================
    // INITIALS
    // =========================================================

    private String getInitials(
            String name) {

        if (
                name == null
                        || name.trim().isEmpty()
        ) {

            return "?";
        }


        String[] parts =
                name.trim().split(
                        "\\s+"
                );


        if (parts.length == 1) {

            return parts[0]
                    .substring(
                            0,
                            1
                    )
                    .toUpperCase();
        }


        String first =
                parts[0]
                        .substring(
                                0,
                                1
                        );


        String last =
                parts[parts.length - 1]
                        .substring(
                                0,
                                1
                        );


        return (
                first
                        + last
        ).toUpperCase();
    }


    // =========================================================
    // HTTP ERROR
    // =========================================================

    private String getHttpErrorMessage(
            int code) {

        if (code == 401) {

            return "Your session has expired. Please login again.";
        }


        if (code == 403) {

            return "You are not authorized to access your QR.";
        }


        if (code == 404) {

            return "Your MyBank QR could not be found.";
        }


        return "Unable to load your QR. Error "
                + code
                + ".";
    }
}