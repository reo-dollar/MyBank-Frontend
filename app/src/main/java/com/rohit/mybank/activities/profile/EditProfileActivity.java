package com.rohit.mybank.activities.profile;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.databinding.ActivityEditProfileBinding;
import com.rohit.mybank.model.profile.ProfileResponse;
import com.rohit.mybank.model.profile.UpdateProfileRequest;
import com.rohit.mybank.repository.ProfileRepository;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private ProfileRepository repository;

    // =========================================================
    // PROFILE PHOTO
    // =========================================================

    @Nullable
    private Uri selectedPhotoUri;

    @Nullable
    private Drawable defaultProfileDrawable;

    private static final long MAX_PROFILE_PHOTO_SIZE =
            5L * 1024L * 1024L;


    // =========================================================
    // UPDATE STATE
    // =========================================================

    private boolean updateInProgress = false;


    // =========================================================
    // IMAGE PICKER
    // =========================================================

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "No photo selected",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        selectedPhotoUri = uri;

                        if (binding != null) {

                            binding.imgProfile.setImageURI(uri);

                            binding.btnRemovePhoto.setVisibility(
                                    View.VISIBLE
                            );
                        }
                    }
            );


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding =
                ActivityEditProfileBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );


        // -----------------------------------------------------
        // Save default profile image
        // -----------------------------------------------------

        defaultProfileDrawable =
                binding.imgProfile.getDrawable();


        // -----------------------------------------------------
        // Repository
        // -----------------------------------------------------

        repository =
                new ProfileRepository(this);


        // -----------------------------------------------------
        // Hide Remove Photo initially
        // -----------------------------------------------------

        binding.btnRemovePhoto.setVisibility(
                View.GONE
        );


        // -----------------------------------------------------
        // Load profile
        // -----------------------------------------------------

        loadProfile();


        // -----------------------------------------------------
        // Load profile photo
        // -----------------------------------------------------

        loadProfilePhoto();


        // -----------------------------------------------------
        // Change photo
        // -----------------------------------------------------

        binding.btnChangePhoto.setOnClickListener(
                v -> openPhotoPicker()
        );


        // -----------------------------------------------------
        // Remove photo
        // -----------------------------------------------------

        binding.btnRemovePhoto.setOnClickListener(
                v -> confirmRemovePhoto()
        );


        // -----------------------------------------------------
        // Update profile
        // -----------------------------------------------------

        binding.btnUpdate.setOnClickListener(
                v -> updateProfile()
        );
    }


    // =========================================================
    // OPEN PHOTO PICKER
    // =========================================================

    private void openPhotoPicker() {

        if (updateInProgress) {
            return;
        }

        pickImageLauncher.launch("image/*");
    }


    // =========================================================
    // LOAD PROFILE
    // =========================================================

    private void loadProfile() {

        repository.getProfile().enqueue(
                new Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProfileResponse> call,
                            Response<ProfileResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ProfileResponse profile =
                                    response.body();


                            // Email
                            binding.etEmail.setText(
                                    safeString(
                                            profile.getEmail()
                                    )
                            );


                            // Mobile
                            binding.etMobile.setText(
                                    safeString(
                                            profile.getMobile()
                                    )
                            );


                            // Address
                            binding.etAddress.setText(
                                    safeString(
                                            profile.getAddress()
                                    )
                            );


                            // City
                            binding.etCity.setText(
                                    safeString(
                                            profile.getCity()
                                    )
                            );


                            // State
                            binding.etState.setText(
                                    safeString(
                                            profile.getState()
                                    )
                            );


                            // Pincode
                            binding.etPincode.setText(
                                    safeString(
                                            profile.getPincode()
                                    )
                            );


                            // Occupation
                            binding.etOccupation.setText(
                                    safeString(
                                            profile.getOccupation()
                                    )
                            );

                        } else {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Unable to load profile.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<ProfileResponse> call,
                            Throwable t
                    ) {

                        if (call.isCanceled()) {
                            return;
                        }

                        Toast.makeText(
                                EditProfileActivity.this,
                                "Network Error: "
                                        + getErrorMessage(t),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // LOAD PROFILE PHOTO
    // =========================================================

    private void loadProfilePhoto() {

        repository.getProfilePhoto().enqueue(
                new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            try {

                                byte[] imageBytes =
                                        response.body().bytes();


                                if (imageBytes.length == 0) {

                                    hideRemovePhoto();
                                    return;
                                }


                                Bitmap bitmap =
                                        BitmapFactory.decodeByteArray(
                                                imageBytes,
                                                0,
                                                imageBytes.length
                                        );


                                if (bitmap != null
                                        && binding != null) {

                                    binding.imgProfile.setImageBitmap(
                                            bitmap
                                    );

                                    binding.btnRemovePhoto.setVisibility(
                                            View.VISIBLE
                                    );

                                } else {

                                    hideRemovePhoto();
                                }

                            } catch (IOException e) {

                                hideRemovePhoto();
                            }

                        } else if (response.code() == 404) {

                            hideRemovePhoto();

                        } else {

                            hideRemovePhoto();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {

                        hideRemovePhoto();
                    }
                }
        );
    }


    // =========================================================
    // HIDE REMOVE PHOTO
    // =========================================================

    private void hideRemovePhoto() {

        if (binding != null) {

            binding.btnRemovePhoto.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // CONFIRM REMOVE PHOTO
    // =========================================================

    private void confirmRemovePhoto() {

        if (updateInProgress) {
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("Remove Profile Photo")
                .setMessage(
                        "Are you sure you want to remove your profile photo?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Remove",
                        (dialog, which) ->
                                removeProfilePhoto()
                )
                .show();
    }


    // =========================================================
    // REMOVE PROFILE PHOTO
    // =========================================================

    private void removeProfilePhoto() {

        if (updateInProgress) {
            return;
        }


        updateInProgress = true;

        setControlsEnabled(false);


        repository.deleteProfilePhoto().enqueue(
                new Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProfileResponse> call,
                            Response<ProfileResponse> response
                    ) {

                        updateInProgress = false;

                        setControlsEnabled(true);


                        if (response.isSuccessful()) {

                            selectedPhotoUri = null;

                            restoreDefaultProfilePhoto();

                            hideRemovePhoto();


                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Profile photo removed successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    getHttpErrorMessage(
                                            response,
                                            "Unable to remove profile photo."
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<ProfileResponse> call,
                            Throwable t
                    ) {

                        updateInProgress = false;

                        setControlsEnabled(true);


                        if (call.isCanceled()) {
                            return;
                        }


                        Toast.makeText(
                                EditProfileActivity.this,
                                "Network Error: "
                                        + getErrorMessage(t),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // RESTORE DEFAULT PROFILE PHOTO
    // =========================================================

    private void restoreDefaultProfilePhoto() {

        if (binding == null) {
            return;
        }


        if (defaultProfileDrawable != null) {

            binding.imgProfile.setImageDrawable(
                    defaultProfileDrawable
            );

        } else {

            binding.imgProfile.setImageDrawable(
                    null
            );
        }
    }


    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    private void updateProfile() {

        if (updateInProgress) {
            return;
        }


        // -----------------------------------------------------
        // READ FIELDS
        // -----------------------------------------------------

        String email =
                binding.etEmail
                        .getText()
                        .toString()
                        .trim();


        String mobile =
                binding.etMobile
                        .getText()
                        .toString()
                        .trim();


        String address =
                binding.etAddress
                        .getText()
                        .toString()
                        .trim();


        String city =
                binding.etCity
                        .getText()
                        .toString()
                        .trim();


        String state =
                binding.etState
                        .getText()
                        .toString()
                        .trim();


        String pincode =
                binding.etPincode
                        .getText()
                        .toString()
                        .trim();


        String occupation =
                binding.etOccupation
                        .getText()
                        .toString()
                        .trim();


        // -----------------------------------------------------
        // CLEAR OLD ERRORS
        // -----------------------------------------------------

        binding.etEmail.setError(null);
        binding.etMobile.setError(null);
        binding.etAddress.setError(null);
        binding.etCity.setError(null);
        binding.etState.setError(null);
        binding.etPincode.setError(null);
        binding.etOccupation.setError(null);


        // =====================================================
        // EMAIL VALIDATION
        // =====================================================

        if (email.isEmpty()) {

            binding.etEmail.setError(
                    "Email is required"
            );

            binding.etEmail.requestFocus();

            return;
        }


        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            binding.etEmail.setError(
                    "Enter a valid email address"
            );

            binding.etEmail.requestFocus();

            return;
        }


        // =====================================================
        // MOBILE VALIDATION
        // =====================================================

        if (mobile.isEmpty()) {

            binding.etMobile.setError(
                    "Mobile number is required"
            );

            binding.etMobile.requestFocus();

            return;
        }


        if (!mobile.matches(
                "^[6-9]\\d{9}$"
        )) {

            binding.etMobile.setError(
                    "Enter a valid 10-digit mobile number"
            );

            binding.etMobile.requestFocus();

            return;
        }


        // =====================================================
        // ADDRESS VALIDATION
        // =====================================================

        if (address.isEmpty()) {

            binding.etAddress.setError(
                    "Address is required"
            );

            binding.etAddress.requestFocus();

            return;
        }


        if (address.length() < 10
                || address.length() > 200) {

            binding.etAddress.setError(
                    "Address must be between 10 and 200 characters"
            );

            binding.etAddress.requestFocus();

            return;
        }


        // =====================================================
        // CITY VALIDATION
        // =====================================================

        if (city.isEmpty()) {

            binding.etCity.setError(
                    "City is required"
            );

            binding.etCity.requestFocus();

            return;
        }


        if (!city.matches(
                "^[A-Za-z ]+$"
        )) {

            binding.etCity.setError(
                    "City must contain only letters"
            );

            binding.etCity.requestFocus();

            return;
        }


        if (city.length() < 2
                || city.length() > 50) {

            binding.etCity.setError(
                    "City must be between 2 and 50 characters"
            );

            binding.etCity.requestFocus();

            return;
        }


        // =====================================================
        // STATE VALIDATION
        // =====================================================

        if (state.isEmpty()) {

            binding.etState.setError(
                    "State is required"
            );

            binding.etState.requestFocus();

            return;
        }


        if (!state.matches(
                "^[A-Za-z ]+$"
        )) {

            binding.etState.setError(
                    "State must contain only letters"
            );

            binding.etState.requestFocus();

            return;
        }


        if (state.length() < 2
                || state.length() > 50) {

            binding.etState.setError(
                    "State must be between 2 and 50 characters"
            );

            binding.etState.requestFocus();

            return;
        }


        // =====================================================
        // PINCODE VALIDATION
        // =====================================================

        if (pincode.isEmpty()) {

            binding.etPincode.setError(
                    "Pincode is required"
            );

            binding.etPincode.requestFocus();

            return;
        }


        if (!pincode.matches(
                "^\\d{6}$"
        )) {

            binding.etPincode.setError(
                    "Pincode must contain exactly 6 digits"
            );

            binding.etPincode.requestFocus();

            return;
        }


        // =====================================================
        // OCCUPATION VALIDATION
        // =====================================================

        if (occupation.isEmpty()) {

            binding.etOccupation.setError(
                    "Occupation is required"
            );

            binding.etOccupation.requestFocus();

            return;
        }


        if (occupation.length() < 2
                || occupation.length() > 50) {

            binding.etOccupation.setError(
                    "Occupation must be between 2 and 50 characters"
            );

            binding.etOccupation.requestFocus();

            return;
        }


        // =====================================================
        // CREATE REQUEST
        // =====================================================

        UpdateProfileRequest request =
                new UpdateProfileRequest();


        request.setEmail(email);

        request.setMobile(mobile);

        request.setAddress(address);

        request.setCity(city);

        request.setState(state);

        request.setPincode(pincode);

        request.setOccupation(occupation);


        // =====================================================
        // START UPDATE
        // =====================================================

        updateInProgress = true;

        setControlsEnabled(false);


        // =====================================================
        // UPDATE PROFILE
        // =====================================================

        repository.updateProfile(request)
                .enqueue(
                        new Callback<ProfileResponse>() {

                            @Override
                            public void onResponse(
                                    Call<ProfileResponse> call,
                                    Response<ProfileResponse> response
                            ) {

                                if (!response.isSuccessful()) {

                                    updateInProgress = false;

                                    setControlsEnabled(true);

                                    handleProfileUpdateError(
                                            response
                                    );

                                    return;
                                }


                                // -------------------------------------------------
                                // Profile details updated
                                // -------------------------------------------------

                                if (selectedPhotoUri != null) {

                                    uploadSelectedPhoto();

                                } else {

                                    updateInProgress = false;


                                    Toast.makeText(
                                            EditProfileActivity.this,
                                            "Profile Updated Successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    finish();
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ProfileResponse> call,
                                    Throwable t
                            ) {

                                updateInProgress = false;

                                setControlsEnabled(true);


                                if (call.isCanceled()) {
                                    return;
                                }


                                Toast.makeText(
                                        EditProfileActivity.this,
                                        "Network Error: "
                                                + getErrorMessage(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // HANDLE PROFILE UPDATE ERROR
    // =========================================================

    private void handleProfileUpdateError(
            Response<?> response
    ) {

        if (response == null) {

            Toast.makeText(
                    EditProfileActivity.this,
                    "Profile update failed.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        // =====================================================
        // HTTP 400
        // =====================================================

        if (response.code() == 400) {

            String errorMessage =
                    extractValidationError(
                            response
                    );


            Toast.makeText(
                    EditProfileActivity.this,
                    errorMessage,
                    Toast.LENGTH_LONG
            ).show();


            return;
        }


        // =====================================================
        // HTTP 409
        // =====================================================

        if (response.code() == 409) {

            handleProfileConflict(
                    response
            );

            return;
        }


        // =====================================================
        // OTHER HTTP ERRORS
        // =====================================================

        Toast.makeText(
                EditProfileActivity.this,
                getHttpErrorMessage(
                        response,
                        "Profile update failed."
                ),
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // HANDLE 409 CONFLICT
    // =========================================================

    private void handleProfileConflict(
            Response<?> response
    ) {

        String message =
                extractServerMessage(
                        response
                );


        String lowerMessage =
                message.toLowerCase();


        // =====================================================
        // EMAIL CONFLICT
        // =====================================================

        if (lowerMessage.contains("email")) {

            binding.etEmail.setError(
                    "Email already registered. Please use a different email."
            );


            binding.etEmail.requestFocus();


            Toast.makeText(
                    EditProfileActivity.this,
                    "Email already registered.",
                    Toast.LENGTH_LONG
            ).show();


            return;
        }


        // =====================================================
        // MOBILE CONFLICT
        // =====================================================

        if (lowerMessage.contains("mobile")
                || lowerMessage.contains("phone")) {

            binding.etMobile.setError(
                    "Mobile number already registered. Please use a different number."
            );


            binding.etMobile.requestFocus();


            Toast.makeText(
                    EditProfileActivity.this,
                    "Mobile number already registered.",
                    Toast.LENGTH_LONG
            ).show();


            return;
        }


        // =====================================================
        // UNKNOWN CONFLICT
        // =====================================================

        Toast.makeText(
                EditProfileActivity.this,
                message.isEmpty()
                        ? "Profile update conflict."
                        : message,
                Toast.LENGTH_LONG
        ).show();
    }


    // =========================================================
    // EXTRACT SERVER MESSAGE
    // =========================================================

    private String extractServerMessage(
            Response<?> response
    ) {

        try {

            if (response == null) {
                return "";
            }


            if (response.errorBody() == null) {
                return "";
            }


            String errorBody =
                    response.errorBody()
                            .string();


            if (errorBody == null
                    || errorBody.trim().isEmpty()) {

                return "";
            }


            JSONObject json =
                    new JSONObject(
                            errorBody
                    );


            // -------------------------------------------------
            // Spring message
            // -------------------------------------------------

            String message =
                    json.optString(
                            "message",
                            ""
                    );


            if (!message.isEmpty()) {

                return message;
            }


            // -------------------------------------------------
            // Error field
            // -------------------------------------------------

            String error =
                    json.optString(
                            "error",
                            ""
                    );


            if (!error.isEmpty()) {

                return error;
            }


        } catch (Exception ignored) {

            // Ignore JSON parsing errors.
        }


        return "";
    }


    // =========================================================
    // EXTRACT VALIDATION ERROR
    // =========================================================

    private String extractValidationError(
            Response<?> response
    ) {

        try {

            if (response.errorBody() == null) {

                return "Profile update failed. (HTTP "
                        + response.code()
                        + ")";
            }


            String errorBody =
                    response.errorBody()
                            .string();


            if (errorBody == null
                    || errorBody.trim().isEmpty()) {

                return "Profile update failed. (HTTP "
                        + response.code()
                        + ")";
            }


            JSONObject json =
                    new JSONObject(
                            errorBody
                    );


            // =================================================
            // FIELD VALIDATION ERRORS
            // =================================================

            if (json.has("fields")) {

                JSONObject fields =
                        json.getJSONObject(
                                "fields"
                        );


                StringBuilder message =
                        new StringBuilder();


                Iterator<String> keys =
                        fields.keys();


                while (keys.hasNext()) {

                    String field =
                            keys.next();


                    String error =
                            fields.optString(
                                    field,
                                    ""
                            );


                    if (error.isEmpty()) {
                        continue;
                    }


                    if (message.length() > 0) {

                        message.append("\n");
                    }


                    message.append(
                            formatFieldName(
                                    field
                            )
                    );


                    message.append(": ");

                    message.append(error);
                }


                if (message.length() > 0) {

                    return message.toString();
                }
            }


            // =================================================
            // NORMAL BACKEND MESSAGE
            // =================================================

            String backendMessage =
                    json.optString(
                            "message",
                            ""
                    );


            if (!backendMessage.isEmpty()) {

                return backendMessage;
            }


            // =================================================
            // ERROR FIELD
            // =================================================

            String error =
                    json.optString(
                            "error",
                            ""
                    );


            if (!error.isEmpty()) {

                return error;
            }


            // =================================================
            // FALLBACK
            // =================================================

            return "Profile update failed. (HTTP "
                    + response.code()
                    + ")";


        } catch (Exception e) {

            return "Profile update failed. (HTTP "
                    + response.code()
                    + ")";
        }
    }


    // =========================================================
    // FORMAT FIELD NAME
    // =========================================================

    private String formatFieldName(
            String field
    ) {

        if (field == null
                || field.trim().isEmpty()) {

            return "Field";
        }


        switch (field) {

            case "email":
                return "Email";

            case "mobile":
                return "Mobile";

            case "address":
                return "Address";

            case "city":
                return "City";

            case "state":
                return "State";

            case "pincode":
                return "Pincode";

            case "occupation":
                return "Occupation";

            default:

                if (field.length() == 1) {

                    return field.toUpperCase();
                }


                return Character.toUpperCase(
                        field.charAt(0)
                ) + field.substring(1);
        }
    }


    // =========================================================
    // UPLOAD SELECTED PROFILE PHOTO
    // =========================================================

    private void uploadSelectedPhoto() {

        if (selectedPhotoUri == null) {

            updateInProgress = false;

            setControlsEnabled(true);


            Toast.makeText(
                    EditProfileActivity.this,
                    "Profile Updated Successfully",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

            return;
        }


        // =====================================================
        // MIME TYPE
        // =====================================================

        String mimeType =
                getContentResolver().getType(
                        selectedPhotoUri
                );


        if (mimeType == null
                || mimeType.trim().isEmpty()) {

            mimeType =
                    detectMimeType(
                            selectedPhotoUri
                    );
        }


        if (mimeType == null
                || mimeType.trim().isEmpty()) {

            mimeType = "image/jpeg";
        }


        mimeType =
                mimeType.trim().toLowerCase();


        // =====================================================
        // VALIDATE MIME TYPE
        // =====================================================

        if (!mimeType.equals("image/jpeg")
                && !mimeType.equals("image/jpg")
                && !mimeType.equals("image/png")) {

            updateInProgress = false;

            setControlsEnabled(true);


            Toast.makeText(
                    EditProfileActivity.this,
                    "Please select a JPEG or PNG image.",
                    Toast.LENGTH_LONG
            ).show();


            return;
        }


        // =====================================================
        // VALIDATE SIZE
        // =====================================================

        long fileSize =
                getFileSize(
                        selectedPhotoUri
                );


        if (fileSize > MAX_PROFILE_PHOTO_SIZE) {

            updateInProgress = false;

            setControlsEnabled(true);


            Toast.makeText(
                    EditProfileActivity.this,
                    "Profile photo must not exceed 5 MB.",
                    Toast.LENGTH_LONG
            ).show();


            return;
        }


        // =====================================================
        // REQUEST BODY
        // =====================================================

        RequestBody requestBody =
                createImageRequestBody(
                        selectedPhotoUri,
                        mimeType
                );


        MultipartBody.Part photoPart =
                MultipartBody.Part.createFormData(
                        "photo",
                        "profile.jpg",
                        requestBody
                );


        // =====================================================
        // UPLOAD
        // =====================================================

        repository.uploadProfilePhoto(
                photoPart
        ).enqueue(
                new Callback<ProfileResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProfileResponse> call,
                            Response<ProfileResponse> response
                    ) {

                        updateInProgress = false;

                        setControlsEnabled(true);


                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Profile Updated Successfully",
                                    Toast.LENGTH_SHORT
                            ).show();


                            finish();

                        } else {

                            Toast.makeText(
                                    EditProfileActivity.this,
                                    getHttpErrorMessage(
                                            response,
                                            "Profile details updated, but photo upload failed."
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<ProfileResponse> call,
                            Throwable t
                    ) {

                        updateInProgress = false;

                        setControlsEnabled(true);


                        if (call.isCanceled()) {
                            return;
                        }


                        Toast.makeText(
                                EditProfileActivity.this,
                                "Profile details updated, but photo upload failed: "
                                        + getErrorMessage(t),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // CREATE IMAGE REQUEST BODY
    // =========================================================

    private RequestBody createImageRequestBody(
            Uri uri,
            String mimeType
    ) {

        final String finalMimeType =
                (mimeType != null
                        && !mimeType.trim().isEmpty())
                        ? mimeType
                        : "image/jpeg";


        return new RequestBody() {

            @Override
            public MediaType contentType() {

                return MediaType.parse(
                        finalMimeType
                );
            }


            @Override
            public long contentLength() {

                return getFileSize(uri);
            }


            @Override
            public void writeTo(
                    BufferedSink sink
            ) throws IOException {

                InputStream inputStream = null;


                try {

                    inputStream =
                            getContentResolver()
                                    .openInputStream(uri);


                    if (inputStream == null) {

                        throw new IOException(
                                "Unable to open selected image."
                        );
                    }


                    byte[] buffer =
                            new byte[8192];


                    int bytesRead;


                    while ((bytesRead =
                            inputStream.read(buffer)) != -1) {

                        sink.write(
                                buffer,
                                0,
                                bytesRead
                        );
                    }


                } finally {

                    if (inputStream != null) {

                        try {

                            inputStream.close();

                        } catch (IOException ignored) {

                            // Ignore close exception.
                        }
                    }
                }
            }
        };
    }


    // =========================================================
    // GET FILE SIZE
    // =========================================================

    private long getFileSize(
            Uri uri
    ) {

        if (uri == null) {
            return -1;
        }


        Cursor cursor = null;


        try {

            cursor =
                    getContentResolver().query(
                            uri,
                            null,
                            null,
                            null,
                            null
                    );


            if (cursor != null) {

                int sizeIndex =
                        cursor.getColumnIndex(
                                OpenableColumns.SIZE
                        );


                if (sizeIndex >= 0
                        && cursor.moveToFirst()) {

                    return cursor.getLong(
                            sizeIndex
                    );
                }
            }


        } catch (Exception ignored) {

            // Unknown size.

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }


        return -1;
    }


    // =========================================================
    // DETECT MIME TYPE
    // =========================================================

    private String detectMimeType(
            Uri uri
    ) {

        if (uri == null) {
            return null;
        }


        String value =
                uri.toString()
                        .toLowerCase();


        if (value.endsWith(".png")) {

            return "image/png";
        }


        if (value.endsWith(".jpg")
                || value.endsWith(".jpeg")) {

            return "image/jpeg";
        }


        return null;
    }


    // =========================================================
    // ENABLE / DISABLE CONTROLS
    // =========================================================

    private void setControlsEnabled(
            boolean enabled
    ) {

        if (binding == null) {
            return;
        }


        binding.btnUpdate.setEnabled(
                enabled
        );


        binding.btnChangePhoto.setEnabled(
                enabled
        );


        binding.btnRemovePhoto.setEnabled(
                enabled
        );
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(
            String value
    ) {

        return value != null
                ? value
                : "";
    }


    // =========================================================
    // HTTP ERROR MESSAGE
    // =========================================================

    private String getHttpErrorMessage(
            Response<?> response,
            String fallback
    ) {

        if (response == null) {
            return fallback;
        }


        String message =
                "HTTP " + response.code();


        try {

            if (response.errorBody() != null) {

                String error =
                        response.errorBody()
                                .string();


                if (error != null
                        && !error.trim().isEmpty()) {

                    if (error.length() > 300) {

                        error =
                                error.substring(
                                        0,
                                        300
                                );
                    }


                    return fallback
                            + " ("
                            + message
                            + "): "
                            + error;
                }
            }


        } catch (Exception ignored) {

            // Use fallback.
        }


        return fallback
                + " ("
                + message
                + ")";
    }


    // =========================================================
    // NETWORK ERROR MESSAGE
    // =========================================================

    private String getErrorMessage(
            Throwable throwable
    ) {

        if (throwable == null) {

            return "Unknown error";
        }


        String message =
                throwable.getMessage();


        if (message == null
                || message.trim().isEmpty()) {

            return "Unknown error";
        }


        return message;
    }


    // =========================================================
    // ON DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        binding = null;
    }
}