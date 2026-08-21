package com.hashkit.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import java.io.InputStream;
import java.util.*;

public class PhotoHashtagActivity extends AppCompatActivity {

    private Uri imageUri;
    private View layoutPick, layoutCategory, layoutCaption, layoutHashtags, loader;
    private ImageView ivPreview;
    private TextView tvCaption, tvTop5, tvAllTags, btnAnalyze;
    private View selectedCatBtn = null;
    private String detectedCategory = "lifestyle";

    private static final int[] CAT_VIEW_IDS = {
        R.id.cat_food, R.id.cat_travel, R.id.cat_fashion,
        R.id.cat_fitness, R.id.cat_nature, R.id.cat_beauty,
        R.id.cat_business, R.id.cat_motivation, R.id.cat_reels,
        R.id.cat_photography, R.id.cat_art, R.id.cat_lifestyle
    };
    private static final String[] CAT_KEYS = {
        "food","travel","fashion","fitness","nature","beauty",
        "business","motivation","reels","photography","art","lifestyle"
    };

    // Modern Photo Picker — no storage/media permission needed at all.
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
        registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri == null) return;
            imageUri = uri;
            ivPreview.setImageURI(imageUri);
            ivPreview.setVisibility(View.VISIBLE);
            layoutPick.setVisibility(View.GONE);
            btnAnalyze.setVisibility(View.VISIBLE);
            layoutCategory.setVisibility(View.GONE);
            layoutCaption.setVisibility(View.GONE);
            layoutHashtags.setVisibility(View.GONE);
            selectedCatBtn = null;
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_hashtag);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Photo AI Generator");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutPick     = findViewById(R.id.layout_pick);
        ivPreview      = findViewById(R.id.iv_preview);
        layoutCategory = findViewById(R.id.layout_category);
        layoutCaption  = findViewById(R.id.layout_caption);
        layoutHashtags = findViewById(R.id.layout_hashtags);
        loader         = findViewById(R.id.loader);
        tvCaption      = findViewById(R.id.tv_caption);
        tvTop5         = findViewById(R.id.tv_top5);
        tvAllTags      = findViewById(R.id.tv_all_tags);
        btnAnalyze     = findViewById(R.id.btn_analyze);

        layoutPick.setOnClickListener(v -> pickImage());
        ivPreview.setOnClickListener(v -> pickImage());
        btnAnalyze.setOnClickListener(v -> analyzeImage());

        for (int i = 0; i < CAT_VIEW_IDS.length; i++) {
            final String key = CAT_KEYS[i];
            final int idx = i;
            TextView btn = findViewById(CAT_VIEW_IDS[i]);
            if (btn != null) btn.setOnClickListener(v -> selectCategory((TextView) v, key));
        }

        findViewById(R.id.btn_copy_caption).setOnClickListener(v ->
            copyText(tvCaption.getText().toString(), "Caption copied!"));
        findViewById(R.id.btn_copy_top5).setOnClickListener(v ->
            copyText(tvTop5.getText().toString(), "Top 5 hashtags copied!"));
        findViewById(R.id.btn_copy_all).setOnClickListener(v ->
            copyText(tvAllTags.getText().toString(), "All 30 hashtags copied!"));
        findViewById(R.id.btn_new_caption).setOnClickListener(v -> refreshCaption());
    }

    private void pickImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
            .build());
    }

    private void analyzeImage() {
        if (imageUri == null) return;
        loader.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(false);

        // Load bitmap from actual URI — fixes the "same result" bug
        Bitmap bitmap;
        try {
            InputStream is = getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 2; // downsample for speed
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            if (is != null) is.close();
        } catch (Exception e) {
            loader.setVisibility(View.GONE);
            btnAnalyze.setEnabled(true);
            Toast.makeText(this, "Cannot load image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bitmap == null) {
            loader.setVisibility(View.GONE);
            btnAnalyze.setEnabled(true);
            layoutCategory.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Select category manually", Toast.LENGTH_SHORT).show();
            return;
        }

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        ImageLabelerOptions options = new ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.45f)
            .build();
        ImageLabeler labeler = ImageLabeling.getClient(options);

        labeler.process(image)
            .addOnSuccessListener(labels -> {
                loader.setVisibility(View.GONE);
                btnAnalyze.setEnabled(true);
                if (labels.isEmpty()) {
                    layoutCategory.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Could not detect — select category manually", Toast.LENGTH_LONG).show();
                    return;
                }
                processLabels(labels);
            })
            .addOnFailureListener(e -> {
                loader.setVisibility(View.GONE);
                btnAnalyze.setEnabled(true);
                layoutCategory.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Auto-detect failed — select manually", Toast.LENGTH_LONG).show();
            });
    }

    private void processLabels(List<ImageLabel> labels) {
        // Build category vote map from all detected labels
        Map<String, Double> catScore = new LinkedHashMap<>();
        StringBuilder detectedSb = new StringBuilder();

        for (ImageLabel label : labels) {
            String text = label.getText();
            double conf = label.getConfidence();
            detectedSb.append(text).append("  ");
            String cat = HashtagData.labelToCategory(text);
            catScore.put(cat, catScore.getOrDefault(cat, 0.0) + conf);
        }

        // Pick highest-score category
        String bestCat = "lifestyle";
        double maxScore = 0;
        for (Map.Entry<String, Double> e : catScore.entrySet()) {
            if (e.getValue() > maxScore) { maxScore = e.getValue(); bestCat = e.getKey(); }
        }
        detectedCategory = bestCat;

        // Show category chips for manual override
        layoutCategory.setVisibility(View.VISIBLE);

        // Highlight detected category button
        for (int i = 0; i < CAT_KEYS.length; i++) {
            if (CAT_KEYS[i].equals(bestCat)) {
                TextView btn = findViewById(CAT_VIEW_IDS[i]);
                if (btn != null) selectCategory(btn, bestCat);
                break;
            }
        }

        Toast.makeText(this, "✅ Detected: " + bestCat, Toast.LENGTH_SHORT).show();
    }

    private void selectCategory(TextView btn, String category) {
        detectedCategory = category;

        // Reset all buttons
        for (int id : CAT_VIEW_IDS) {
            View v = findViewById(id);
            if (v != null) {
                v.setBackground(getDrawable(R.drawable.bg_chip));
                if (v instanceof TextView) ((TextView) v).setTextColor(0xFFBBCCFF);
            }
        }
        // Highlight selected
        btn.setBackground(getDrawable(R.drawable.bg_btn_generate));
        btn.setTextColor(0xFFFFFFFF);
        selectedCatBtn = btn;

        generateContent(category);
    }

    private void generateContent(String category) {
        String[] hashtags = HashtagData.getHashtags(category);
        List<String> tagList = new ArrayList<>(Arrays.asList(hashtags));

        // Top 5 = first 5 (trending)
        List<String> top5 = new ArrayList<>(tagList.subList(0, Math.min(5, tagList.size())));
        List<String> rest  = new ArrayList<>(tagList.subList(Math.min(5, tagList.size()), tagList.size()));
        Collections.shuffle(rest);

        StringBuilder top5Str = new StringBuilder();
        for (String t : top5) top5Str.append(t).append("\n");
        tvTop5.setText(top5Str.toString().trim());

        StringBuilder allStr = new StringBuilder();
        for (String t : top5) allStr.append(t).append(" ");
        for (String t : rest)  allStr.append(t).append(" ");
        tvAllTags.setText(allStr.toString().trim());

        // Pick random caption from category
        String[] captions = HashtagData.getCaptions(category);
        int idx = new Random().nextInt(captions.length);
        tvCaption.setText(captions[idx]);

        layoutCaption.setVisibility(View.VISIBLE);
        layoutHashtags.setVisibility(View.VISIBLE);
    }

    private void refreshCaption() {
        String[] captions = HashtagData.getCaptions(detectedCategory);
        tvCaption.setText(captions[new Random().nextInt(captions.length)]);
    }

    private void copyText(String text, String msg) {
        if (text.isEmpty()) { Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show(); return; }
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("hashkit", text));
        Toast.makeText(this, "✅ " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
