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

    private RecyclerView recyclerView;
    private RDAdapter adapter;
    private List<RDResponse> rdList;
    private RecurringDepositRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rd_list);

        initializeViews();

        loadRecurringDeposits();
    }

    private void initializeViews() {

        recyclerView = findViewById(R.id.recyclerViewRD);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rdList = new ArrayList<>();

        adapter = new RDAdapter(this, rdList);

        recyclerView.setAdapter(adapter);

        repository = new RecurringDepositRepository(this);
    }

    private void loadRecurringDeposits() {

        repository.getMyRecurringDeposits()
                .enqueue(new Callback<List<RDResponse>>() {

                    @Override
                    public void onResponse(Call<List<RDResponse>> call,
                                           Response<List<RDResponse>> response) {

                        Log.d(TAG, "HTTP Code : " + response.code());

                        if (response.errorBody() != null) {
                            try {
                                Log.e(TAG, "Error Body : "
                                        + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (!response.isSuccessful()) {

                            Toast.makeText(
                                    RDListActivity.this,
                                    "Server Error : " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        List<RDResponse> list = response.body();

                        if (list == null) {

                            Log.d(TAG, "Response body is NULL");

                            Toast.makeText(
                                    RDListActivity.this,
                                    "Response Body is NULL",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        Log.d(TAG, "Total Records = " + list.size());

                        rdList.clear();

                        rdList.addAll(list);

                        adapter.notifyDataSetChanged();

                        if (list.isEmpty()) {

                            Toast.makeText(
                                    RDListActivity.this,
                                    "No Recurring Deposits Found",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    RDListActivity.this,
                                    list.size() + " RD Found",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RDResponse>> call,
                                          Throwable t) {

                        Log.e(TAG, "Failure", t);

                        Toast.makeText(
                                RDListActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}