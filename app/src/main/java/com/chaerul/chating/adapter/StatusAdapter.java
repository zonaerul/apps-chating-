package com.chaerul.chating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chaerul.chating.R;
import com.chaerul.chating.adapter.data.StatusUsers;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHodler> {
    Context context;
    ArrayList<StatusUsers> array;
    public StatusAdapter(Context context, ArrayList<StatusUsers> array){
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHodler(LayoutInflater.from(context).inflate(R.layout.recycler_status, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodler holder, int position) {
        StatusUsers data = array.get(position);
        Glide.with(context)
                .load(data.getUrlPhotoOrVideo())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.background_rounded)
                .into(holder.imageView);
        holder.nameUser.setText(data.getUserName());
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class ViewHodler extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameUser;
        public ViewHodler(@NonNull View itemView) {
            super(itemView);

            nameUser = itemView.findViewById(R.id.user_name);
            imageView = itemView.findViewById(R.id.status_image);
        }
    }
}
