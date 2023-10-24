package com.github.larsj_blip.matchers;

import com.github.larsj_blip.LocalAlignmentMatcher;
import com.github.larsj_blip.SuffixMatcher;
import com.github.larsj_blip.records.ApproximateMatchLocalAlignmentCosts;
import com.github.larsj_blip.records.StringComparisonResult;
import java.util.Arrays;

public class ApproximateSuffixMatcher implements SuffixMatcher {
    private final double errorMargin;
    private final LocalAlignmentMatcher matcher;

    public ApproximateSuffixMatcher(double errorMargin, String adapterSequence) {
        this.matcher = LocalAlignmentMatcher.builder()
                           .withCost(new ApproximateMatchLocalAlignmentCosts())
                           .withAdapterSequence(Arrays.stream(adapterSequence.split("(?!^)")).toList())
                           .build();
        this.errorMargin = errorMargin;
    }

    @Override
    public StringComparisonResult matchAgainst(String stringToMatchAgainst) {
        matcher.setStringToMatchAgainst(Arrays.stream(stringToMatchAgainst.split("(?!^)")).toList());
        return new StringComparisonResult(false,0, "","" );
    }

    @Override
    public boolean suffixFulfillsCustomRequirement(Integer suffixMapKey, Integer suffixMapValue) {
        return suffixMapValue > (9*matcher.getCost().getMatchCost()-matcher.getCost().getMismatchCost())/errorMargin;
    }
}
