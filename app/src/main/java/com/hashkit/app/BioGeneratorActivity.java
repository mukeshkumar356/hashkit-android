package com.hashkit.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BioGeneratorActivity extends AppCompatActivity {

    private static final int[] NICHE_IDS = {
        R.id.niche_influencer, R.id.niche_photographer, R.id.niche_entrepreneur,
        R.id.niche_fitness, R.id.niche_chef, R.id.niche_artist,
        R.id.niche_traveler, R.id.niche_fashion, R.id.niche_motivator,
        R.id.niche_beauty, R.id.niche_tech, R.id.niche_musician
    };
    private static final String[] NICHE_KEYS = {
        "influencer", "photographer", "entrepreneur",
        "fitness", "chef", "artist",
        "traveler", "fashion", "motivator",
        "beauty", "tech", "musician"
    };

    private static final String[][] BIOS = {
        // influencer
        {"✨ Creating content that inspires | Collab → DM 📩", "🌟 Turning moments into memories | Brand deals ✉️", "📱 Content Creator | Building communities one post at a time", "🔥 Lifestyle & Inspiration | Let's grow together 🚀", "💫 Your daily dose of good vibes | DM for collabs"},
        // photographer
        {"📸 Capturing life one frame at a time 🌍", "🎞️ Photographer | Finding beauty in ordinary moments", "📷 Visual storyteller | Canon shooter 🌄", "🖼️ Freezing time through my lens | Bookings open ✉️", "📸 Documentary photographer | Real moments, real emotions"},
        // entrepreneur
        {"🚀 Building empires one day at a time 💼", "💡 Entrepreneur | Turning ideas into income streams", "💼 CEO mindset | Hustling daily for financial freedom 🔥", "📈 Business builder | Multiple income streams | Mentoring 🎯", "🏆 Serial entrepreneur | Disrupting industries | DM to connect"},
        // fitness
        {"💪 Fitness coach | Transforming bodies & minds 🔥", "🏋️ Gym addict | Helping you achieve your dream body 💯", "🌿 Fitness & wellness | Progress over perfection every day", "⚡ Personal trainer | Nutrition | No excuses mentality 💪", "🎯 Fit life | Body transformation | Coach → DM 📩"},
        // chef
        {"🍳 Chef | Making food an experience, not just a meal ✨", "🌮 Food lover | Sharing recipes that make you smile 😋", "👨‍🍳 Professional chef | Farm to table philosophy 🌿", "🍕 Foodie | Exploring cuisines around the world ✈️", "🥘 Home cook | Easy recipes for busy people | Daily posts"},
        // artist
        {"🎨 Artist | Painting emotions on canvas 🖌️", "✏️ Illustrator | Turning imagination into reality", "🖼️ Digital artist | Available for commissions ✉️", "🎭 Creative soul | Art is my language 🌈", "🖌️ Abstract artist | Every piece tells a story | Art for sale"},
        // traveler
        {"✈️ Exploring the world one country at a time 🌍", "🗺️ Wanderer | 40+ countries | Travel tips daily ✨", "🏔️ Adventure seeker | Mountains, beaches & everything in between", "🌅 Travel blogger | Budget travel tips | 🎒 Full-time nomad", "🧳 Passport addict | Turning wanderlust into destinations"},
        // fashion
        {"👗 Fashion lover | Style is my superpower 💫", "💃 Style blogger | Outfit inspo daily | Collab ✉️", "🛍️ Fashionista | Because life's too short for boring outfits 🔥", "👠 Fashion & lifestyle | Dressing up is self-care 💕", "✨ Style curator | Helping you look your best every day"},
        // motivator
        {"🔥 Motivational speaker | Helping you become your best self", "💯 Life coach | Mindset shifts that create millionaires 🚀", "⚡ Daily motivation | Because your dreams deserve a shot 🎯", "🌟 Inspire. Grow. Conquer. | Join the movement 💪", "🧠 Mindset mentor | Change your thoughts, change your life"},
        // beauty
        {"💄 Beauty blogger | Honest reviews & tutorials ✨", "🌸 Makeup artist | Beauty is for everyone 💕", "💅 Skincare + makeup | Glowing inside and out 🌟", "💋 MUA | Beauty tips that actually work | Bookings open ✉️", "✨ Clean beauty advocate | Natural glow is always in 🌿"},
        // tech
        {"💻 Tech enthusiast | Breaking down complex tech simply 🔧", "🤖 AI & software developer | Building the future one line at a time", "📱 App developer | Startup founder | Code is my art 🎨", "⚡ Full-stack dev | Open source contributor | Tech tips daily", "🖥️ Tech reviewer | Gadgets, apps & productivity hacks 🚀"},
        // musician
        {"🎵 Musician | Music is my therapy and my message 🎶", "🎸 Singer-songwriter | Releasing original music every month ✨", "🎹 Pianist | Creating melodies that touch the soul 💫", "🎤 Vocalist | Studio life | Collabs → DM 📩", "🎼 Composer | Turning emotions into music 🌊"}
    };

    private View selectedNicheBtn = null;
    private View layoutBios;
    private TextView[] tvBios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_generator);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bio Generator");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutBios = findViewById(R.id.layout_bios);
        tvBios = new TextView[]{
            findViewById(R.id.tv_bio1), findViewById(R.id.tv_bio2),
            findViewById(R.id.tv_bio3), findViewById(R.id.tv_bio4),
            findViewById(R.id.tv_bio5)
        };

        int[] copyIds = {R.id.btn_copy_bio1, R.id.btn_copy_bio2, R.id.btn_copy_bio3, R.id.btn_copy_bio4, R.id.btn_copy_bio5};
        for (int i = 0; i < copyIds.length; i++) {
            final int idx = i;
            findViewById(copyIds[i]).setOnClickListener(v -> copyBio(idx));
        }

        for (int i = 0; i < NICHE_IDS.length; i++) {
            final int idx = i;
            TextView btn = findViewById(NICHE_IDS[i]);
            if (btn != null) btn.setOnClickListener(v -> selectNiche(btn, idx));
        }
    }

    private void selectNiche(TextView btn, int idx) {
        for (int id : NICHE_IDS) {
            View v = findViewById(id);
            if (v != null) {
                v.setBackground(getDrawable(R.drawable.bg_chip));
                if (v instanceof TextView) ((TextView) v).setTextColor(0xFFBBCCFF);
            }
        }
        btn.setBackground(getDrawable(R.drawable.bg_btn_generate));
        btn.setTextColor(0xFFFFFFFF);
        selectedNicheBtn = btn;

        String[] bios = BIOS[idx];
        for (int i = 0; i < tvBios.length; i++) {
            tvBios[i].setText(bios[i]);
        }
        layoutBios.setVisibility(View.VISIBLE);
    }

    private void copyBio(int idx) {
        String text = tvBios[idx].getText().toString();
        if (text.isEmpty()) return;
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("hashkit_bio", text));
        Toast.makeText(this, "✅ Bio copied!", Toast.LENGTH_SHORT).show();
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
