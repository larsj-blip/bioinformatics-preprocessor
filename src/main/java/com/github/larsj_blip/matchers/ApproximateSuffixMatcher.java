package com.github.larsj_blip.matchers;

import com.github.larsj_blip.AlignmentHandler;
import com.github.larsj_blip.LocalAlignmentHandler;
import com.github.larsj_blip.SuffixMatcher;
import com.github.larsj_blip.records.ApproximateMatchLocalAlignmentCosts;
import java.util.Arrays;
import java.util.List;

public class ApproximateSuffixMatcher implements SuffixMatcher {

    private final LocalAlignmentHandler matcher;
    private final int smallestAllowableScore;

    public ApproximateSuffixMatcher(int errorMargin, String adapterSequence) {
        this.matcher = LocalAlignmentHandler.builder()
                           .withCost(new ApproximateMatchLocalAlignmentCosts())
                           .withAdapterSequence(Arrays.stream(adapterSequence.split("(?!^)")).toList())
                           .build();
        smallestAllowableScore = ((100 * matcher.getCost().getMatchCost()
                                   - matcher.getCost().getMatchCost() * errorMargin) + errorMargin * matcher.getCost()
                                                                                                         .getMismatchCost())
                                 / 100;
    }

    @Override
    public AlignmentHandler getAlignmentHandler() {
        return matcher;
    }

    @Override
    public String getPrefixForBestSuffixMatch(List<String> match) {
        return concatenateListToString(matcher.getMatchPrefix(match));
    }

    @Override
    public boolean suffixFulfillsCustomRequirement(Integer suffixMapKey, Integer suffixMapValue) {

        return suffixMapValue >= smallestAllowableScore * getMatchLength();
    }
}
