package com.github.larsj_blip.matchers;

import com.github.larsj_blip.records.BestMatchLocation;
import com.github.larsj_blip.records.ExactMatchLocalAlignmentCosts;
import com.github.larsj_blip.records.ExactSuffixAlignmentResult;
import com.github.larsj_blip.records.StringComparisonResult;
import com.github.larsj_blip.SuffixMatcher;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class ExactSuffixMatcher implements SuffixMatcher {

    private final List<String> adapterSequence;
    private Table<Integer, Integer, Integer> dpTable;
    private List<String> stringToMatchAgainst;

    public ExactSuffixMatcher(String adapterSequence) {
        this.adapterSequence = List.of(adapterSequence.split("(?!^)"));
    }

    public StringComparisonResult matchAgainst(String stringToMatchAgainst) {
        this.stringToMatchAgainst = List.of(stringToMatchAgainst.split("(?!^)"));
        initializeDpTable();
        var match = getLongestExactSuffixMatch();
        if (match.size() > 1) {
            var matchPrefix = getMatchPrefix(match);
            return new StringComparisonResult(true, match.size(), concatenateListToString(match), matchPrefix);
        }
        return new StringComparisonResult(false, 0, "","");
    }

    private String getMatchPrefix(List<String> match) {
        var prefix = this.stringToMatchAgainst.subList(0, this.stringToMatchAgainst.size() - match.size());
        return concatenateListToString(prefix);
    }

    private static String concatenateListToString(List<String> prefix) {
        var stringBuilder = new StringBuilder(40);
        prefix.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }

    private List<String> getLongestExactSuffixMatch() {
        var result = getBestMatch();
        if (result.bestMatch() != 0) {
            var exactMatchLength = result.bestMatch()/10;
            return this.stringToMatchAgainst.subList(stringToMatchAgainst.size()- exactMatchLength-1,
                                                     stringToMatchAgainst.size());
        }
        return new ArrayList<>();
    }

    private ExactSuffixAlignmentResult getBestMatch() {
        var bestMatch = 0;
        var bestMatchLocation = new BestMatchLocation(0, 0);
        for (var yIndex = 1; yIndex < this.stringToMatchAgainst.size(); yIndex++) {
            for (var xIndex = 1; xIndex < this.adapterSequence.size(); xIndex++) {
                if(!this.stringToMatchAgainst.get(yIndex-1).equals(this.adapterSequence.get(xIndex-1))){
                    dpTable.put(yIndex, xIndex, -1000);
                    ;
                }
                var cost =
                    evaluateCostBasedOnSurroundingCells(yIndex, xIndex).stream().max(Integer::compareTo);
                if (cost.isPresent()) {
                    var nonNullCost = cost.get();
                    dpTable.put(yIndex, xIndex, nonNullCost);
                    if (nonNullCost > bestMatch) {
                        bestMatch = nonNullCost;
                        bestMatchLocation = new BestMatchLocation(xIndex, yIndex);
                    }
                }
            }
        }
        bestMatch = doNotAcceptNonSuffixMatches(bestMatchLocation, bestMatch);
        return new ExactSuffixAlignmentResult(bestMatch, bestMatchLocation);
    }

    private int doNotAcceptNonSuffixMatches(BestMatchLocation bestMatchLocation, int bestMatch) {
        if (bestMatchLocation.yPosition() == this.stringToMatchAgainst.size() - 1){
            bestMatch = 0;
        }
        return bestMatch;
    }

    private List<Integer> evaluateCostBasedOnSurroundingCells(int yIndex, int xIndex) {
        var xGapCost = dpTable.get(yIndex, xIndex - 1) + ExactMatchLocalAlignmentCosts.GAP.cost;
        var yGapCost = dpTable.get(yIndex - 1, xIndex) + ExactMatchLocalAlignmentCosts.GAP.cost;
        var matchCost = ExactMatchLocalAlignmentCosts.MISMATCH.cost;
        if (hasTheSameCharacterInPreviousEntry(yIndex, xIndex)) {
            matchCost = ExactMatchLocalAlignmentCosts.EQUALITY.cost + dpTable.get(yIndex-1, xIndex-1);
        }
        return List.of(xGapCost, yGapCost, matchCost, ExactMatchLocalAlignmentCosts.START_OVER.cost);
    }

    private boolean hasTheSameCharacterInPreviousEntry(int yIndex, int xIndex) {
        return Objects.equals(adapterSequence.get(xIndex - 1), stringToMatchAgainst.get(yIndex - 1));
    }

    private void initializeDpTable() {
        var xRange = IntStream.range(0, this.stringToMatchAgainst.size())
                         .boxed().toList();
        var yRange = IntStream.range(0, this.adapterSequence.size())
                         .boxed().toList();
        this.dpTable = ArrayTable.create(xRange, yRange);

        for (int index : xRange) {
            this.dpTable.put(index, 0, 0);
        }
        for (int index : yRange) {
            this.dpTable.put(0, index, 0);
        }
    }
}
