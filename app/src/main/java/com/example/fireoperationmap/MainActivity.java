package com.example.fireoperationmap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private String searchState = "st_name";
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        recyclerView = findViewById(R.id.recyclerView);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        createSearchView();
        createMapView();
        initializeAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void createSearchView() {
        EditText searchField = (EditText) findViewById(R.id.searchField);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);

        radioGroup.check(R.id.rb_stname);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_stname) {
                    Toast.makeText(MainActivity.this, "상호명으로 검색", Toast.LENGTH_SHORT).show();
                    searchState = "st_name";
                }
                else if (checkedId == R.id.rb_address) {
                    Toast.makeText(MainActivity.this, "주소지로 검색", Toast.LENGTH_SHORT).show();
                    searchState = "address";
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchField.getText().toString();
                if (searchText.equals("")) return;
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                processSearch(searchText);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = searchField.getText().toString();
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    processSearch(searchText);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    return true;
                }

                return false;
            }
        });
    }

    private void processSearch(String searchText) {
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("User")
                        .orderByChild(searchState).startAt(searchText).endAt(searchText + "\uf8ff"), User.class).build();

        adapter.updateOptions(options);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void initializeAdapter() {
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(database.getReference().child("User"), User.class).build();
        adapter = new CustomAdapter(options);

        adapter.setOnItemClickListener(new CustomAdapter.OnPersonItemClickLister() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onItemClick(View view, int position) {
                User user = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), user.getId() + "가 선택됨", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMapView(){
        photoView = findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.naver_map2);

        photoView.setMaximumScale(3.0f);

        //테스트 용 좌표 찍기
        photoView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                Log.d("rect", "left: " + rect.left + ", top: " + rect.top);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}