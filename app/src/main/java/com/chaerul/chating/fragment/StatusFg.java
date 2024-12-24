package com.chaerul.chating.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.helper.widget.Grid;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaerul.chating.R;
import com.chaerul.chating.adapter.StatusAdapter;
import com.chaerul.chating.adapter.data.StatusUsers;

import java.util.ArrayList;

public class StatusFg extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.status_fg, container, false);

        CardView add_status = view.findViewById(R.id.card_add_status);
        add_status.setOnClickListener(v -> addStatusWithGallery());
        loadDataStatus(view);
        return view;
    }
    private void addStatusWithGallery(){

    }

    private void loadDataStatus(View v){
        RecyclerView recyclerView = v.findViewById(R.id.recycler_status);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(lm);
        ArrayList<StatusUsers> arrayList = new ArrayList<>();
        arrayList.add(new StatusUsers("https://i.pinimg.com/originals/d6/14/18/d614180dc4c54171a9b18412aadcbf8d.jpg", "foto", "erul@app.com", "Erul"));
        arrayList.add(new StatusUsers("https://i.pinimg.com/originals/da/2b/70/da2b70ec58a26d97e8444c6e0eec8e34.jpg", "foto", "nila@app.com", "Nila"));
       StatusAdapter adapter = new StatusAdapter(getContext(), arrayList);
        recyclerView.setAdapter(adapter);
    }
}
