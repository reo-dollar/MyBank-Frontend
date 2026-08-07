package com.rohit.mybank.activities.payments.recurringdeposit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.mybank.R;
import com.rohit.mybank.adapter.RDHistoryAdapter;
import com.rohit.mybank.model.recurringdeposit.RDHistoryResponse;
import com.rohit.mybank.repository.RecurringDepositRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RDHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RDHistoryAdapter adapter;
    private List<RDHistoryResponse> historyList;

    private RecurringDepositRepository repository;

    private String rdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rd_history);

        repository = new RecurringDepositRepository(this);

        rdNumber = getIntent().getStringExtra("RD_NUMBER");

        if (TextUtils.isEmpty(rdNumber)) {

            Toast.makeText(
                    this,
                    "RD Number not received.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewHistory);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        historyList = new ArrayList<>();

        adapter = new RDHistoryAdapter(
                this,
                historyList
        );

        recyclerView.setAdapter(adapter);

        loadHistory();
    }

    private void loadHistory() {

        repository.getRecurringDepositHistory(rdNumber)
                .enqueue(new Callback<List<RDHistoryResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<RDHistoryResponse>> call,
                            Response<List<RDHistoryResponse>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            historyList.clear();

                            historyList.addAll(response.body());

                            adapter.notifyDataSetChanged();

                        } else {

                            String error = "";

                            try {

                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }

                            } catch (Exception e) {
                                error = e.getMessage();
                            }

                            Toast.makeText(
                                    RDHistoryActivity.this,
                                    "HTTP "
                                            + response.code()
                                            + "\n\n"
                                            + error,
                                    Toast.LENGTH_LONG
                            ).show();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<List<RDHistoryResponse>> call,
                            Throwable t) {

                        Toast.makeText(
                                RDHistoryActivity.this,
                                "Network Error\n\n"
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}