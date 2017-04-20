package me.kaelaela.gallerysample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

public class GalleryActivity extends AppCompatActivity {

    private final int COLUMN_COUNT = 3;
    private ImageListAdapter adapter;
    private FloatingActionButton fab;

    public static Intent getIntent(Context context) {
        return new Intent(context, GalleryActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "画像を送信しました", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.image_list);
        Point point = new Point();
        display.getSize(point);
        adapter = new ImageListAdapter(point.x, COLUMN_COUNT, new ImageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int count) {
                if (count <= 0) {
                    ((FloatingActionButton) findViewById(R.id.fab)).hide();
                } else {
                    ((FloatingActionButton) findViewById(R.id.fab)).show();
                }
            }

            @Override
            public void onOverCount() {
                Snackbar.make(findViewById(R.id.activity_gallery), "一度に送信できるのは5枚までです", Snackbar.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMN_COUNT));
        recyclerView.setAdapter(adapter);
        ContentUtil.setImageList(this, adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                for (int result : grantResults) {
                    if (result == PermissionChecker.PERMISSION_GRANTED) {
                        ContentUtil.setImageList(this, adapter);
                    } else {
                        finish();
                    }
                }
                break;
        }
    }
}
