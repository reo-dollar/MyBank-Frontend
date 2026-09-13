package com.rohit.mybank.activities.profile;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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

import java.io.IOException;
import java.io.InputStream;

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

    /**
     * Image selected by the user.
     *
     * The image is NOT uploaded immediately.
     * It is uploaded only when Update is pressed.
     */
    @Nullable
    private Uri selectedPhotoUri;

    /**
     * Prevents duplicate update/remove/upload operations.
     */
    private boolean updateInProgress = false;

    /**
     * Stores the original/default drawable of the profile ImageView.
     *
     * This avoids hardcoding a drawable name such as
     * R.drawable.ic_profile.
     */
    @Nullable
    private Drawable defaultProfileDrawable;

    /**
     * Maximum Android-side profile photo size.
     */
    private static final long MAX_PROFILE_PHOTO_SIZE =
            5L * 1024L * 1024L;

    /**
     * Android image picker.
     */
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

                        /*
                         * Store selected URI.
                         */
                        selectedPhotoUri = uri;

                        /*
                         * Show selected image immediately.
                         */
                        if (binding != null) {

                            binding.imgProfile.setImageURI(uri);

                            /*
                             * A photo is now available,
                             * so show Remove Photo.
                             */
                            binding.btnRemovePhoto.setVisibility(
                                    View.VISIBLE
                            );
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Initialize ViewBinding.
         */
        binding = ActivityEditProfileBinding.inflate(
                getLayoutInflater()
        );

        setContentView(binding.getRoot());

        /*
         * Save the default profile drawable before
         * loading the user's actual photo.
         */
        defaultProfileDrawable =
                binding.imgProfile.getDrawable();

        /*
         * Initialize repository.
         */
        repository = new ProfileRepository(this);

        /*
         * Remove Photo is hidden initially.
         *
         * It will become visible if an existing photo
         * is successfully loaded.
         */
        binding.btnRemovePhoto.setVisibility(
                View.GONE
        );

        /*
         * Load profile details.
         */
        loadProfile();

        /*
         * Load existing profile photo.
         */
        loadProfilePhoto();

        /*
         * Change Photo button.
         */
        binding.btnChangePhoto.setOnClickListener(
                v -> openPhotoPicker()
        );

        /*
         * Remove Photo button.
         */
        binding.btnRemovePhoto.setOnClickListener(
                v -> confirmRemovePhoto()
        );

        /*
         * Update button.
         */
        binding.btnUpdate.setOnClickListener(
                v -> updateProfile()
        );
    }

    /**
     * Opens Android image picker.
     */
    private void openPhotoPicker() {

        if (updateInProgress) {
            return;
        }

        pickImageLauncher.launch("image/*");
    }

    /**
     * Loads profile details from backend.
     */
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

                            /*
                             * Email.
                             */
                            binding.etEmail.setText(
                                    safeString(
                                            profile.getEmail()
                                    )
                            );

                            /*
                             * Mobile.
                             */
                            binding.etMobile.setText(
                                    safeString(
                                            profile.getMobile()
                                    )
                            );

                            /*
                             * Address.
                             */
                            binding.etAddress.setText(
                                    safeString(
                                            profile.getAddress()
                                    )
                            );

                            /*
                             * City.
                             */
                            binding.etCity.setText(
                                    safeString(
                                            profile.getCity()
                                    )
                            );

                            /*
                             * State.
                             */
                            binding.etState.setText(
                                    safeString(
                                            profile.getState()
                                    )
                            );

                            /*
                             * Pincode.
                             */
                            binding.etPincode.setText(
                                    safeString(
                                            profile.getPincode()
                                    )
                            );

                            /*
                             * Occupation.
                             */
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

    /**
     * Loads existing profile photo.
     *
     * Backend:
     *
     * 200 -> photo exists
     * 404 -> no photo exists
     */
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

                                /*
                                 * Read image bytes.
                                 */
                                byte[] imageBytes =
                                        response.body().bytes();

                                if (imageBytes.length == 0) {

                                    hideRemovePhoto();
                                    return;
                                }

                                /*
                                 * Decode JPEG returned by backend.
                                 */
                                Bitmap bitmap =
                                        BitmapFactory.decodeByteArray(
                                                imageBytes,
                                                0,
                                                imageBytes.length
                                        );

                                if (bitmap != null
                                        && binding != null) {

                                    /*
                                     * Display existing profile photo.
                                     */
                                    binding.imgProfile.setImageBitmap(
                                            bitmap
                                    );

                                    /*
                                     * Photo exists.
                                     */
                                    binding.btnRemovePhoto.setVisibility(
                                            View.VISIBLE
                                    );
                                } else {

                                    hideRemovePhoto();
                                }

                            } catch (IOException e) {

                                /*
                                 * Keep default profile icon.
                                 */
                                hideRemovePhoto();
                            }

                        } else if (response.code() == 404) {

                            /*
                             * User has no profile photo.
                             */
                            hideRemovePhoto();

                        } else {

                            /*
                             * Photo loading failed.
                             *
                             * Do not block profile editing.
                             */
                            hideRemovePhoto();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {

                        /*
                         * Photo loading failure should not
                         * prevent profile editing.
                         */
                        hideRemovePhoto();
                    }
                }
        );
    }

    /**
     * Hides the Remove Photo button.
     */
    private void hideRemovePhoto() {

        if (binding != null) {

            binding.btnRemovePhoto.setVisibility(
                    View.GONE
            );
        }
    }

    /**
     * Shows confirmation dialog before deleting
     * the profile photo.
     */
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

    /**
     * Removes profile photo from backend.
     *
     * Backend:
     *
     * DELETE /profile/photo
     */
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

                            /*
                             * Clear any locally selected photo.
                             */
                            selectedPhotoUri = null;

                            /*
                             * Restore the original/default
                             * profile drawable.
                             */
                            restoreDefaultProfilePhoto();

                            /*
                             * Hide Remove Photo button.
                             */
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

    /**
     * Restores the default profile image.
     */
    private void restoreDefaultProfilePhoto() {

        if (binding == null) {
            return;
        }

        if (defaultProfileDrawable != null) {

            binding.imgProfile.setImageDrawable(
                    defaultProfileDrawable
            );

        } else {

            /*
             * If there was no drawable configured in XML,
             * clear the ImageView.
             */
            binding.imgProfile.setImageDrawable(null);
        }
    }

    /**
     * Updates profile details and, if selected,
     * profile photo.
     *
     * Flow:
     *
     * 1. Validate fields
     * 2. Update profile details
     * 3. Upload selected photo
     * 4. Finish only when required operations succeed
     */
    private void updateProfile() {

        if (updateInProgress) {
            return;
        }

        /*
         * Read fields.
         */
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

        /*
         * Basic validation.
         */
        if (email.isEmpty()) {

            binding.etEmail.setError(
                    "Email is required"
            );

            binding.etEmail.requestFocus();

            return;
        }

        if (mobile.isEmpty()) {

            binding.etMobile.setError(
                    "Mobile is required"
            );

            binding.etMobile.requestFocus();

            return;
        }

        /*
         * Create request.
         */
        UpdateProfileRequest request =
                new UpdateProfileRequest();

        request.setEmail(email);
        request.setMobile(mobile);
        request.setAddress(address);
        request.setCity(city);
        request.setState(state);
        request.setPincode(pincode);
        request.setOccupation(occupation);

        /*
         * Start update operation.
         */
        updateInProgress = true;

        setControlsEnabled(false);

        /*
         * STEP 1:
         *
         * Update normal profile information.
         */
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

                                    Toast.makeText(
                                            EditProfileActivity.this,
                                            getHttpErrorMessage(
                                                    response,
                                                    "Profile update failed."
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                /*
                                 * Text profile update succeeded.
                                 *
                                 * If the user selected a new photo,
                                 * upload it now.
                                 */
                                if (selectedPhotoUri != null) {

                                    uploadSelectedPhoto();

                                } else {

                                    /*
                                     * Nothing else to upload.
                                     */
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

    /**
     * Uploads selected profile photo.
     *
     * Backend:
     *
     * POST /profile/photo
     *
     * Multipart field:
     *
     * photo
     */
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

        /*
         * Determine MIME type.
         */
        String mimeType =
                getContentResolver().getType(
                        selectedPhotoUri
                );

        /*
         * Some document providers don't return
         * a MIME type.
         */
        if (mimeType == null
                || mimeType.trim().isEmpty()) {

            mimeType = detectMimeType(
                    selectedPhotoUri
            );
        }

        /*
         * Final fallback.
         */
        if (mimeType == null
                || mimeType.trim().isEmpty()) {

            mimeType = "image/jpeg";
        }

        /*
         * Normalize MIME type.
         */
        mimeType =
                mimeType.trim().toLowerCase();

        /*
         * Android-side validation.
         *
         * Backend performs final validation too.
         */
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

        /*
         * Validate selected file size.
         */
        long fileSize =
                getFileSize(selectedPhotoUri);

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

        /*
         * Create streaming RequestBody.
         */
        RequestBody requestBody =
                createImageRequestBody(
                        selectedPhotoUri,
                        mimeType
                );

        /*
         * Backend expects multipart field:
         *
         * photo
         */
        MultipartBody.Part photoPart =
                MultipartBody.Part.createFormData(
                        "photo",
                        "profile.jpg",
                        requestBody
                );

        /*
         * STEP 2:
         *
         * Upload photo.
         */
        repository.uploadProfilePhoto(photoPart)
                .enqueue(
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

                                    /*
                                     * Text profile update already
                                     * succeeded.
                                     *
                                     * Only photo upload failed.
                                     */
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

                                /*
                                 * Text profile update already succeeded.
                                 */
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

    /**
     * Creates a streaming RequestBody.
     *
     * ContentResolver
     *       ->
     * InputStream
     *       ->
     * BufferedSink
     */
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

                    /*
                     * Open selected image through
                     * Android ContentResolver.
                     */
                    inputStream =
                            getContentResolver()
                                    .openInputStream(uri);

                    if (inputStream == null) {

                        throw new IOException(
                                "Unable to open selected image."
                        );
                    }

                    /*
                     * Stream image in chunks.
                     */
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

    /**
     * Returns selected file size.
     *
     * Returns -1 if the provider does not expose size.
     */
    private long getFileSize(Uri uri) {

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

            /*
             * Unknown size.
             */

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return -1;
    }

    /**
     * Attempts to determine MIME type from
     * file extension.
     */
    private String detectMimeType(Uri uri) {

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

    /**
     * Enables/disables all photo/profile controls
     * while an operation is running.
     */
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

    /**
     * Safely converts null String to empty String.
     */
    private String safeString(
            String value
    ) {

        return value != null
                ? value
                : "";
    }

    /**
     * Builds a useful HTTP error message.
     */
    private String getHttpErrorMessage(
            Response<?> response,
            String fallback
    ) {

        if (response == null) {
            return fallback;
        }

        String message =
                "HTTP " + response.code();

        /*
         * Try to include server error body.
         */
        try {

            if (response.errorBody() != null) {

                String error =
                        response.errorBody()
                                .string();

                if (error != null
                        && !error.trim().isEmpty()) {

                    /*
                     * Keep error reasonably short.
                     */
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

    /**
     * Returns a safe network error message.
     */
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

    @Override
    protected void onDestroy() {

        super.onDestroy();

        /*
         * Release ViewBinding reference.
         */
        binding = null;
    }
}