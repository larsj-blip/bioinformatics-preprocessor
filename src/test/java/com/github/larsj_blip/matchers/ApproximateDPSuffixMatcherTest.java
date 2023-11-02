package com.github.larsj_blip.matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;


class ApproximateDPSuffixMatcherTest {


    private static final String STRING_TO_BE_MATCHED = "abcdefghij123456789000";
    private static final String STRING_TO_BE_MATCHED_WITHIN_MARGIN = "abcdefghij123456789100";
    private static final String ADAPTER_SEQUENCE= "123456789000";

    @Test
    void shouldReportExactSuffixMatchAsSuccess(){
        var matcher = new ApproximateSuffixMatcher(10, ADAPTER_SEQUENCE);
        var result = matcher.matchAgainst(STRING_TO_BE_MATCHED);
        assertThat(result.isMatch(), is(equalTo(true)));
    }

    @Test
    void shouldReportApproximateMatchWithinErrorMarginAsSuccess(){
        var matcher = new ApproximateSuffixMatcher(10, ADAPTER_SEQUENCE);
        var result = matcher.matchAgainst(STRING_TO_BE_MATCHED_WITHIN_MARGIN);
        assertThat(result.isMatch(), is(equalTo(true)));
        assertThat(result.matchPrefix(), is(equalTo("abcdefghij")));
    }

}