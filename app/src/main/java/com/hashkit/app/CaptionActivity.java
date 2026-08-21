package com.hashkit.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.*;

public class CaptionActivity extends AppCompatActivity {

    private TextView tvCaption, tvCharCount;
    private View layoutCaption;
    private String[] currentCaptions;
    private int currentIndex = 0;
    private String currentCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Caption Writer");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvCaption    = findViewById(R.id.tv_caption);
        tvCharCount  = findViewById(R.id.tv_char_count);
        layoutCaption = findViewById(R.id.layout_caption);

        TextView btnShuffle = findViewById(R.id.btn_shuffle);
        TextView btnCopy    = findViewById(R.id.btn_copy);

        // Build category chips
        LinearLayout chipRow = findViewById(R.id.chip_row);
        List<String> cats = HashtagData.getCaptionCategories();
        for (String cat : cats) {
            TextView chip = makeChip(cat);
            chip.setOnClickListener(v -> loadCategory(cat, chipRow, chip));
            chipRow.addView(chip);
        }

        btnShuffle.setOnClickListener(v -> {
            if (currentCaptions == null || currentCaptions.length == 0) return;
            currentIndex = (currentIndex + 1) % currentCaptions.length;
            showCaption(currentCaptions[currentIndex]);
        });

        btnCopy.setOnClickListener(v -> {
            String text = tvCaption.getText().toString();
            if (text.isEmpty()) return;
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("hashkit", text));
            Toast.makeText(this, "✅ Caption copied!", Toast.LENGTH_SHORT).show();
        });

        // Auto-select first category
        if (!cats.isEmpty()) {
            TextView first = (TextView) chipRow.getChildAt(0);
            if (first != null) loadCategory(cats.get(0), chipRow, first);
        }
    }

    private void loadCategory(String cat, LinearLayout chipRow, TextView selected) {
        currentCategory = cat;
        currentCaptions = HashtagData.getCaptions(cat);
        currentIndex = new Random().nextInt(currentCaptions.length);

        // Update chip selection visuals
        for (int i = 0; i < chipRow.getChildCount(); i++) {
            View child = chipRow.getChildAt(i);
            if (child instanceof TextView) {
                boolean isSelected = child == selected;
                child.setBackground(isSelected
                    ? getDrawable(R.drawable.bg_btn_generate)
                    : getDrawable(R.drawable.bg_chip));
            }
        }

        showCaption(currentCaptions[currentIndex]);
        layoutCaption.setVisibility(View.VISIBLE);
    }

    private void showCaption(String caption) {
        tvCaption.setText(caption);
        tvCharCount.setText(caption.length() + " / 2200");
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
        tv.setPadding(28, 14, 28, 14);
        tv.setBackground(getDrawable(R.drawable.bg_chip));
        tv.setClickable(true);
        tv.setFocusable(true);
        return tv;
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
