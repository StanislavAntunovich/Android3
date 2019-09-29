package geekbrains.ru.lesson4retrofit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import geekbrains.ru.lesson4retrofit.adapters.UsersAdapter;
import geekbrains.ru.lesson4retrofit.data.DataWorker;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;
import geekbrains.ru.lesson4retrofit.data.entities.UserEntity;
import geekbrains.ru.lesson4retrofit.data.room.RoomDB;
import geekbrains.ru.lesson4retrofit.rest.RestAPI;
import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String DB_NAME = "RoomDB";

    private UsersAdapter adapter;
    private MainPresenter presenter;

    private ProgressBar progressBar;
    private EditText etUserName;
    private Double timeResult = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Realm.init(getApplicationContext());

        initPresenter();

        initRecycler();
        initViews();
    }

    @Override
    protected void onResume() {
        presenter.bindView(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unbindView();
    }



    private void initPresenter() {
        presenter = new MainPresenter();
        DataWorker model = new DataWorker();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RoomDB roomDB = Room.databaseBuilder(getApplicationContext(), RoomDB.class, DB_NAME).build();
        model.setRoomDB(roomDB);
        model.setApi(retrofit.create(RestAPI.class));
        presenter.setModel(model);
    }

    private void initRecycler() {
        adapter = new UsersAdapter();
        RecyclerView recyclerView = findViewById(R.id.rv_repos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initViews() {
        etUserName = findViewById(R.id.editText);
        progressBar = findViewById(R.id.progressBar);

        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(v -> onClick());

        Button bntSaveRoom = findViewById(R.id.btn_save_room);
        bntSaveRoom.setOnClickListener(v -> presenter.onRoomSaveClicked(adapter.getData()));

        Button btnSaveRealm = findViewById(R.id.btn_save_realm);
        btnSaveRealm.setOnClickListener(v -> presenter.onRealmSaveClicked(adapter.getData()));

        Button btnLoadRoom = findViewById(R.id.btn_load_room);
        btnLoadRoom.setOnClickListener(v -> presenter.onRoomLoadClicked());

        Button btnLoadRealm = findViewById(R.id.btn_load_realm);
        btnLoadRealm.setOnClickListener(v -> presenter.onRealmLoadClicked());
    }

    //TODO
    private void startUserReposActivity(RepoEntity model) {
        Intent intent = new Intent(this, RepoActivity.class);
        intent.putExtra(RepoActivity.REPO_MODEL_KEY, model);
        startActivity(intent);
    }

    private boolean checkInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityManager != null
                ? connectivityManager.getActiveNetworkInfo() : null;
        if (networkinfo == null || !networkinfo.isConnected()) {
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void onClick() {
        if (checkInternet()) return;
        String request = etUserName.getText().toString();
        etUserName.setText("");
        presenter.loadNetData(request);
    }

    public void updateResult() {
        TextView tvResult = findViewById(R.id.tv_test_time);
        TextView tvCount = findViewById(R.id.tv_test_items_count);
        tvResult.setText(String.valueOf(timeResult));
        tvCount.setText(String.valueOf(adapter.getData().size()));
    }

    public void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgress() {
        progressBar.setVisibility(View.GONE);
    }

    public void setResult(Double result) {
        this.timeResult = result;
    }

    public void updateRecyclerView() {
        adapter.notifyDataSetChanged();
    }

    public void updateRecyclerData(List<UserEntity> data) {
        adapter.setData(data);
    }

}
