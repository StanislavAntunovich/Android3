package geekbrains.ru.lesson4retrofit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import geekbrains.ru.lesson4retrofit.R;
import geekbrains.ru.lesson4retrofit.data.entities.RepoEntity;

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.ReposViewHolder> {
    private List<RepoEntity> data;
    private OnItemClickListener listener;

    public ReposAdapter(OnItemClickListener listener) {
        this.listener = listener;
        this.data = new ArrayList<>();
    }

    public void setData(List<RepoEntity> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ReposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_repo, parent, false);
        return new ReposViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReposViewHolder holder, int position) {
        RepoEntity repo = data.get(position);
        holder.tvRepoName.setText(repo.getName());
        int iconId = repo.isPrivate() ? R.drawable.ic_invisible : R.drawable.ic_visible;
        holder.imRepoPrivacy.setImageResource(iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected class ReposViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRepoName;
        private ImageView imRepoPrivacy;

        public ReposViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRepoName = itemView.findViewById(R.id.tv_repo_name);
            imRepoPrivacy = itemView.findViewById(R.id.im_repo_privacy);
            itemView.setOnClickListener(v -> {
                RepoEntity model = data.get(getAdapterPosition());
                listener.onItemClick(model);
            });
        }
    }

}
