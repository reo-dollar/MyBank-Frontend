package com.rohit.mybank.activities.admin;

import android.content.Intent;
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
import androidx.core.content.ContextCompat;

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
 *
 * UI supports:
 * - Light mode
 * - Dark mode
 * - Dynamic user cards
 * - Search
 * - Enable / Disable user
 * - Lock / Unlock account
 *
 * Existing API functionality is preserved.
 */
public class AdminUserManagementActivity
        extends AppCompatActivity {


    // =========================================================
    // VIEWS
    // =========================================================

    private EditText etSearchUsers;

    private TextView tvUserCount;

    private ProgressBar progressBar;

    private LinearLayout usersContainer;


    // =========================================================
    // REPOSITORY
    // =========================================================

    private AdminUserRepository adminUserRepository;

    private SessionManager sessionManager;


    // =========================================================
    // DATA
    // =========================================================

    private List<AdminUserResponse> allUsers =
            new ArrayList<>();


    // =========================================================
    // ON CREATE
    // =========================================================

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


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

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


    // =========================================================
    // LOAD USERS
    // =========================================================

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


    // =========================================================
    // DISPLAY USERS
    // =========================================================

    private void displayUsers(
            List<AdminUserResponse> users) {

        usersContainer.removeAllViews();

        tvUserCount.setText(
                users.size()
                        + (users.size() == 1
                        ? " user"
                        : " users")
        );


        // =====================================================
        // EMPTY STATE
        // =====================================================

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
                    color(R.color.admin_text_secondary)
            );

            emptyView.setPadding(
                    dp(20),
                    dp(60),
                    dp(20),
                    dp(60)
            );

            usersContainer.addView(
                    emptyView
            );

            return;
        }


        // =====================================================
        // ADD USER CARDS
        // =====================================================

        for (AdminUserResponse user : users) {

            addUserCard(user);
        }
    }


    // =========================================================
    // USER CARD
    // =========================================================

    private void addUserCard(
            AdminUserResponse user) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        /*
         * Theme-aware card background.
         *
         * Light mode:
         * admin_card = white
         *
         * Dark mode:
         * admin_card = dark surface
         */
        card.setBackground(
                createCardBackground()
        );


        // =====================================================
        // CARD LAYOUT PARAMETERS
        // =====================================================

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(12)
        );

        card.setLayoutParams(
                cardParams
        );


        // =====================================================
        // USERNAME
        // =====================================================

        TextView username =
                createTextView(
                        "Username: "
                                + safe(user.getUsername()),
                        17,
                        true
                );

        username.setTextColor(
                color(R.color.admin_text_primary)
        );

        username.setPadding(
                0,
                0,
                0,
                dp(8)
        );

        card.addView(username);


        // =====================================================
        // NAME
        // =====================================================

        card.addView(
                createInfoRow(
                        "Name:",
                        safe(user.getFullName())
                )
        );


        // =====================================================
        // CUSTOMER ID
        // =====================================================

        card.addView(
                createInfoRow(
                        "Customer ID:",
                        safe(user.getCustomerId())
                )
        );


        // =====================================================
        // EMAIL
        // =====================================================

        card.addView(
                createInfoRow(
                        "Email:",
                        safe(user.getEmail())
                )
        );


        // =====================================================
        // MOBILE
        // =====================================================

        if (user.getMobile() != null
                && !user.getMobile().trim().isEmpty()) {

            card.addView(
                    createInfoRow(
                            "Mobile:",
                            user.getMobile()
                    )
            );
        }


        // =====================================================
        // ROLE
        // =====================================================

        card.addView(
                createInfoRow(
                        "Role:",
                        safe(user.getRole())
                )
        );


        // =====================================================
        // STATUS
        // =====================================================

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


        // =====================================================
        // BUTTON CONTAINER
        // =====================================================

        LinearLayout buttonContainer =
                new LinearLayout(this);

        buttonContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams buttonContainerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonContainerParams.topMargin =
                dp(10);

        buttonContainer.setLayoutParams(
                buttonContainerParams
        );


        // =====================================================
        // ENABLE / DISABLE BUTTON
        // =====================================================

        Button enableDisableButton =
                createActionButton();


        if (user.isEnabled()) {

            enableDisableButton.setText(
                    "DISABLE USER"
            );

            enableDisableButton.setBackground(
                    createRoundedBackground(
                            color(R.color.admin_primary),
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
                            color(R.color.admin_success),
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


        // =====================================================
        // LOCK / UNLOCK BUTTON
        // =====================================================

        Button lockButton =
                createActionButton();

        LinearLayout.LayoutParams lockParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(44)
                );

        lockParams.setMargins(
                0,
                dp(8),
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
                            color(R.color.admin_success),
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
                            color(R.color.admin_error),
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


        // =====================================================
        // ADD CARD TO CONTAINER
        // =====================================================

        usersContainer.addView(
                card
        );
    }


    // =========================================================
    // INFO ROW
    // =========================================================

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


    // =========================================================
    // INFO ROW CONTAINER
    // =========================================================

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
                dp(4),
                0,
                dp(4)
        );

        return row;
    }


    // =========================================================
    // LABEL TEXT
    // =========================================================

    private TextView createLabelTextView(
            String text) {

        TextView textView =
                createTextView(
                        text,
                        13,
                        false
                );

        textView.setTextColor(
                color(R.color.admin_text_secondary)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        dp(100),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textView.setLayoutParams(
                params
        );

        return textView;
    }


    // =========================================================
    // VALUE TEXT
    // =========================================================

    private TextView createValueTextView(
            String text) {

        TextView textView =
                createTextView(
                        text,
                        14,
                        false
                );

        textView.setTextColor(
                color(R.color.admin_text_primary)
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

        /*
         * Allow long values such as:
         * - email
         * - customer ID
         * - mobile number
         */
        textView.setMaxLines(2);

        return textView;
    }


    // =========================================================
    // GENERIC TEXT VIEW
    // =========================================================

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
                color(R.color.admin_text_primary)
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


    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private Button createActionButton() {

        Button button =
                new Button(this);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(44)
                );

        button.setLayoutParams(
                params
        );

        button.setTextColor(
                color(R.color.white)
        );

        button.setTextSize(
                13
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
                dp(8),
                0,
                dp(8),
                0
        );

        /*
         * Remove the default Button minimum inset/padding
         * so our custom rounded background looks clean.
         */
        button.setMinHeight(0);
        button.setMinWidth(0);

        button.setAlpha(
                1.0f
        );

        return button;
    }


    // =========================================================
    // CARD BACKGROUND
    // =========================================================

    private GradientDrawable createCardBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        /*
         * IMPORTANT:
         *
         * Do NOT use Color.WHITE here.
         *
         * admin_card automatically resolves to:
         *
         * Light → #FFFFFF
         * Dark  → #111827
         */
        drawable.setColor(
                color(R.color.admin_card)
        );

        drawable.setCornerRadius(
                dp(16)
        );

        drawable.setStroke(
                dp(1),
                color(R.color.admin_divider)
        );

        return drawable;
    }


    // =========================================================
    // ROUNDED BACKGROUND
    // =========================================================

    private GradientDrawable createRoundedBackground(
            int backgroundColor,
            int radiusDp) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                backgroundColor
        );

        drawable.setCornerRadius(
                dp(radiusDp)
        );

        return drawable;
    }


    // =========================================================
    // CONFIRMATION DIALOG
    // =========================================================

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


    // =========================================================
    // ENABLE USER
    // =========================================================

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


    // =========================================================
    // DISABLE USER
    // =========================================================

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


    // =========================================================
    // LOCK USER
    // =========================================================

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


    // =========================================================
    // UNLOCK USER
    // =========================================================

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


    // =========================================================
    // COMMON CALLBACK
    // =========================================================

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


    // =========================================================
    // SEARCH
    // =========================================================

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


    // =========================================================
    // FILTER USERS
    // =========================================================

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


    // =========================================================
    // SEARCH HELPER
    // =========================================================

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


    // =========================================================
    // STATUS
    // =========================================================

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


    // =========================================================
    // STATUS COLOR
    // =========================================================

    private int getStatusColor(
            AdminUserResponse user) {

        if (!user.isEnabled()) {

            return color(
                    R.color.admin_error
            );
        }

        if (user.isAccountLocked()) {

            return color(
                    R.color.admin_orange_stroke
            );
        }

        return color(
                R.color.admin_success
        );
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "N/A";
        }

        return value;
    }


    // =========================================================
    // COLOR HELPER
    // =========================================================

    private int color(
            int colorResId) {

        return ContextCompat.getColor(
                this,
                colorResId
        );
    }


    // =========================================================
    // DP HELPER
    // =========================================================

    private int dp(
            int value) {

        return Math.round(
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }


    // =========================================================
    // LOADING
    // =========================================================

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


    // =========================================================
    // HTTP ERROR
    // =========================================================

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


    // =========================================================
    // ON RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (adminUserRepository != null) {

            loadUsers();
        }
    }
}