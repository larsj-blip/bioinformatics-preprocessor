package com.github.larsj_blip.matchers;

import com.github.larsj_blip.AlignmentHandler;
import com.github.larsj_blip.LocalAlignmentHandler;
import com.github.larsj_blip.SuffixMatcher;
import com.github.larsj_blip.records.ExactMatchLocalAlignmentCosts;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import java.util.Arrays;
import java.util.List;

public class ExactSuffixMatcher implements SuffixMatcher {

    private final LocalAlignmentHandler localAlignmentHandler;

    public ExactSuffixMatcher(String adapterSequence) {
        this.localAlignmentHandler = LocalAlignmentHandler.builder()
                                         .withAdapterSequence(Arrays.stream(adapterSequence.split("(?!^)")).toList())
                                         .withCost(new ExactMatchLocalAlignmentCosts())
                                         .build();
    }

    @Override
    public AlignmentHandler getAlignmentHandler() {
        return localAlignmentHandler;
    }

    public String getPrefixForBestSuffixMatch(List<String> match) {
        return concatenateListToString(getAlignmentHandler().getMatchPrefix(match));
    }


    @Override
    public boolean suffixFulfillsCustomRequirement(Integer index, Integer matchScore) {
        return suffixIsCompleteMatchOfAdapterPrefix(index, matchScore);
    }

    private static boolean suffixIsCompleteMatchOfAdapterPrefix(Integer index, Integer matchScore) {
        return matchScore / 10 == index;
    }
}
