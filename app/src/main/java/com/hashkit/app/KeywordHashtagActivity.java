package com.hashkit.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.*;

public class KeywordHashtagActivity extends AppCompatActivity {

    private EditText etKeywords;
    private View layoutResults;
    private TextView tvTop5, tvAllTags, btnCopyTop5, btnCopyAll;
    private TextView selectedChip = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_hashtag);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Keyword Hashtags");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etKeywords    = findViewById(R.id.et_keywords);
        layoutResults = findViewById(R.id.layout_results);
        tvTop5        = findViewById(R.id.tv_top5);
        tvAllTags     = findViewById(R.id.tv_all_tags);
        btnCopyTop5   = findViewById(R.id.btn_copy_top5);
        btnCopyAll    = findViewById(R.id.btn_copy_all);

        // Build category chips
        LinearLayout chipRow = findViewById(R.id.chip_row);
        List<String> cats = HashtagData.getCategories();
        for (String cat : cats) {
            TextView chip = makeChip(cat);
            chip.setOnClickListener(v -> {
                // Reset previous selection
                if (selectedChip != null) {
                    selectedChip.setBackground(getDrawable(R.drawable.bg_chip));
                    selectedChip.setTextColor(0xFFBBCCFF);
                }
                // Highlight selected chip
                chip.setBackground(getDrawable(R.drawable.bg_btn_generate));
                chip.setTextColor(0xFFFFFFFF);
                selectedChip = chip;
                etKeywords.setText(cat);
                generate(cat);
            });
            chipRow.addView(chip);
        }

        TextView btnGenerate = findViewById(R.id.btn_generate);
        btnGenerate.setOnClickListener(v -> {
            String kw = etKeywords.getText().toString().trim();
            if (kw.isEmpty()) { Toast.makeText(this, "Enter at least one keyword", Toast.LENGTH_SHORT).show(); return; }
            generate(kw);
        });

        btnCopyTop5.setOnClickListener(v -> copyText(tvTop5.getText().toString(), "Top 5 hashtags copied!"));
        btnCopyAll.setOnClickListener(v -> copyText(tvAllTags.getText().toString(), "All 30 hashtags copied!"));
    }

    private void generate(String input) {
        // Handle multiple keywords separated by comma
        String[] keywords = input.split(",");
        Set<String> allTags = new LinkedHashSet<>();

        for (String kw : keywords) {
            String[] tags = HashtagData.getHashtags(kw.trim());
            allTags.addAll(Arrays.asList(tags));
        }

        List<String> tagList = new ArrayList<>(allTags);
        // Ensure at least 30
        while (tagList.size() < 30) {
            tagList.addAll(Arrays.asList(HashtagData.getHashtags("lifestyle")));
        }

        // Top 5 (most trending — first 5 of primary keyword)
        String primaryKw = keywords[0].trim();
        String[] primaryTags = HashtagData.getHashtags(primaryKw);
        List<String> top5 = new ArrayList<>();
        for (int i = 0; i < Math.min(5, primaryTags.length); i++) top5.add(primaryTags[i]);

        // All 30 (unique, shuffled after position 5)
        Set<String> full = new LinkedHashSet<>(top5);
        for (String t : tagList) {
            full.add(t);
            if (full.size() >= 30) break;
        }
        List<String> fullList = new ArrayList<>(full);

        StringBuilder top5Str = new StringBuilder();
        for (String t : top5) top5Str.append(t).append("  ");
        tvTop5.setText(top5Str.toString().trim());

        StringBuilder allStr = new StringBuilder();
        for (String t : fullList) allStr.append(t).append(" ");
        tvAllTags.setText(allStr.toString().trim());

        layoutResults.setVisibility(View.VISIBLE);
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
        tv.setPadding(28, 12, 28, 12);
        tv.setBackground(getDrawable(R.drawable.bg_chip));
        tv.setClickable(true);
        tv.setFocusable(true);
        return tv;
    }

    private void copyText(String text, String msg) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("hashkit", text));
        Toast.makeText(this, "✅ " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
