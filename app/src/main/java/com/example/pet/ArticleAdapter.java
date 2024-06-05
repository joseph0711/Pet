package com.example.pet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private Context context;
    private List<ArticleClass> articleClassList;

    public ArticleAdapter(Context context, List<ArticleClass> articleClassList) {
        this.context = context;
        this.articleClassList = articleClassList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        if (position < 2) {
            ArticleClass articleClass = articleClassList.get(position);
            holder.title.setText(articleClass.getTitle());
            holder.author.setText(articleClass.getAuthor());
            holder.date.setText(articleClass.getDate());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ArticleDetailActivity.class);
                intent.putExtra("articleClass", articleClass);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return articleClassList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date;
        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
        }
    }
}
