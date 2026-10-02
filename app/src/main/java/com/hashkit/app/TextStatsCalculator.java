package com.hashkit.app;

/**
 * Pure character/word/line counting logic for the Character Counter screen,
 * pulled out of CharCounterActivity so it can be unit-tested without
 * touching any Android views.
 */
public final class TextStatsCalculator {

    public static final int CAPTION_LIMIT = 2200;
    public static final int BIO_LIMIT = 150;
    public static final int COMMENT_LIMIT = 2200;

    private TextStatsCalculator() {}

    public static final class Stats {
        public final int chars;
        public final int words;
        public final int lines;

        public Stats(int chars, int words, int lines) {
            this.chars = chars;
            this.words = words;
            this.lines = lines;
        }
    }

    public static Stats calculate(String text) {
        if (text == null) text = "";
        int chars = text.length();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int lines = text.isEmpty() ? 0 : text.split("\n", -1).length;
        return new Stats(chars, words, lines);
    }

    /** Progress bar value for a given character count against a limit, capped at the limit. */
    public static int progressFor(int chars, int limit) {
        return Math.min(chars, limit);
    }

    public static boolean isOverLimit(int chars, int limit) {
        return chars > limit;
    }
}
