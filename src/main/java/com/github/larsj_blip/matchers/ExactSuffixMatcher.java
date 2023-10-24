package com.github.larsj_blip.matchers;

import com.github.larsj_blip.LocalAlignmentMatcher;
import com.github.larsj_blip.records.BestMatchLocation;
import com.github.larsj_blip.records.ExactMatchLocalAlignmentCosts;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import com.github.larsj_blip.records.StringComparisonResult;
import com.github.larsj_blip.SuffixMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ExactSuffixMatcher implements SuffixMatcher {

    private final LocalAlignmentMatcher localAlignmentMatcher;

    public ExactSuffixMatcher(String adapterSequence) {
        this.localAlignmentMatcher = LocalAlignmentMatcher.builder()
                                         .withAdapterSequence(Arrays.stream(adapterSequence.split("(?!^)")).toList())
                                         .withCost(new ExactMatchLocalAlignmentCosts())
                                         .build();
    }

    public StringComparisonResult matchAgainst(String stringToMatchAgainst) {
        this.localAlignmentMatcher.setStringToMatchAgainst(List.of(stringToMatchAgainst.split("(?!^)")));
        var match = getLongestSuffixMatch();
        if (match.size() > 1) {
            var matchPrefix = localAlignmentMatcher.getMatchPrefix(match);
            return new StringComparisonResult(true, match.size(), concatenateListToString(match), matchPrefix);
        }
        return new StringComparisonResult(false, 0, "","");
    }


    private static String concatenateListToString(List<String> prefix) {
        var stringBuilder = new StringBuilder(40);
        prefix.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }

    private List<String> getLongestSuffixMatch() {
        var result = getSuffixLocalAlignment();
        if (result.bestMatch() != 0) {
            var exactMatchLength = result.bestMatch()/10;
            var stringToMatchAgainst = localAlignmentMatcher.getStringToMatchAgainst();
            var sequenceLength = localAlignmentMatcher.getStringToMatchAgainst().size();
            return stringToMatchAgainst.subList(sequenceLength - exactMatchLength - 1,
                                                sequenceLength);
        }
        return new ArrayList<>();
    }

    private SuffixAlignmentResult getSuffixLocalAlignment() {
        localAlignmentMatcher.getLocalAlignment();
        int finalRowKey = localAlignmentMatcher.getStringToMatchAgainst().size() - 1;
        var finalRow = localAlignmentMatcher.getDpTable().row(finalRowKey);
        return getBestSuffixMatch(finalRow, finalRowKey);
    }



    @Override
    public boolean suffixFulfillsCustomRequirement(Integer index, Integer matchScore) {
        return suffixIsCompleteMatchOfAdapterPrefix(index, matchScore);
    }

    private static boolean suffixIsCompleteMatchOfAdapterPrefix(Integer index, Integer matchScore) {
        return matchScore / 10 == index;
    }
}
