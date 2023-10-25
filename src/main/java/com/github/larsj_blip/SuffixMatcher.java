package com.github.larsj_blip;

import com.github.larsj_blip.records.DpTableLocation;
import com.github.larsj_blip.records.StringComparisonResult;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SuffixMatcher {

    default StringComparisonResult matchAgainst(String stringToMatchAgainst) {
        setStringToMatchAgainst(stringToMatchAgainst);
        var match = evaluateAlignments();
        return returnBestResultOrNothing(match);
    }

    default StringComparisonResult returnBestResultOrNothing(List<String> match) {
        if (match.size() > 1) {
            var matchPrefix = getPrefixForBestSuffixMatch(match);
            return new StringComparisonResult(true, match.size(), concatenateListToString(match), matchPrefix);
        }
        return new StringComparisonResult(false, 0, "", "");
    }

    default String concatenateListToString(List<String> match){
        var stringBuilder = new StringBuilder(40);
        match.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
    AlignmentHandler getAlignmentHandler();
    String getPrefixForBestSuffixMatch(List<String> match);

    default List<String> evaluateAlignments(){
        var result = getSuffixLocalAlignment();
        if (result.bestMatch() != 0) {
//            TODO: traverse DP table to get match!
            var amountOfMatchingLetters = getMatchLength();
            var stringToMatchAgainst = getAlignmentHandler().getStringToMatchAgainst();
            var sequenceLength = getAlignmentHandler().getStringToMatchAgainst().size();
            return stringToMatchAgainst.subList(sequenceLength - amountOfMatchingLetters - 1,
                                                sequenceLength);
        }
        return new ArrayList<>();
    }

    default int getMatchLength(){
        return getAlignmentHandler().getBestSuffixLength();
    }

    private SuffixAlignmentResult getSuffixLocalAlignment() {
        getAlignmentHandler().getLocalAlignment();
        int finalRowKey = getAlignmentHandler().getStringToMatchAgainst().size() - 1;
        var finalRow = getAlignmentHandler().getFinalRow();
        return getBestSuffixMatch(finalRow, finalRowKey);
    }
    private void setStringToMatchAgainst(String stringToMatchAgainst) {
        getAlignmentHandler().setStringToMatchAgainst(List.of(stringToMatchAgainst.split("(?!^)")));
    }

    default SuffixAlignmentResult getBestSuffixMatch(Map<Integer, Integer> finalRowOfDpTable, Integer finalRowKey) {

        var bestSuffixMatch = 0;
        SuffixAlignmentResult bestSuffixAlignmentResult;
        var columnKey = 0;
        for (var set: finalRowOfDpTable.entrySet()){
            if (set.getValue() > bestSuffixMatch && suffixFulfillsCustomRequirement(set.getKey(), set.getValue())) {
                bestSuffixMatch = set.getValue();
                columnKey = set.getKey();
            }
        }
        bestSuffixAlignmentResult = new SuffixAlignmentResult(bestSuffixMatch, new DpTableLocation(columnKey,
                                                                                                   finalRowKey));
        return bestSuffixAlignmentResult;
    }
    boolean suffixFulfillsCustomRequirement(Integer suffixMapKey, Integer suffixMapValue);
}
