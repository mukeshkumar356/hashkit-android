package com.hashkit.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.*;

public class HashtagSearchActivity extends AppCompatActivity {

    private EditText etKeyword;
    private LinearLayout containerEasy, containerMedium, containerHard;
    private View scrollCards, layoutEmpty, layoutActions;
    private TextView tvSelectedCount, tabSimilar, tabRelated;

    private final Set<String> selectedTags = new LinkedHashSet<>();
    private boolean showingSimilar = true;
    private String lastKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag_search);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Hashtag Search");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etKeyword       = findViewById(R.id.et_keyword);
        containerEasy   = findViewById(R.id.container_easy);
        containerMedium = findViewById(R.id.container_medium);
        containerHard   = findViewById(R.id.container_hard);
        scrollCards     = findViewById(R.id.scroll_cards);
        layoutEmpty     = findViewById(R.id.layout_empty);
        layoutActions   = findViewById(R.id.layout_actions);
        tvSelectedCount = findViewById(R.id.tv_selected_count);
        tabSimilar      = findViewById(R.id.tab_similar);
        tabRelated      = findViewById(R.id.tab_related);

        findViewById(R.id.btn_search).setOnClickListener(v -> doSearch());
        etKeyword.setOnEditorActionListener((v, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { doSearch(); return true; }
            return false;
        });

        tabSimilar.setOnClickListener(v -> switchTab(true));
        tabRelated.setOnClickListener(v -> switchTab(false));

        findViewById(R.id.btn_smart_select).setOnClickListener(v -> smartSelect());
        findViewById(R.id.btn_copy_selected).setOnClickListener(v -> copySelected());
    }

    private void switchTab(boolean similar) {
        showingSimilar = similar;
        tabSimilar.setBackground(similar ? getDrawable(R.drawable.bg_btn_generate) : getDrawable(R.drawable.bg_chip));
        tabSimilar.setTextColor(similar ? 0xFFFFFFFF : 0xFFBBCCFF);
        tabRelated.setBackground(similar ? getDrawable(R.drawable.bg_chip) : getDrawable(R.drawable.bg_btn_generate));
        tabRelated.setTextColor(similar ? 0xFFBBCCFF : 0xFFFFFFFF);
        if (!lastKeyword.isEmpty()) loadHashtags(lastKeyword);
    }

    private void doSearch() {
        String kw = etKeyword.getText().toString().trim();
        if (kw.isEmpty()) { Toast.makeText(this, "Please enter a keyword", Toast.LENGTH_SHORT).show(); return; }
        lastKeyword = kw;
        selectedTags.clear();
        updateSelectedCount();
        loadHashtags(kw);
    }

    private void loadHashtags(String keyword) {
        List<HashtagItem> tags = showingSimilar
            ? generateSimilar(keyword)
            : generateRelated(keyword);

        List<HashtagItem> easy   = new ArrayList<>();
        List<HashtagItem> medium = new ArrayList<>();
        List<HashtagItem> hard   = new ArrayList<>();

        for (HashtagItem item : tags) {
            if      (item.count > 1_000_000) hard.add(item);
            else if (item.count > 100_000)   medium.add(item);
            else                             easy.add(item);
        }

        containerEasy.removeAllViews();
        containerMedium.removeAllViews();
        containerHard.removeAllViews();

        fillContainer(containerEasy,   easy,   R.drawable.bg_chip_easy);
        fillContainer(containerMedium, medium, R.drawable.bg_chip_med);
        fillContainer(containerHard,   hard,   R.drawable.bg_chip_hard);

        scrollCards.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutActions.setVisibility(View.VISIBLE);
    }

    private void fillContainer(LinearLayout container, List<HashtagItem> items, int bgRes) {
        // 2 chips per row
        LinearLayout row = null;
        for (int i = 0; i < items.size(); i++) {
            if (i % 2 == 0) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rowLp.bottomMargin = dp(6);
                row.setLayoutParams(rowLp);
                container.addView(row);
            }
            row.addView(makeChip(items.get(i), bgRes));
        }
    }

    private View makeChip(HashtagItem item, int defaultBgRes) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMarginEnd(dp(4));
        tv.setLayoutParams(lp);
        tv.setText(item.tag + "\n" + item.formattedCount());
        tv.setTextColor(0xFFEEEEFF);
        tv.setTextSize(11f);
        tv.setPadding(dp(8), dp(7), dp(8), dp(7));
        tv.setBackground(getDrawable(selectedTags.contains(item.tag) ? R.drawable.bg_chip_selected : defaultBgRes));
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setTag(new Object[]{item, defaultBgRes});

        tv.setOnClickListener(v -> {
            if (selectedTags.contains(item.tag)) {
                selectedTags.remove(item.tag);
                tv.setBackground(getDrawable(defaultBgRes));
            } else {
                selectedTags.add(item.tag);
                tv.setBackground(getDrawable(R.drawable.bg_chip_selected));
            }
            updateSelectedCount();
        });
        return tv;
    }

    private void smartSelect() {
        // Clear previous selection
        selectedTags.clear();
        refreshAllChips();

        // Rebuild tag lists
        List<HashtagItem> tags = showingSimilar ? generateSimilar(lastKeyword) : generateRelated(lastKeyword);
        List<HashtagItem> easy = new ArrayList<>(), medium = new ArrayList<>(), hard = new ArrayList<>();
        for (HashtagItem it : tags) {
            if      (it.count > 1_000_000) hard.add(it);
            else if (it.count > 100_000)   medium.add(it);
            else                           easy.add(it);
        }

        // Smart mix: 3 Hard + 10 Medium + 7 Easy = 20 tags (Instagram optimal)
        int added = 0;
        for (int i = 0; i < Math.min(3, hard.size());   i++) { selectedTags.add(hard.get(i).tag);   added++; }
        for (int i = 0; i < Math.min(10, medium.size()); i++) { selectedTags.add(medium.get(i).tag); added++; }
        for (int i = 0; i < Math.min(7, easy.size());   i++) { selectedTags.add(easy.get(i).tag);   added++; }

        refreshAllChips();
        updateSelectedCount();
        Toast.makeText(this, "⚡ Smart selected " + selectedTags.size() + " hashtags!", Toast.LENGTH_SHORT).show();
    }

    private void refreshAllChips() {
        refreshChipsIn(containerEasy,   R.drawable.bg_chip_easy);
        refreshChipsIn(containerMedium, R.drawable.bg_chip_med);
        refreshChipsIn(containerHard,   R.drawable.bg_chip_hard);
    }

    private void refreshChipsIn(LinearLayout container, int defaultBg) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View rowView = container.getChildAt(i);
            if (!(rowView instanceof LinearLayout)) continue;
            LinearLayout row = (LinearLayout) rowView;
            for (int j = 0; j < row.getChildCount(); j++) {
                View child = row.getChildAt(j);
                if (child instanceof TextView tv) {
                    Object[] tag = (Object[]) tv.getTag();
                    if (tag == null) continue;
                    HashtagItem item = (HashtagItem) tag[0];
                    tv.setBackground(getDrawable(
                        selectedTags.contains(item.tag) ? R.drawable.bg_chip_selected : defaultBg));
                }
            }
        }
    }

    private void copySelected() {
        if (selectedTags.isEmpty()) {
            Toast.makeText(this, "Tap hashtags to select, or use Smart Select", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String t : selectedTags) sb.append(t).append(" ");
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("hashkit", sb.toString().trim()));
        Toast.makeText(this, "✅ " + selectedTags.size() + " hashtags copied!", Toast.LENGTH_SHORT).show();
    }

    private void updateSelectedCount() {
        tvSelectedCount.setText(selectedTags.size() + " selected");
    }

    // ── Hashtag generation ──

    private List<HashtagItem> generateSimilar(String keyword) {
        String kw = keyword.toLowerCase().trim().replaceAll("\\s+", "");
        List<String> tags = new ArrayList<>(Arrays.asList(
            "#" + kw,
            "#" + kw + "s",
            "#" + kw + "life",
            "#" + kw + "love",
            "#" + kw + "photography",
            "#" + kw + "gram",
            "#" + kw + "daily",
            "#" + kw + "photo",
            "#" + kw + "oftheday",
            "#" + kw + "art",
            "#" + kw + "world",
            "#" + kw + "style",
            "#" + kw + "vibes",
            "#" + kw + "community",
            "#" + kw + "lovers",
            "#" + kw + "day",
            "#" + kw + "inspo",
            "#" + kw + "mode",
            "#best" + kw,
            "#my" + kw,
            "#insta" + kw,
            "#" + kw + "hub",
            "#" + kw + "pics",
            "#" + kw + "shots",
            "#" + kw + "fan",
            "#" + kw + "fanpage",
            "#" + kw + "post",
            "#" + kw + "blog",
            "#" + kw + "tips",
            "#" + kw + "goals"
        ));
        return toItems(tags);
    }

    private List<HashtagItem> generateRelated(String keyword) {
        String cat = HashtagData.labelToCategory(keyword);
        String[] catTags = HashtagData.getHashtags(cat);

        // General trending tags
        String[] general = {
            "#instagood", "#photooftheday", "#trending", "#viral", "#explore",
            "#instagram", "#photography", "#beautiful", "#happy", "#love",
            "#follow", "#picoftheday", "#instalike", "#instadaily", "#reels",
            "#content", "#contentcreator", "#influencer", "#fyp", "#trendingnow"
        };

        List<String> tags = new ArrayList<>(Arrays.asList(catTags));
        tags.addAll(Arrays.asList(general));
        // Remove duplicates, limit to 30
        Set<String> seen = new LinkedHashSet<>(tags);
        List<String> unique = new ArrayList<>(seen);
        return toItems(unique.subList(0, Math.min(30, unique.size())));
    }

    private List<HashtagItem> toItems(List<String> tags) {
        List<HashtagItem> result = new ArrayList<>();
        for (String tag : tags) result.add(new HashtagItem(tag, postCount(tag)));
        return result;
    }

    private static int postCount(String tag) {
        long h = 0;
        for (char c : tag.toCharArray()) h = h * 31 + c;
        h = Math.abs(h);
        // Bucket: 40% Easy, 40% Medium, 20% Hard
        int bucket = (int)(h % 10);
        if (bucket < 4) {
            return (int)(5_000   + ((h / 10) % 90_000));   // Easy   5k–95k
        } else if (bucket < 8) {
            return (int)(100_000 + ((h / 10) % 850_000));  // Medium 100k–950k
        } else {
            return (int)(1_000_000 + ((h / 10) % 4_000_000)); // Hard 1M–5M
        }
    }

    private int dp(int v) {
        return (int)(v * getResources().getDisplayMetrics().density);
    }

    // ── Data model ──
    static class HashtagItem {
        final String tag;
        final int count;
        HashtagItem(String tag, int count) { this.tag = tag; this.count = count; }
        String formattedCount() {
            if (count >= 1_000_000) return String.format(Locale.US, "%.1fM", count / 1_000_000.0);
            if (count >= 1_000)     return (count / 1_000) + "k";
            return String.valueOf(count);
        }
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
