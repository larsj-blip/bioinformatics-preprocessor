package com.github.larsj_blip;

import com.github.larsj_blip.records.BestMatchLocation;
import com.github.larsj_blip.records.StringComparisonResult;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import java.util.List;
import java.util.Map;

public interface SuffixMatcher {

    public StringComparisonResult matchAgainst(String stringToMatchAgainst);

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
        bestSuffixAlignmentResult = new SuffixAlignmentResult(bestSuffixMatch, new BestMatchLocation(columnKey,
                                                                                                     finalRowKey));
        return bestSuffixAlignmentResult;
    }
    public boolean suffixFulfillsCustomRequirement(Integer suffixMapKey, Integer suffixMapValue);
}
