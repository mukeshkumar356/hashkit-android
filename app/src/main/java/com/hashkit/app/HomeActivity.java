package com.hashkit.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.card_reels).setOnClickListener(v ->
            startActivity(new Intent(this, ReelsDownloaderActivity.class)));
        findViewById(R.id.card_photo).setOnClickListener(v ->
            startActivity(new Intent(this, PhotoHashtagActivity.class)));
        findViewById(R.id.card_search).setOnClickListener(v ->
            startActivity(new Intent(this, HashtagSearchActivity.class)));
        findViewById(R.id.card_keyword).setOnClickListener(v ->
            startActivity(new Intent(this, KeywordHashtagActivity.class)));
        findViewById(R.id.card_caption).setOnClickListener(v ->
            startActivity(new Intent(this, CaptionActivity.class)));
        findViewById(R.id.card_bio).setOnClickListener(v ->
            startActivity(new Intent(this, BioGeneratorActivity.class)));
        findViewById(R.id.card_counter).setOnClickListener(v ->
            startActivity(new Intent(this, CharCounterActivity.class)));
        findViewById(R.id.card_trending).setOnClickListener(v ->
            startActivity(new Intent(this, TrendingTagsActivity.class)));
    }
}
