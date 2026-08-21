package com.hashkit.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CharCounterActivity extends AppCompatActivity {

    private EditText etText;
    private TextView tvCharCount, tvWordCount, tvLineCount;
    private TextView tvCaptionStatus, tvBioStatus, tvCommentStatus;
    private ProgressBar pbCaption, pbBio, pbComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_counter);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Character Counter");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etText         = findViewById(R.id.et_text);
        tvCharCount    = findViewById(R.id.tv_char_count);
        tvWordCount    = findViewById(R.id.tv_word_count);
        tvLineCount    = findViewById(R.id.tv_line_count);
        tvCaptionStatus = findViewById(R.id.tv_caption_status);
        tvBioStatus    = findViewById(R.id.tv_bio_status);
        tvCommentStatus = findViewById(R.id.tv_comment_status);
        pbCaption      = findViewById(R.id.pb_caption);
        pbBio          = findViewById(R.id.pb_bio);
        pbComment      = findViewById(R.id.pb_comment);

        findViewById(R.id.btn_clear).setOnClickListener(v -> etText.setText(""));

        etText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { updateCounts(s.toString()); }
        });
    }

    private void updateCounts(String text) {
        int chars = text.length();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int lines = text.isEmpty() ? 0 : text.split("\n", -1).length;

        tvCharCount.setText(String.valueOf(chars));
        tvWordCount.setText(String.valueOf(words));
        tvLineCount.setText(String.valueOf(lines));

        // Caption: 2200
        int capProgress = Math.min(chars, 2200);
        pbCaption.setProgress(capProgress);
        tvCaptionStatus.setText(chars + " / 2200");
        tvCaptionStatus.setTextColor(chars > 2200 ? 0xFFFF5252 : 0xFF00E5FF);

        // Bio: 150
        int bioProgress = Math.min(chars, 150);
        pbBio.setProgress(bioProgress);
        tvBioStatus.setText(chars + " / 150");
        tvBioStatus.setTextColor(chars > 150 ? 0xFFFF5252 : 0xFF00E5FF);

        // Comment: 2200
        int comProgress = Math.min(chars, 2200);
        pbComment.setProgress(comProgress);
        tvCommentStatus.setText(chars + " / 2200");
        tvCommentStatus.setTextColor(chars > 2200 ? 0xFFFF5252 : 0xFF00E5FF);
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
