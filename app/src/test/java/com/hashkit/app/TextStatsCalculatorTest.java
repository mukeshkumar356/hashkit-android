package com.hashkit.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class TextStatsCalculatorTest {

    @Test
    public void emptyText_hasZeroCountsOnEverything() {
        TextStatsCalculator.Stats stats = TextStatsCalculator.calculate("");
        assertEquals(0, stats.chars);
        assertEquals(0, stats.words);
        assertEquals(0, stats.lines);
    }

    @Test
    public void nullText_treatedSameAsEmpty() {
        TextStatsCalculator.Stats stats = TextStatsCalculator.calculate(null);
        assertEquals(0, stats.chars);
        assertEquals(0, stats.words);
        assertEquals(0, stats.lines);
    }

    @Test
    public void singleLineText_countsCharsWordsAndOneLine() {
        TextStatsCalculator.Stats stats = TextStatsCalculator.calculate("Hello world");
        assertEquals(11, stats.chars);
        assertEquals(2, stats.words);
        assertEquals(1, stats.lines);
    }

    @Test
    public void multiLineText_countsEveryLineIncludingTrailingEmptyOnes() {
        TextStatsCalculator.Stats stats = TextStatsCalculator.calculate("line one\nline two\n");
        assertEquals(3, stats.lines); // "line one", "line two", "" (trailing newline)
    }

    @Test
    public void whitespaceOnlyText_countsZeroWordsButNonZeroChars() {
        TextStatsCalculator.Stats stats = TextStatsCalculator.calculate("   ");
        assertEquals(3, stats.chars);
        assertEquals(0, stats.words);
    }

    @Test
    public void progressFor_capsAtTheLimitEvenWhenTextIsLonger() {
        assertEquals(150, TextStatsCalculator.progressFor(500, TextStatsCalculator.BIO_LIMIT));
        assertEquals(100, TextStatsCalculator.progressFor(100, TextStatsCalculator.BIO_LIMIT));
    }

    @Test
    public void isOverLimit_trueOnlyStrictlyAboveTheLimit() {
        assertFalse(TextStatsCalculator.isOverLimit(150, TextStatsCalculator.BIO_LIMIT));
        assertTrue(TextStatsCalculator.isOverLimit(151, TextStatsCalculator.BIO_LIMIT));
    }

    @Test
    public void captionAndCommentLimits_areBothTwoThousandTwoHundred() {
        assertEquals(2200, TextStatsCalculator.CAPTION_LIMIT);
        assertEquals(2200, TextStatsCalculator.COMMENT_LIMIT);
    }
}
