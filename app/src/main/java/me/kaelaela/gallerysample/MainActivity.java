package me.kaelaela.gallerysample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

public class MainActivity extends AppCompatActivity {

    private MessengerImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.gallery_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GalleryActivity.getIntent(v.getContext()));
            }
        });

        final View bottomSheet = findViewById(R.id.messenger_bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(STATE_HIDDEN);
        findViewById(R.id.messenger_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (behavior.getState()) {
                    case STATE_EXPANDED:
                        behavior.setState(STATE_HIDDEN);
                        break;
                    case STATE_HIDDEN:
                        behavior.setState(STATE_EXPANDED);
                        break;
                }
            }
        });
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.messenger_image_list);
        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new MessengerImageListAdapter(new MessengerImageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                behavior.setState(STATE_HIDDEN);
            }
        });
        recyclerView.setAdapter(adapter);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == STATE_EXPANDED) {
                    ContentUtil.setImageList(MainActivity.this, adapter);
                    recyclerView.scrollToPosition(0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        findViewById(R.id.expand_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(STATE_HIDDEN);
                startActivity(GalleryActivity.getIntent(v.getContext()));
            }
        });
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
