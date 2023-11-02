package com.github.larsj_blip;

import com.github.larsj_blip.records.DpTableLocation;
import com.github.larsj_blip.records.StringComparisonResult;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SuffixMatcher {

    int NO_MATCHING_LETTERS_IN_SUFFIX = 0;

    default StringComparisonResult matchAgainst(String stringToMatchAgainst) {
        setStringToMatchAgainst(stringToMatchAgainst);
        var match = evaluateAlignments();
        return returnBestResultOrNothing(match);
    }
// this should return optional if it may be null?? or not
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
        if (result.bestMatchScore() != 0) {
            var amountOfMatchingLetters = getMatchLength();
            var stringToMatchAgainst = getAlignmentHandler().getStringToMatchAgainst();
            var sequenceLength = getAlignmentHandler().getStringToMatchAgainst().size();
            int fromIndex = sequenceLength - amountOfMatchingLetters;
            return stringToMatchAgainst.subList(fromIndex,
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

        var bestSuffixMatch = NO_MATCHING_LETTERS_IN_SUFFIX;
        var columnKey = finalRowOfDpTable.size()-1;
        Integer bestSuffixScore = finalRowOfDpTable.get(columnKey);
        boolean isGreaterThanCurrentMax = bestSuffixScore > NO_MATCHING_LETTERS_IN_SUFFIX;
        boolean suffixFulfillsCustomRequirement = suffixFulfillsCustomRequirement(columnKey,bestSuffixScore);
        if (isGreaterThanCurrentMax && suffixFulfillsCustomRequirement) {
            bestSuffixMatch = bestSuffixScore;
        }

        return new SuffixAlignmentResult(bestSuffixMatch, new DpTableLocation(columnKey,
                                                                              finalRowKey));
    }
    boolean suffixFulfillsCustomRequirement(Integer suffixMapKey, Integer suffixMapValue);
}
