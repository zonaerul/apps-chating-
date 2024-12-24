package com.chaerul.chating.adapter.data;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chaerul.chating.R;

public class Loading {

    private AlertDialog dialog;

    public Loading(Context context) {
        // Inflate layout untuk dialog custom
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.progresscustom, null);

        // Buat AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setCancelable(false);  // Agar tidak bisa ditutup sembarangan

        // Set transparent background
        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // Transparent background
        }

        // Atur elemen dialog
        ImageView loadingGif = customView.findViewById(R.id.loading_gif);
        TextView loadingText = customView.findViewById(R.id.loading_text);
        loadingText.setGravity(Gravity.CENTER);

        // Muat GIF menggunakan Glide
        Glide.with(context)
                .asGif()
                .load(R.drawable.loading) // Ganti dengan file GIF di folder drawable
                .into(loadingGif);

        // Set teks loading (opsional)
        loadingText.setText("Loading...");

        // Mengatur lebar dialog menjadi 130dp
        dialog.setOnShowListener(dialogInterface -> {
            if (dialog.getWindow() != null) {
                // Mengonversi 130dp ke dalam piksel
                int widthInDp = 140; // Lebar dalam dp
                float density = context.getResources().getDisplayMetrics().density; // Mendapatkan density layar
                int widthInPx = (int) (widthInDp * density); // Mengonversi dp ke px

                int heightInPx = WindowManager.LayoutParams.WRAP_CONTENT; // Bisa disesuaikan

                // Menetapkan lebar dan tinggi dialog
                dialog.getWindow().setLayout(widthInPx, heightInPx);
            }
        });
    }

    // Metode untuk menampilkan dialog
    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    // Metode untuk menutup dialog
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
