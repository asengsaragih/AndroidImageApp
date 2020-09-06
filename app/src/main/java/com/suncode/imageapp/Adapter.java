package com.suncode.imageapp;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterHolder> {

    private Context mContext;
    private List<Model> mData;
    private ClickHandler mClickHandler;

    private static final String TAG = "CHECKTAG";

    public Adapter(Context mContext, List<Model> mData, ClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mData = mData;
        this.mClickHandler = mClickHandler;
    }

    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_image, parent, false);
        return new AdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHolder holder, int position) {
        //change dp imageview
        int dpOne = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, mContext.getResources().getDisplayMetrics());
        int dpTwo = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, mContext.getResources().getDisplayMetrics());

        // width diacak seuai dengan random
        int rand = getRandomHeight();
        if (rand == 1) {
            holder.imageView.getLayoutParams().height = dpOne;
            holder.imageView.requestLayout();
        } else {
            holder.imageView.getLayoutParams().height = dpTwo;
            holder.imageView.requestLayout();
        }

        String url = mData.get(position).getImage();
        String urlSubstring = "https://picsum.photos/id/" + mData.get(position).getId();
        String imageCompress = urlSubstring + "/480/360.webp";

        Glide.with(mContext)
                .load(imageCompress)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            showImage(imageCompress);
        });

        holder.imageView.setOnLongClickListener(v -> {
            // handler nya di panggil disini, parameternya bisa dicustom sesuka hati
            mClickHandler.downloadImage(url + ".jpg");
            return true;
        });
    }

    void showImage(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_image, null);
        builder.setView(view);

        ImageView imageView = view.findViewById(R.id.imageView_dialog);

        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);

        builder.show();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    private int getRandomHeight() {
        //fungsi untuk ngacak widht
        return new Random().nextInt(2);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AdapterHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;

        public AdapterHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_list_item);
        }
    }

    //buat interface untuk menghandle fungsi longclick listener, dan di masukkan ke construct
    interface ClickHandler {
        //bisa buat void juga disini atau boolean kalau mau bolean harus dipanggil di holder class
        // parameter bebas sesuai dengan keinginan
        void downloadImage(String url);
    }
}
