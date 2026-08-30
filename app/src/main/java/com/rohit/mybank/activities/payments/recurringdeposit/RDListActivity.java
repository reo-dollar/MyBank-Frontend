package com.rohit.mybank.activities.payments.recurringdeposit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.adapter.RDAdapter;
import com.rohit.mybank.model.recurringdeposit.RDResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RDListActivity extends AppCompatActivity {

    private static final String TAG = "RD_LIST";

    // =========================================================
    // ACTION CONSTANTS
    // =========================================================

    public static final String ACTION_PAY_INSTALLMENT =
            "PAY_INSTALLMENT";

    public static final String ACTION_HISTORY =
            "HISTORY";

    public static final String ACTION_PREMATURE_CLOSE =
            "PREMATURE_CLOSE";

    public static final String ACTION_DEFAULT =
            "DEFAULT";

    // =========================================================
    // VIEWS
    // =========================================================

    private RecyclerView recyclerView;

    // =========================================================
    // ADAPTER / DATA
    // =========================================================

    private RDAdapter adapter;

    private List<RDResponse> rdList;

    // =========================================================
    // REPOSITORY
    // =========================================================

    private RecurringDepositRepository repository;

    // =========================================================
    // CURRENT ACTION
    // =========================================================

    private String action;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_rd_list
        );

        // -----------------------------------------------------
        // Read action from dashboard
        // -----------------------------------------------------

        readAction();

        initializeViews();

        loadRecurringDeposits();
    }

    // =========================================================
    // READ ACTION
    // =========================================================

    private void readAction() {

        action = getIntent().getStringExtra("ACTION");

        if (action == null
                || action.trim().isEmpty()) {

            action = ACTION_DEFAULT;
        }

        Log.d(
                TAG,
                "Current Action = " + action
        );
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        recyclerView =
                findViewById(
                        R.id.recyclerViewRD
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rdList =
                new ArrayList<>();

        /*
         * IMPORTANT:
         *
         * Pass the current action to RDAdapter.
         *
         * The adapter will decide what Activity
         * should open when an RD is selected.
         */

        adapter =
                new RDAdapter(
                        this,
                        rdList,
                        action
                );

        recyclerView.setAdapter(adapter);

        repository =
                new RecurringDepositRepository(this);
    }

    // =========================================================
    // LOAD MY RD
    // =========================================================

    private void loadRecurringDeposits() {

        repository
                .getMyRecurringDeposits()
                .enqueue(
                        new Callback<List<RDResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<RDResponse>> call,
                                    Response<List<RDResponse>> response) {

                                Log.d(
                                        TAG,
                                        "HTTP Code : "
                                                + response.code()
                                );

                                // =================================
                                // ERROR BODY
                                // =================================

                                if (response.errorBody() != null) {

                                    try {

                                        Log.e(
                                                TAG,
                                                "Error Body : "
                                                        + response
                                                        .errorBody()
                                                        .string()
                                        );

                                    } catch (Exception e) {

                                        Log.e(
                                                TAG,
                                                "Unable to read error body",
                                                e
                                        );
                                    }
                                }

                                // =================================
                                // HTTP ERROR
                                // =================================

                                if (!response.isSuccessful()) {

                                    Toast.makeText(
                                            RDListActivity.this,
                                            "Server Error : "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // =================================
                                // RESPONSE BODY
                                // =================================

                                List<RDResponse> list =
                                        response.body();

                                if (list == null) {

                                    Log.d(
                                            TAG,
                                            "Response body is NULL"
                                    );

                                    Toast.makeText(
                                            RDListActivity.this,
                                            "Response Body is NULL",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // =================================
                                // UPDATE LIST
                                // =================================

                                Log.d(
                                        TAG,
                                        "Total Records = "
                                                + list.size()
                                );

                                rdList.clear();

                                rdList.addAll(list);

                                adapter.notifyDataSetChanged();

                                // =================================
                                // EMPTY / SUCCESS MESSAGE
                                // =================================

                                if (list.isEmpty()) {

                                    Toast.makeText(
                                            RDListActivity.this,
                                            "No Recurring Deposits Found",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Log.d(
                                            TAG,
                                            list.size()
                                                    + " RD loaded"
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<List<RDResponse>> call,
                                    Throwable t) {

                                Log.e(
                                        TAG,
                                        "Network Failure",
                                        t
                                );

                                String message =
                                        t.getMessage();

                                if (message == null
                                        || message.trim().isEmpty()) {

                                    message =
                                            "Unable to load Recurring Deposits.";
                                }

                                Toast.makeText(
                                        RDListActivity.this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}