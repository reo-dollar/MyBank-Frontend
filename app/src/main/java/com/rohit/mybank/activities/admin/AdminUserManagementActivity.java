package com.rohit.mybank.activities.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.activities.auth.LoginActivity;
import com.rohit.mybank.model.admin.AdminUserResponse;
import com.rohit.mybank.repository.AdminUserRepository;
import com.rohit.mybank.session.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * =========================================================
 * ADMIN USER MANAGEMENT ACTIVITY
 * =========================================================
 *
 * Professional administration screen for managing users.
 */
public class AdminUserManagementActivity
        extends AppCompatActivity {

    // =====================================================
    // COLORS
    // =====================================================

    private static final int COLOR_BACKGROUND =
            Color.rgb(244, 247, 251);

    private static final int COLOR_WHITE =
            Color.WHITE;

    private static final int COLOR_PRIMARY =
            Color.rgb(37, 99, 235);

    private static final int COLOR_PRIMARY_DARK =
            Color.rgb(17, 24, 39);

    private static final int COLOR_LABEL =
            Color.rgb(71, 85, 105);

    private static final int COLOR_SECONDARY =
            Color.rgb(100, 116, 139);

    private static final int COLOR_BORDER =
            Color.rgb(226, 232, 240);

    private static final int COLOR_ACTIVE =
            Color.rgb(22, 163, 74);

    private static final int COLOR_DISABLED =
            Color.rgb(220, 38, 38);

    private static final int COLOR_LOCKED =
            Color.rgb(234, 88, 12);

    private static final int COLOR_DISABLE_BUTTON =
            Color.rgb(37, 99, 235);

    private static final int COLOR_LOCK_BUTTON =
            Color.rgb(220, 38, 38);

    // =====================================================
    // VIEWS
    // =====================================================

    private EditText etSearchUsers;

    private TextView tvUserCount;

    private ProgressBar progressBar;

    private LinearLayout usersContainer;

    // =====================================================
    // REPOSITORY
    // =====================================================

    private AdminUserRepository adminUserRepository;

    private SessionManager sessionManager;

    // =====================================================
    // DATA
    // =====================================================

    private List<AdminUserResponse> allUsers =
            new ArrayList<>();

    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_user_management
        );

        initializeViews();

        adminUserRepository =
                new AdminUserRepository(this);

        sessionManager =
                new SessionManager(this);

        setupSearch();

        loadUsers();
    }

    // =====================================================
    // INITIALIZE VIEWS
    // =====================================================

    private void initializeViews() {

        etSearchUsers =
                findViewById(
                        R.id.etSearchUsers
                );

        tvUserCount =
                findViewById(
                        R.id.tvUserCount
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        usersContainer =
                findViewById(
                        R.id.usersContainer
                );
    }

    // =====================================================
    // LOAD USERS
    // =====================================================

    private void loadUsers() {

        showLoading(true);

        adminUserRepository
                .getAllUsers()
                .enqueue(
                        new Callback<List<AdminUserResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<AdminUserResponse>> call,
                                    Response<List<AdminUserResponse>> response) {

                                showLoading(false);

                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    allUsers =
                                            response.body();

                                    displayUsers(
                                            allUsers
                                    );

                                } else {

                                    handleHttpError(
                                            response.code()
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<List<AdminUserResponse>> call,
                                    Throwable t) {

                                showLoading(false);

                                t.printStackTrace();

                                Toast.makeText(
                                        AdminUserManagementActivity.this,
                                        "Unable to connect to server.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =====================================================
    // DISPLAY USERS
    // =====================================================

    private void displayUsers(
            List<AdminUserResponse> users) {

        usersContainer.removeAllViews();

        tvUserCount.setText(
                users.size()
                        + (users.size() == 1
                        ? " user"
                        : " users")
        );

        if (users.isEmpty()) {

            TextView emptyView =
                    createTextView(
                            "No users found.",
                            16,
                            false
                    );

            emptyView.setGravity(
                    Gravity.CENTER
            );

            emptyView.setTextColor(
                    COLOR_SECONDARY
            );

            emptyView.setPadding(
                    20,
                    60,
                    20,
                    60
            );

            usersContainer.addView(
                    emptyView
            );

            return;
        }

        for (AdminUserResponse user : users) {

            addUserCard(user);
        }
    }

    // =====================================================
    // USER CARD
    // =====================================================

    private void addUserCard(
            AdminUserResponse user) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                20,
                20,
                20
        );

        /*
         * White card with subtle border.
         */
        card.setBackground(
                createCardBackground()
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                16
        );

        card.setLayoutParams(
                cardParams
        );

        // =================================================
        // USERNAME
        // =================================================

        TextView username =
                createTextView(
                        "Username: "
                                + safe(user.getUsername()),
                        19,
                        true
                );

        username.setTextColor(
                COLOR_PRIMARY_DARK
        );

        username.setPadding(
                0,
                0,
                0,
                12
        );

        card.addView(username);

        // =================================================
        // NAME
        // =================================================

        card.addView(
                createInfoRow(
                        "Name:",
                        safe(user.getFullName())
                )
        );

        // =================================================
        // CUSTOMER ID
        // =================================================

        card.addView(
                createInfoRow(
                        "Customer ID:",
                        safe(user.getCustomerId())
                )
        );

        // =================================================
        // EMAIL
        // =================================================

        card.addView(
                createInfoRow(
                        "Email:",
                        safe(user.getEmail())
                )
        );

        // =================================================
        // MOBILE
        // =================================================

        if (user.getMobile() != null
                && !user.getMobile().trim().isEmpty()) {

            card.addView(
                    createInfoRow(
                            "Mobile:",
                            user.getMobile()
                    )
            );
        }

        // =================================================
        // ROLE
        // =================================================

        card.addView(
                createInfoRow(
                        "Role:",
                        safe(user.getRole())
                )
        );

        // =================================================
        // STATUS
        // =================================================

        LinearLayout statusRow =
                createInfoRowContainer();

        TextView statusLabel =
                createLabelTextView(
                        "Status:"
                );

        TextView statusValue =
                createValueTextView(
                        getStatus(user)
                );

        statusValue.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        statusValue.setTextColor(
                getStatusColor(user)
        );

        statusRow.addView(
                statusLabel
        );

        statusRow.addView(
                statusValue
        );

        card.addView(
                statusRow
        );

        // =================================================
        // BUTTON CONTAINER
        // =================================================

        LinearLayout buttonContainer =
                new LinearLayout(this);

        buttonContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        buttonContainer.setPadding(
                0,
                16,
                0,
                0
        );

        // =================================================
        // ENABLE / DISABLE BUTTON
        // =================================================

        Button enableDisableButton =
                createActionButton();

        if (user.isEnabled()) {

            enableDisableButton.setText(
                    "DISABLE USER"
            );

            enableDisableButton.setBackground(
                    createRoundedBackground(
                            COLOR_DISABLE_BUTTON,
                            12
                    )
            );

            enableDisableButton.setOnClickListener(
                    v -> showConfirmationDialog(
                            "Disable User",
                            "Are you sure you want to disable user \""
                                    + safe(user.getUsername())
                                    + "\"?",
                            () -> disableUser(user)
                    )
            );

        } else {

            enableDisableButton.setText(
                    "ENABLE USER"
            );

            enableDisableButton.setBackground(
                    createRoundedBackground(
                            COLOR_ACTIVE,
                            12
                    )
            );

            enableDisableButton.setOnClickListener(
                    v -> showConfirmationDialog(
                            "Enable User",
                            "Are you sure you want to enable user \""
                                    + safe(user.getUsername())
                                    + "\"?",
                            () -> enableUser(user)
                    )
            );
        }

        buttonContainer.addView(
                enableDisableButton
        );

        // =================================================
        // LOCK / UNLOCK BUTTON
        // =================================================

        Button lockButton =
                createActionButton();

        LinearLayout.LayoutParams lockParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        52
                );

        lockParams.setMargins(
                0,
                10,
                0,
                0
        );

        lockButton.setLayoutParams(
                lockParams
        );

        if (user.isAccountLocked()) {

            lockButton.setText(
                    "UNLOCK ACCOUNT"
            );

            lockButton.setBackground(
                    createRoundedBackground(
                            COLOR_ACTIVE,
                            12
                    )
            );

            lockButton.setOnClickListener(
                    v -> showConfirmationDialog(
                            "Unlock Account",
                            "Are you sure you want to unlock account \""
                                    + safe(user.getUsername())
                                    + "\"?",
                            () -> unlockUser(user)
                    )
            );

        } else {

            lockButton.setText(
                    "LOCK ACCOUNT"
            );

            lockButton.setBackground(
                    createRoundedBackground(
                            COLOR_LOCK_BUTTON,
                            12
                    )
            );

            lockButton.setOnClickListener(
                    v -> showConfirmationDialog(
                            "Lock Account",
                            "Are you sure you want to lock account \""
                                    + safe(user.getUsername())
                                    + "\"?",
                            () -> lockUser(user)
                    )
            );
        }

        buttonContainer.addView(
                lockButton
        );

        card.addView(
                buttonContainer
        );

        usersContainer.addView(
                card
        );
    }

    // =====================================================
    // INFO ROW
    // =====================================================

    private LinearLayout createInfoRow(
            String label,
            String value) {

        LinearLayout row =
                createInfoRowContainer();

        TextView labelView =
                createLabelTextView(
                        label
                );

        TextView valueView =
                createValueTextView(
                        value
                );

        row.addView(
                labelView
        );

        row.addView(
                valueView
        );

        return row;
    }

    // =====================================================
    // INFO ROW CONTAINER
    // =====================================================

    private LinearLayout createInfoRowContainer() {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                0,
                5,
                0,
                5
        );

        return row;
    }

    // =====================================================
    // LABEL TEXT
    // =====================================================

    private TextView createLabelTextView(
            String text) {

        TextView textView =
                createTextView(
                        text,
                        14,
                        false
                );

        textView.setTextColor(
                COLOR_LABEL
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        110,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textView.setLayoutParams(
                params
        );

        return textView;
    }

    // =====================================================
    // VALUE TEXT
    // =====================================================

    private TextView createValueTextView(
            String text) {

        TextView textView =
                createTextView(
                        text,
                        15,
                        false
                );

        textView.setTextColor(
                COLOR_PRIMARY_DARK
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        textView.setLayoutParams(
                params
        );

        return textView;
    }

    // =====================================================
    // GENERIC TEXT VIEW
    // =====================================================

    private TextView createTextView(
            String text,
            float textSize,
            boolean bold) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(
                textSize
        );

        textView.setTextColor(
                COLOR_PRIMARY_DARK
        );

        textView.setAlpha(
                1.0f
        );

        if (bold) {

            textView.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return textView;
    }

    // =====================================================
    // ACTION BUTTON
    // =====================================================

    private Button createActionButton() {

        Button button =
                new Button(this);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        52
                );

        button.setLayoutParams(
                params
        );

        button.setTextColor(
                Color.WHITE
        );

        button.setTextSize(
                14
        );

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setGravity(
                Gravity.CENTER
        );

        button.setAllCaps(
                false
        );

        button.setPadding(
                12,
                0,
                12,
                0
        );

        button.setAlpha(
                1.0f
        );

        return button;
    }

    // =====================================================
    // CARD BACKGROUND
    // =====================================================

    private GradientDrawable createCardBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                COLOR_WHITE
        );

        drawable.setCornerRadius(
                18
        );

        drawable.setStroke(
                1,
                COLOR_BORDER
        );

        return drawable;
    }

    // =====================================================
    // ROUNDED BACKGROUND
    // =====================================================

    private GradientDrawable createRoundedBackground(
            int color,
            int radius) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                color
        );

        drawable.setCornerRadius(
                radius
        );

        return drawable;
    }

    // =====================================================
    // CONFIRMATION DIALOG
    // =====================================================

    private void showConfirmationDialog(
            String title,
            String message,
            Runnable action) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "CONFIRM",
                        (dialog, which) ->
                                action.run()
                )
                .show();
    }

    // =====================================================
    // ENABLE USER
    // =====================================================

    private void enableUser(
            AdminUserResponse user) {

        showLoading(true);

        adminUserRepository
                .enableUser(
                        user.getUsername()
                )
                .enqueue(
                        createStatusCallback(
                                "User enabled successfully."
                        )
                );
    }

    // =====================================================
    // DISABLE USER
    // =====================================================

    private void disableUser(
            AdminUserResponse user) {

        showLoading(true);

        adminUserRepository
                .disableUser(
                        user.getUsername()
                )
                .enqueue(
                        createStatusCallback(
                                "User disabled successfully."
                        )
                );
    }

    // =====================================================
    // LOCK USER
    // =====================================================

    private void lockUser(
            AdminUserResponse user) {

        showLoading(true);

        adminUserRepository
                .lockUser(
                        user.getUsername()
                )
                .enqueue(
                        createStatusCallback(
                                "Account locked successfully."
                        )
                );
    }

    // =====================================================
    // UNLOCK USER
    // =====================================================

    private void unlockUser(
            AdminUserResponse user) {

        showLoading(true);

        adminUserRepository
                .unlockUser(
                        user.getUsername()
                )
                .enqueue(
                        createStatusCallback(
                                "Account unlocked successfully."
                        )
                );
    }

    // =====================================================
    // COMMON CALLBACK
    // =====================================================

    private Callback<AdminUserResponse>
    createStatusCallback(
            String successMessage) {

        return new Callback<AdminUserResponse>() {

            @Override
            public void onResponse(
                    Call<AdminUserResponse> call,
                    Response<AdminUserResponse> response) {

                showLoading(false);

                if (response.isSuccessful()) {

                    Toast.makeText(
                            AdminUserManagementActivity.this,
                            successMessage,
                            Toast.LENGTH_SHORT
                    ).show();

                    loadUsers();

                } else {

                    handleHttpError(
                            response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<AdminUserResponse> call,
                    Throwable t) {

                showLoading(false);

                t.printStackTrace();

                Toast.makeText(
                        AdminUserManagementActivity.this,
                        "Unable to connect to server.",
                        Toast.LENGTH_LONG
                ).show();
            }
        };
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void setupSearch() {

        etSearchUsers.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterUsers(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    // =====================================================
    // FILTER USERS
    // =====================================================

    private void filterUsers(
            String query) {

        String search =
                query
                        .trim()
                        .toLowerCase(
                                Locale.getDefault()
                        );

        if (search.isEmpty()) {

            displayUsers(
                    allUsers
            );

            return;
        }

        List<AdminUserResponse> filteredUsers =
                new ArrayList<>();

        for (AdminUserResponse user : allUsers) {

            if (contains(
                    user.getUsername(),
                    search
            )
                    || contains(
                    user.getFullName(),
                    search
            )
                    || contains(
                    user.getCustomerId(),
                    search
            )
                    || contains(
                    user.getEmail(),
                    search
            )
                    || contains(
                    user.getMobile(),
                    search
            )
                    || contains(
                    user.getRole(),
                    search
            )) {

                filteredUsers.add(
                        user
                );
            }
        }

        displayUsers(
                filteredUsers
        );
    }

    // =====================================================
    // SEARCH HELPER
    // =====================================================

    private boolean contains(
            String value,
            String query) {

        return value != null
                && value
                .toLowerCase(
                        Locale.getDefault()
                )
                .contains(query);
    }

    // =====================================================
    // STATUS
    // =====================================================

    private String getStatus(
            AdminUserResponse user) {

        if (!user.isEnabled()) {

            return "DISABLED";
        }

        if (user.isAccountLocked()) {

            return "LOCKED";
        }

        return "ACTIVE";
    }

    // =====================================================
    // STATUS COLOR
    // =====================================================

    private int getStatusColor(
            AdminUserResponse user) {

        if (!user.isEnabled()) {

            return COLOR_DISABLED;
        }

        if (user.isAccountLocked()) {

            return COLOR_LOCKED;
        }

        return COLOR_ACTIVE;
    }

    // =====================================================
    // SAFE STRING
    // =====================================================

    private String safe(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "N/A";
        }

        return value;
    }

    // =====================================================
    // LOADING
    // =====================================================

    private void showLoading(
            boolean loading) {

        if (progressBar == null) {
            return;
        }

        progressBar.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    // =====================================================
    // HTTP ERROR
    // =====================================================

    private void handleHttpError(
            int code) {

        if (code == 401) {

            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            sessionManager.logout();

            Intent intent =
                    new Intent(
                            this,
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();

            return;
        }

        if (code == 403) {

            Toast.makeText(
                    this,
                    "Access denied. Administrator privileges required.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (code == 404) {

            Toast.makeText(
                    this,
                    "User Management API was not found.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Request failed. HTTP " + code,
                Toast.LENGTH_LONG
        ).show();
    }

    // =====================================================
    // ON RESUME
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (adminUserRepository != null) {

            loadUsers();
        }
    }
}