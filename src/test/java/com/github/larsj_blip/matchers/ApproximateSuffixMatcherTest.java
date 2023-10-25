package com.github.larsj_blip.matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;


class ApproximateSuffixMatcherTest {


    private static final String STRING_TO_BE_MATCHED = "abcdefghijkkkkkkkkkk";
    private static final String ADAPTER_SEQUENCE= "kkkkkkkkki";

    @Test
    void shouldReportExactSuffixMatchAsSuccess(){
        var matcher = new ApproximateSuffixMatcher(10, ADAPTER_SEQUENCE);
        var result = matcher.matchAgainst(STRING_TO_BE_MATCHED);
        assertThat(result.isMatch(), is(equalTo(true)));
    }

}