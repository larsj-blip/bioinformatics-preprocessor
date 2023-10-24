package com.github.larsj_blip.matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import com.github.larsj_blip.LocalAlignmentMatcher;
import java.util.Arrays;
import org.junit.jupiter.api.Test;


class ApproximateSuffixMatcherTest {


    private static final String STRING_TO_BE_MATCHED = "abcdefghijk";
    private static final String ADAPTER_SEQUENCE= "ijk";

    @Test
    void shouldReportExactSuffixMatchAsSuccess(){
        var matcher = new ApproximateSuffixMatcher(0.1, ADAPTER_SEQUENCE);
        var result = matcher.matchAgainst(STRING_TO_BE_MATCHED);
        assertThat(result.isMatch(), is(equalTo(true)));
    }

}