package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
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

    private void initializeViews() {

        recyclerView = findViewById(R.id.recyclerViewMaturedRD);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        maturedList = new ArrayList<>();

        adapter = new RDAdapter(
                this,
                maturedList
        );

        recyclerView.setAdapter(adapter);

        repository = new RecurringDepositRepository(this);

    }

    private void loadMaturedDeposits() {

        repository.getMaturedRecurringDeposits()
                .enqueue(new Callback<List<RDResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<RDResponse>> call,
                            Response<List<RDResponse>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            maturedList.clear();

                            maturedList.addAll(response.body());

                            adapter.notifyDataSetChanged();

                        } else {

                            Toast.makeText(
                                    MaturedRDActivity.this,
                                    "No Matured RD Found",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<List<RDResponse>> call,
                            Throwable t) {

                        Toast.makeText(
                                MaturedRDActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}