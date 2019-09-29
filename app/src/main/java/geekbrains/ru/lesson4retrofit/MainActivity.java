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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import geekbrains.ru.lesson4retrofit.adapters.UsersAdapter;
import geekbrains.ru.lesson4retrofit.data.RepoEntity;
import geekbrains.ru.lesson4retrofit.data.UserEntity;
import geekbrains.ru.lesson4retrofit.data.room.RoomDB;
import geekbrains.ru.lesson4retrofit.rest.RestApi;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String DB_NAME = "RoomDB";
    private int TESTS_COUNT = 100;

    private CompositeDisposable bag = new CompositeDisposable();

    private UsersAdapter adapter;

    private ProgressBar progressBar;
    private EditText etUserName;
    private RoomDB roomDB;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private RestApi api = retrofit.create(RestApi.class);

    private SingleObserver<Double> loadObserver = new SingleObserver<Double>() {
        @Override
        public void onSubscribe(Disposable d) {
            bag.add(d);
        }

        @Override
        public void onSuccess(Double aDouble) {
            setResult(aDouble);
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }
    };
    private SingleObserver<Double> saveObserver = new SingleObserver<Double>() {
        @Override
        public void onSubscribe(Disposable d) {
            bag.add(d);
        }

        @Override
        public void onSuccess(Double aDouble) {
            setResult(aDouble);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Realm.init(getApplicationContext());

        roomDB = Room.databaseBuilder(getApplicationContext(), RoomDB.class, DB_NAME).build();

        etUserName = findViewById(R.id.editText);
        progressBar = findViewById(R.id.progressBar);

        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener((v) -> onClick());

        Button bntSaveRoom = findViewById(R.id.btn_save_room);
        bntSaveRoom.setOnClickListener(v -> saveAllRoom());

        Button btnSaveRealm = findViewById(R.id.btn_save_realm);
        btnSaveRealm.setOnClickListener(v -> saveAllRealm());

        Button btnLoadRoom = findViewById(R.id.btn_load_room);
        btnLoadRoom.setOnClickListener(v -> loadFromRoom());

        Button btnLoadRealm = findViewById(R.id.btn_load_realm);
        btnLoadRealm.setOnClickListener(v -> loadFromRealm());

        adapter = new UsersAdapter();
        RecyclerView recyclerView = findViewById(R.id.rv_repos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        bag.clear();
        super.onDestroy();
    }

    private void startRepoActivity(RepoEntity model) {
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
        progressBar.setVisibility(View.VISIBLE);
        download(etUserName.getText().toString());
    }

    private void saveAllRoom() {
        progressBar.setVisibility(View.VISIBLE);
        Single<Double> saveRoomObs = Single.create((emitter) -> {
            List<UserEntity> data = adapter.getData();
            long sum = 0;

            for (int i = 0; i < TESTS_COUNT; i++) {
                roomDB.getUsersDao().deleteAll();
                Date date1 = new Date();
                roomDB.getUsersDao().saveAll(data);
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }

            double result = sum / (double) TESTS_COUNT;
            emitter.onSuccess(result);
        });

        saveRoomObs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(saveObserver);
    }

    private void saveAllRealm() {
        progressBar.setVisibility(View.VISIBLE);
        Single<Double> single = Single.create(emitter -> {

            Realm realm = Realm.getDefaultInstance();
            List<UserEntity> data = adapter.getData();
            long sum = 0;
            for (int i = 0; i < TESTS_COUNT; i++) {
                realm.beginTransaction();
                realm.deleteAll();
                Date date1 = new Date();
                realm.insert(data);
                realm.commitTransaction();
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }
            double result = sum / (double) TESTS_COUNT;
            realm.close();
            emitter.onSuccess(result);
        });


        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(saveObserver);

    }

    private void loadFromRoom() {
        progressBar.setVisibility(View.VISIBLE);
        adapter.clearData();
        Single<Double> single = Single.create(emitter -> {

            List<UserEntity> data = new ArrayList<>();
            long sum = 0;
            for (int i = 0; i < TESTS_COUNT; i++) {
                Date date1 = new Date();
                data = roomDB.getUsersDao().getAllUsers();
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }
            adapter.setData(data);
            double result = sum / (double) TESTS_COUNT;
            emitter.onSuccess(result);
        });

        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadObserver);
    }

    private void setResult(Double aDouble) {
        TextView tvResult = findViewById(R.id.tv_test_time);
        TextView tvCount = findViewById(R.id.tv_test_items_count);
        tvResult.setText(String.valueOf(aDouble));
        tvCount.setText(String.valueOf(adapter.getData().size()));
    }

    private void loadFromRealm() {
        progressBar.setVisibility(View.VISIBLE);
        adapter.clearData();

        Single<Double> single = Single.create(emitter -> {
            Realm realm = Realm.getDefaultInstance();
            long sum = 0;
            List<UserEntity> realmResults = new ArrayList<>();
            for (int i = 0; i < TESTS_COUNT; i++) {
                Date date1 = new Date();
                realmResults = realm.where(UserEntity.class).findAll();
                Date date2 = new Date();
                sum += date2.getTime() - date1.getTime();
            }
            List<UserEntity> data = realm.copyFromRealm(realmResults);
            double resultTime = sum / (double) TESTS_COUNT;
            adapter.setData(data);
            realm.close();
            emitter.onSuccess(resultTime);
        });

        single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadObserver);
    }

    private void downloadAllUsers() {
        api.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<UserEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        bag.add(d);
                    }

                    @Override
                    public void onSuccess(List<UserEntity> userEntities) {
                        adapter.setData(userEntities);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void downloadUser(String request) {
        api.getUser(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<UserEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        bag.add(d);
                    }

                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        List<UserEntity> singleList = new ArrayList<>(1);
                        singleList.add(userEntity);
                        adapter.setData(singleList);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void download(String request) {
        if (request.isEmpty()) {
            downloadAllUsers();
            return;
        }

        downloadUser(request);
    }


}
