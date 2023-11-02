package com.github.larsj_blip.matchers;

import com.github.larsj_blip.AlignmentHandler;
import com.github.larsj_blip.LocalAlignmentHandler;
import com.github.larsj_blip.SuffixMatcher;
import com.github.larsj_blip.records.ApproximateMatchLocalAlignmentCosts;
import java.util.Arrays;
import java.util.List;

public class ApproximateSuffixMatcher implements SuffixMatcher {

    private final int errorMarginPercent;
    private final LocalAlignmentHandler matcher;
    private final int smallestAllowableScorePercent;
    private int amountOfMismatches;
    public ApproximateSuffixMatcher(int errorMarginPercent, String adapterSequence) {
        this.errorMarginPercent = errorMarginPercent;
        this.matcher = LocalAlignmentHandler.builder()
                           .withCost(new ApproximateMatchLocalAlignmentCosts())
                           .withAdapterSequence(Arrays.stream(adapterSequence.split("(?!^)")).toList())
                           .build();
        smallestAllowableScorePercent = (((100 - errorMarginPercent) * matcher.getCost().getMatchCost()
                                          + errorMarginPercent * matcher.getCost().getMismatchCost()) / 100);
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
        return suffixIsWithinErrorMarginRange(suffixMapValue);
    }

    private boolean suffixIsWithinErrorMarginRange(Integer suffixMapValue) {
        var matchLength = getMatchLength();
        var amountOfMismatches = getAmountOfMismatches(matchLength);

        var MismatchesRelativeToMatches = (amountOfMismatches / matchLength)*100;
        return MismatchesRelativeToMatches <= errorMarginPercent ;
    }

    private double getAmountOfMismatches(int matchLength) {
        var stringToMatchAgainst = matcher.getStringToMatchAgainst();
        var suffixIndex = stringToMatchAgainst.size() - matchLength;
        var amountOfMismatches = 0;
        for(var index = suffixIndex; index < stringToMatchAgainst.size(); index++){
            var adapterSequenceIndex = index - suffixIndex;
            if (!stringToMatchAgainst.get(index).equals(matcher.getAdapterSequence().get(adapterSequenceIndex))){
                amountOfMismatches++;
            }
        }
        return amountOfMismatches;
    }
}
