package com.example.pet;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.noties.markwon.Markwon;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ArticleClass articleClass = (ArticleClass) getIntent().getSerializableExtra("articleClass");

        TextView title = findViewById(R.id.title);
        TextView author = findViewById(R.id.author);
        TextView date = findViewById(R.id.date);
        TextView content = findViewById(R.id.content);

        title.setText(articleClass.getTitle());
        author.setText(articleClass.getAuthor());
        date.setText(articleClass.getDate());

        // Create a Markwon instance
        Markwon markwon = Markwon.create(this);

        // Use the Markwon instance to set the markdown content
        markwon.setMarkdown(content, articleClass.getMarkdownContent());
    }
}
