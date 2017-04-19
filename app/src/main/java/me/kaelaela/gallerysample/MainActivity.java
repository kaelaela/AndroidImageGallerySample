package me.kaelaela.gallerysample;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_FROM_MAIN = 0;
    private static final int COLUMN_COUNT = 3;
    private AbstractListAdapter<String> adapter;
    private FloatingActionButton fab;

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

        final View bottomSheet = findViewById(R.id.gallery_bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(STATE_HIDDEN);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.bottom_image_list);
        findViewById(R.id.messenger_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new MessengerImageListAdapter(new MessengerImageListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick() {
                        behavior.setState(STATE_HIDDEN);
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                        LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(adapter);
                findViewById(R.id.tools_layout).setVisibility(View.GONE);
                switch (behavior.getState()) {
                    case STATE_COLLAPSED:
                        behavior.setState(STATE_HIDDEN);
                        break;
                    case STATE_HIDDEN:
                        behavior.setState(STATE_COLLAPSED);
                        break;
                }
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                Toast.makeText(view.getContext(), "画像を送信しました", Toast.LENGTH_SHORT).show();
                behavior.setState(STATE_HIDDEN);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        final Point point = new Point();
        display.getSize(point);
        findViewById(R.id.insta_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new ImageListAdapter(point.x, COLUMN_COUNT, new ImageListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int count) {
                        if (count <= 0) {
                            fab.hide();
                        } else {
                            fab.show();
                        }
                    }

                    @Override
                    public void onOverCount() {
                        Snackbar.make(findViewById(R.id.bottom_image_list), "一度に送信できるのは5枚までです", Snackbar.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, COLUMN_COUNT));
                recyclerView.setAdapter(adapter);
                findViewById(R.id.tools_layout).setVisibility(View.VISIBLE);
                switch (behavior.getState()) {
                    case STATE_COLLAPSED:
                        behavior.setState(STATE_HIDDEN);
                        break;
                    case STATE_HIDDEN:
                        behavior.setState(STATE_COLLAPSED);
                        break;
                }
            }
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == STATE_COLLAPSED) {
                    ContentUtil.setImageList(MainActivity.this, adapter);
                    recyclerView.scrollToPosition(0);
                } else if (newState == STATE_HIDDEN) {
                    fab.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(STATE_HIDDEN);
                fab.hide();
            }
        });

        findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "カメラ起動", Toast.LENGTH_SHORT).show();
                behavior.setState(STATE_HIDDEN);
                fab.hide();
            }
        });
        findViewById(R.id.photo_library_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(STATE_HIDDEN);
                startActivity(GalleryActivity.getIntent(v.getContext()));
                fab.hide();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saf:
                Intent intent;
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_FROM_MAIN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FROM_MAIN) {
            Snackbar.make(findViewById(android.R.id.content), data.getData().getPath(), Snackbar.LENGTH_SHORT).show();
        }
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
