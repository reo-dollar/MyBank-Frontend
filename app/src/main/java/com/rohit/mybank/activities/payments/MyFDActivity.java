package com.rohit.mybank.activities.payments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import com.rohit.mybank.R;
import com.rohit.mybank.api.ApiService;
import com.rohit.mybank.api.RetrofitClient;
import com.rohit.mybank.model.fixeddeposit.MyFixedDepositResponse;
import com.rohit.mybank.adapter.MyFDAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyFDActivity extends AppCompatActivity {


    //==================================================
    // Views
    //==================================================

    private MaterialToolbar toolbar;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private TextView tvEmpty;

    private TextView tvSubtitle;


    //==================================================
    // Adapter
    //==================================================

    private MyFDAdapter adapter;


    //==================================================
    // API
    //==================================================

    private ApiService apiService;


    //==================================================
    // ON CREATE
    //==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_my_fd
        );


        //==================================================
        // Initialize Views
        //==================================================

        toolbar =
                findViewById(
                        R.id.toolbar
                );

        recyclerView =
                findViewById(
                        R.id.recyclerMyFd
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        tvEmpty =
                findViewById(
                        R.id.tvEmpty
                );

        tvSubtitle =
                findViewById(
                        R.id.tvSubtitle
                );


        //==================================================
        // Toolbar
        //==================================================

        toolbar.setNavigationOnClickListener(
                v -> finish()
        );


        //==================================================
        // RecyclerView
        //==================================================

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setHasFixedSize(false);


        adapter =
                new MyFDAdapter();

        recyclerView.setAdapter(
                adapter
        );


        //==================================================
        // Retrofit
        //==================================================

        apiService =
                RetrofitClient
                        .getClient(this)
                        .create(ApiService.class);


        //==================================================
        // Load My FD
        //==================================================

        loadMyFixedDeposits();

    }


    //==================================================
    // LOAD MY FIXED DEPOSITS
    //==================================================

    private void loadMyFixedDeposits() {

        showLoading(true);


        apiService
                .getMyFixedDeposits()
                .enqueue(
                        new Callback<List<MyFixedDepositResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<List<MyFixedDepositResponse>> call,
                                    Response<List<MyFixedDepositResponse>> response) {

                                showLoading(false);


                                if (response.isSuccessful()
                                        && response.body() != null) {

                                    List<MyFixedDepositResponse> fdList =
                                            response.body();


                                    if (fdList.isEmpty()) {

                                        showEmptyState();

                                    } else {

                                        showFdList(
                                                fdList
                                        );

                                    }

                                } else {

                                    showError(
                                            "Unable to load your fixed deposits."
                                    );

                                }

                            }


                            @Override
                            public void onFailure(
                                    Call<List<MyFixedDepositResponse>> call,
                                    Throwable t) {

                                showLoading(false);

                                showError(
                                        "Network error. Please try again."
                                );

                            }

                        }
                );

    }


    //==================================================
    // LOADING
    //==================================================

    private void showLoading(
            boolean loading
    ) {

        if (loading) {

            progressBar.setVisibility(
                    View.VISIBLE
            );

            recyclerView.setVisibility(
                    View.GONE
            );

            tvEmpty.setVisibility(
                    View.GONE
            );

        } else {

            progressBar.setVisibility(
                    View.GONE
            );

        }

    }


    //==================================================
    // SHOW FD LIST
    //==================================================

    private void showFdList(
            List<MyFixedDepositResponse> fdList
    ) {

        recyclerView.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setVisibility(
                View.GONE
        );

        tvSubtitle.setText(
                fdList.size()
                        + " Fixed Deposit"
                        + (fdList.size() == 1 ? "" : "s")
        );

        adapter.setFdList(
                fdList
        );

    }


    //==================================================
    // EMPTY STATE
    //==================================================

    private void showEmptyState() {

        recyclerView.setVisibility(
                View.GONE
        );

        tvEmpty.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setText(
                "You don't have any Fixed Deposits yet."
        );

        tvSubtitle.setText(
                "Your FD investments will appear here."
        );

    }


    //==================================================
    // ERROR
    //==================================================

    private void showError(
            String message
    ) {

        recyclerView.setVisibility(
                View.GONE
        );

        tvEmpty.setVisibility(
                View.VISIBLE
        );

        tvEmpty.setText(
                message
        );

        tvSubtitle.setText(
                "Please check your connection and try again."
        );

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();

    }

}