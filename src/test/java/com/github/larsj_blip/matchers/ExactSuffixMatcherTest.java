package com.github.larsj_blip.matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import com.github.larsj_blip.matchers.ExactSuffixMatcher;
import org.junit.jupiter.api.Test;

public class ExactSuffixMatcherTest {

    public static final String TEST_ADAPTER_SEQUENCE = "tgtatat";
    public static final String TEST_STRING_TO_MATCH_AGAINST = "atcttaacatgtatgtatat";
    public static final String TEST_STRING_TO_MATCH_AGAINST_CONTAINING_PARTIAL_PREFIX = "atcttaacatgtatgtat";
    public static final String TEST_STRING_TO_MATCH_AGAINST_WITHOUT_PREFIX = "atcttaacatgta";

    @Test
    void shouldReturnMatchIfStringSuffixContainsEntireAdapter() {
        var suffixMatcher = new ExactSuffixMatcher(TEST_ADAPTER_SEQUENCE);
        var matchResults = suffixMatcher.matchAgainst(TEST_STRING_TO_MATCH_AGAINST);
        assertThat(matchResults.isMatch(), is(true));
    }

    @Test
    void shouldReturnMatchIfStringSuffixContainsExactMatchOfPrefixOfAdapter() {
        var suffixMatcher = new ExactSuffixMatcher(TEST_ADAPTER_SEQUENCE);
        var matchResults = suffixMatcher.matchAgainst(TEST_STRING_TO_MATCH_AGAINST_CONTAINING_PARTIAL_PREFIX);
        assertThat(matchResults.isMatch(), is(true));
    }

    @Test
    void shouldReturnLengthOfMatch() {
        var suffixMatcher = new ExactSuffixMatcher(TEST_ADAPTER_SEQUENCE);
        var matchResults = suffixMatcher.matchAgainst(TEST_STRING_TO_MATCH_AGAINST);
        assertThat(matchResults.matchLength(), is(equalTo(TEST_ADAPTER_SEQUENCE.length())));
    }

    @Test
    void shouldPersistAdapterSequenceMatchAndRemainingSequenceSeparately() {
        var suffixMatcher = new ExactSuffixMatcher(TEST_ADAPTER_SEQUENCE);
        var matchResults = suffixMatcher.matchAgainst(TEST_STRING_TO_MATCH_AGAINST);
        assertThat(matchResults.matchPrefix(), is(equalTo(TEST_STRING_TO_MATCH_AGAINST_WITHOUT_PREFIX)));
    }


}
