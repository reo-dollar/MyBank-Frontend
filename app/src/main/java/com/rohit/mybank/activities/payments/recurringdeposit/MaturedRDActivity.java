package com.rohit.mybank.activities.payments.recurringdeposit;

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

public class MaturedRDActivity extends AppCompatActivity {

    private static final String TAG = "MATURED_RD";

    private RecyclerView recyclerView;

    private RDAdapter adapter;

    private List<RDResponse> maturedList;

    private RecurringDepositRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_matured_rd);

        initializeViews();

        loadMaturedDeposits();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        recyclerView =
                findViewById(R.id.recyclerViewMaturedRD);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        maturedList =
                new ArrayList<>();

        adapter =
                new RDAdapter(
                        this,
                        maturedList,
                        RDListActivity.ACTION_DEFAULT
                );

        recyclerView.setAdapter(adapter);

        repository =
                new RecurringDepositRepository(this);
    }

    // =========================================================
    // LOAD MATURED RDs
    // =========================================================

    private void loadMaturedDeposits() {

        Log.d(
                TAG,
                "Calling matured RD API..."
        );

        repository
                .getMaturedRecurringDeposits()
                .enqueue(
                        new Callback<List<RDResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<RDResponse>> call,
                                    Response<List<RDResponse>> response) {

                                Log.d(
                                        TAG,
                                        "HTTP CODE = "
                                                + response.code()
                                );

                                Log.d(
                                        TAG,
                                        "SUCCESS = "
                                                + response.isSuccessful()
                                );

                                // =================================================
                                // SERVER ERROR
                                // =================================================

                                if (!response.isSuccessful()) {

                                    String errorBody =
                                            "";

                                    try {

                                        if (response.errorBody()
                                                != null) {

                                            errorBody =
                                                    response.errorBody()
                                                            .string();
                                        }

                                    } catch (Exception e) {

                                        Log.e(
                                                TAG,
                                                "Error reading error body",
                                                e
                                        );
                                    }

                                    Log.e(
                                            TAG,
                                            "SERVER ERROR = "
                                                    + errorBody
                                    );

                                    Toast.makeText(
                                            MaturedRDActivity.this,
                                            "Server Error HTTP "
                                                    + response.code(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // =================================================
                                // NULL BODY
                                // =================================================

                                List<RDResponse> responseList =
                                        response.body();

                                if (responseList == null) {

                                    Log.e(
                                            TAG,
                                            "Response body is NULL"
                                    );

                                    Toast.makeText(
                                            MaturedRDActivity.this,
                                            "Server returned NULL response",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // =================================================
                                // RESPONSE SIZE
                                // =================================================

                                Log.d(
                                        TAG,
                                        "MATURED RD COUNT = "
                                                + responseList.size()
                                );

                                // =================================================
                                // PRINT EVERY RD
                                // =================================================

                                for (RDResponse rd :
                                        responseList) {

                                    if (rd == null) {

                                        Log.e(
                                                TAG,
                                                "Received NULL RD object"
                                        );

                                        continue;
                                    }

                                    Log.d(
                                            TAG,
                                            "RD NUMBER = "
                                                    + rd.getRdNumber()
                                    );

                                    Log.d(
                                            TAG,
                                            "ACCOUNT NUMBER = "
                                                    + rd.getAccountNumber()
                                    );

                                    Log.d(
                                            TAG,
                                            "STATUS = "
                                                    + rd.getStatus()
                                    );

                                    Log.d(
                                            TAG,
                                            "CUSTOMER = "
                                                    + rd.getCustomerName()
                                    );

                                    Log.d(
                                            TAG,
                                            "MONTHLY INSTALLMENT = "
                                                    + rd.getMonthlyInstallment()
                                    );

                                    Log.d(
                                            TAG,
                                            "MATURITY AMOUNT = "
                                                    + rd.getMaturityAmount()
                                    );
                                }

                                // =================================================
                                // UPDATE RECYCLER VIEW
                                // =================================================

                                maturedList.clear();

                                maturedList.addAll(
                                        responseList
                                );

                                adapter.notifyDataSetChanged();

                                // =================================================
                                // EMPTY LIST
                                // =================================================

                                if (responseList.isEmpty()) {

                                    Log.w(
                                            TAG,
                                            "API returned EMPTY LIST []"
                                    );

                                    Toast.makeText(
                                            MaturedRDActivity.this,
                                            "API returned 0 matured RDs",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // =================================================
                                // SUCCESS
                                // =================================================

                                Toast.makeText(
                                        MaturedRDActivity.this,
                                        responseList.size()
                                                + " Matured RD Found",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            @Override
                            public void onFailure(
                                    Call<List<RDResponse>> call,
                                    Throwable t) {

                                Log.e(
                                        TAG,
                                        "API CALL FAILED",
                                        t
                                );

                                Toast.makeText(
                                        MaturedRDActivity.this,
                                        "Network Error\n\n"
                                                + t.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}