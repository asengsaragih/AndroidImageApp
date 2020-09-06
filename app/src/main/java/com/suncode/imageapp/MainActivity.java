package com.suncode.imageapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CHECKTAG";
    public static final int REQUEST_CODE_WRITE_STORAGE = 1;

    private RecyclerView recyclerView;
    private Adapter adapter;
    private ApiInterface apiInterface;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mDownloadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide action bar
        getSupportActionBar().hide();
        //transparent status/notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //change color text in status/notification bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        recyclerView = findViewById(R.id.rec_main);
        apiInterface = ApiClient.build().create(ApiInterface.class);
        mSwipeRefresh = findViewById(R.id.swipe_container);
        mDownloadProgress = findViewById(R.id.progress_download_image);

        mSwipeRefresh.setOnRefreshListener(() -> {
            //reset adapter recycle
            adapter.clear();

            //ambil data baru
            getData();

            //menonaktifkan loading refreshing
            mSwipeRefresh.setRefreshing(false);
        });

        //inisialisasi awal
        getData();

    }

    private void getData() {
        Call<List<Model>> call = apiInterface.getImage(new Random().nextInt(10), 100);

        call.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                //shuffle data in list
                List<Model> models = response.body();
                Collections.shuffle(models);

                adapter = new Adapter(MainActivity.this, models, url -> {

                    if (!isStoragePermissionGranted())
                        accessPermissionWriteStorage();

                    showProgressDownload(true);

                    Glide.with(MainActivity.this)
                            .asBitmap()
                            .load(url)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                    showProgressDownload(false);

                                    if (downloadImage(resource))
                                        shortToast("Gambar Tersimpan");
                                    else
                                        shortToast("Gagal mengunduh gambar");
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });

                });

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Model>> call, Throwable t) {

            }
        });
    }

    private boolean downloadImage(Bitmap bitmap) {
        boolean isSuccess = false;

        //buat folder baru di dalam storage
        File exportFolder = new File(Environment.getExternalStorageDirectory() + "/" + "ImageApp");

        if (!exportFolder.exists())
            exportFolder.mkdir();

        //generate image name
        String filename = generateImageName();

        File imageFile = new File(exportFolder, filename);

        try {
            //proses saving image into external storage
            OutputStream stream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        }

        return isSuccess;
    }

    private void showProgressDownload(Boolean check) {
        if (check)
            mDownloadProgress.setVisibility(View.VISIBLE);
        else
            mDownloadProgress.setVisibility(View.GONE);
    }

    private String generateImageName() {
        //fungsi untuk generate nama file image
        // ex images-20200906145565.jpg
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return "Images-" + simpleDateFormat.format(date) + ".jpg";
    }

    private boolean isStoragePermissionGranted() {
        //check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //sudah permisi
                return true;
            } else {
                //belum permisi
                return false;
            }
        } else {
            // perimisi auto ada apabila api dibawah marsmello
            return true;
        }
    }

    private void accessPermissionWriteStorage() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
    }

    private void shortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}