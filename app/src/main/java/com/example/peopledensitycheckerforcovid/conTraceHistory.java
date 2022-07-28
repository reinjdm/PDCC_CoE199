package com.example.peopledensitycheckerforcovid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class conTraceHistory extends Activity {
    private EditText histNameEdt, histConEdt,histTempEdt,histLocEdt;
    private TextView histTimeEdt;
    private Button addBtn, submitBtn;
    private RecyclerView histRV;

    private HistAdapter histAdapter;
    private ArrayList<HistEntry> hist_entries;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        histNameEdt = findViewById(R.id.idEdtName);
        histConEdt = findViewById(R.id.idEdtCon);
        histTempEdt = findViewById(R.id.idEdtTemp);
        histTimeEdt = findViewById(R.id.idCurTime);
        histLocEdt = findViewById(R.id.idEdtLoc);
        addBtn = findViewById(R.id.idBtnAdd);
        submitBtn = findViewById(R.id.idBtnSubmit);
        histRV = findViewById(R.id.idRVHistory);

        loadData();
        buildRecyclerView();


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                histTimeEdt.setText(getCurrentTimestamp());
                hist_entries.add(new HistEntry(histNameEdt.getText().toString(),histConEdt.getText().toString(),histTempEdt.getText().toString(),getCurrentTimestamp(),histLocEdt.getText().toString()));
                histAdapter.notifyItemInserted(hist_entries.size());

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                /*SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();*/
            }
        });
    }

    private void buildRecyclerView(){
        histAdapter = new HistAdapter(hist_entries, conTraceHistory.this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        histRV.setHasFixedSize(true);
        histRV.setLayoutManager(manager);
        histRV.setAdapter(histAdapter);

    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("history", null);
        Type type = new TypeToken<ArrayList<HistEntry>>() {}.getType();
        hist_entries = gson.fromJson(json, type);

        if (hist_entries == null){
            hist_entries = new ArrayList<>();
        }
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(hist_entries);

        editor.putString("history", json);
        editor.apply();

        Toast.makeText(this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }
    @NonNull
    public static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
                .getInstance().getTime());
    }



}
