package com.hashkit.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.*;

public class TrendingTagsActivity extends AppCompatActivity {

    private TextView tvCatTitle, tvTop5, tvAllTags;
    private LinearLayout chipRow;
    private String currentCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_tags);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Trending Tags");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvCatTitle = findViewById(R.id.tv_cat_title);
        tvTop5     = findViewById(R.id.tv_top5);
        tvAllTags  = findViewById(R.id.tv_all_tags);
        chipRow    = findViewById(R.id.chip_row);

        List<String> cats = HashtagData.getCategories();
        for (String cat : cats) {
            TextView chip = makeChip(capitalize(cat));
            chip.setOnClickListener(v -> loadCategory(cat, chip));
            chipRow.addView(chip);
        }

        // Auto-select first category
        if (!cats.isEmpty()) {
            TextView first = (TextView) chipRow.getChildAt(0);
            if (first != null) loadCategory(cats.get(0), first);
        }

        findViewById(R.id.btn_copy_all).setOnClickListener(v ->
            copyText(tvAllTags.getText().toString(), "All 30 hashtags copied!"));
        findViewById(R.id.btn_copy_top5).setOnClickListener(v ->
            copyText(tvTop5.getText().toString(), "Top 5 hashtags copied!"));
    }

    private void loadCategory(String cat, TextView selected) {
        currentCategory = cat;

        for (int i = 0; i < chipRow.getChildCount(); i++) {
            View child = chipRow.getChildAt(i);
            if (child instanceof TextView tv) {
                boolean sel = child == selected;
                child.setBackground(sel ? getDrawable(R.drawable.bg_btn_generate) : getDrawable(R.drawable.bg_chip));
                tv.setTextColor(sel ? 0xFFFFFFFF : 0xFFBBCCFF);
            }
        }

        String[] hashtags = HashtagData.getHashtags(cat);
        tvCatTitle.setText("⭐ Trending — " + capitalize(cat));

        StringBuilder top5Sb = new StringBuilder();
        for (int i = 0; i < Math.min(5, hashtags.length); i++) top5Sb.append(hashtags[i]).append("\n");
        tvTop5.setText(top5Sb.toString().trim());

        StringBuilder allSb = new StringBuilder();
        for (String tag : hashtags) allSb.append(tag).append("  ");
        tvAllTags.setText(allSb.toString().trim());
    }

    private void copyText(String text, String msg) {
        if (text.isEmpty()) { Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show(); return; }
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("hashkit", text));
        Toast.makeText(this, "✅ " + msg, Toast.LENGTH_SHORT).show();
    }

    private TextView makeChip(String text) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(8);
        tv.setLayoutParams(lp);
        tv.setText(text);
        tv.setTextColor(0xFFBBCCFF);
        tv.setTextSize(13f);
        tv.setPadding(28, 16, 28, 16);
        tv.setBackground(getDrawable(R.drawable.bg_chip));
        tv.setClickable(true);
        tv.setFocusable(true);
        return tv;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
