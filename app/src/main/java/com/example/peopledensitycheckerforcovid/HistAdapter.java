package com.example.peopledensitycheckerforcovid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistAdapter extends RecyclerView.Adapter<HistAdapter.ViewHolder>{

    private ArrayList<HistEntry> histEntryArrayList;
    private Context context;

    public HistAdapter(ArrayList<HistEntry> histEntryArrayList, Context context){
        this.histEntryArrayList = histEntryArrayList;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.histentry,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistAdapter.ViewHolder holder, int position) {
        HistEntry entry = histEntryArrayList.get(position);
        holder.histNameTV.setText(entry.getHistName());
        holder.histConTV.setText(entry.getHistCon());
        holder.histTempTV.setText(entry.getHistTemp());
        holder.histTimeTV.setText(entry.getHistTime());
        holder.histLocTV.setText(entry.getHistLoc());

    }

    @Override
    public int getItemCount() {
        return histEntryArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView histNameTV,histConTV,histTempTV,histTimeTV,histLocTV;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            histNameTV = itemView.findViewById(R.id.idName);
            histConTV = itemView.findViewById(R.id.idCon);
            histTempTV = itemView.findViewById(R.id.idTemp);
            histTimeTV = itemView.findViewById(R.id.idTime);
            histLocTV = itemView.findViewById(R.id.idLoc);

        }
    }

}
