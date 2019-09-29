package geekbrains.ru.lesson4retrofit;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;

public class RepoActivity extends AppCompatActivity {
    public static final String REPO_MODEL_KEY = "repo_model_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        RepoEntity model = (RepoEntity) getIntent()
                .getSerializableExtra(REPO_MODEL_KEY);

        if (model != null) {
            TextView tvName = findViewById(R.id.tv_name_value);
            tvName.setText(model.getName());

            TextView tvDesc = findViewById(R.id.tv_description_value);
            String description = model.getDescription();
            String descToSet = description == null || description.isEmpty() ? "no description" : description;
            tvDesc.setText(descToSet);

            TextView tvLang = findViewById(R.id.tv_lang_value);
            tvLang.setText(model.getLanguage());

            TextView tvUrl = findViewById(R.id.tv_url_value);
            tvUrl.setText(model.getUrl());
        }

    }
}
