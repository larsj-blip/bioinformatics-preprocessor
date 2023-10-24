package com.github.larsj_blip;

import com.github.larsj_blip.records.BestMatchLocation;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import com.github.larsj_blip.records.LocalAlignmentCost;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class LocalAlignmentMatcher {

    private final List<String> adapterSequence;
    private Table<Integer, Integer, Integer> dpTable;
    private List<String> stringToMatchAgainst;
    private LocalAlignmentCost cost;

    public String getMatchPrefix(List<String> match) {
        var prefix = this.stringToMatchAgainst.subList(0, this.stringToMatchAgainst.size() - match.size());
        return concatenateListToString(prefix);
    }
    public SuffixAlignmentResult getLocalAlignment() {
        var bestMatch = 0;
        var bestMatchLocation = new BestMatchLocation(0, 0);
        for (var yIndex = 1; yIndex < this.stringToMatchAgainst.size(); yIndex++) {
            for (var xIndex = 1; xIndex < this.adapterSequence.size(); xIndex++) {
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
        return new SuffixAlignmentResult(bestMatch, bestMatchLocation);
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
    public void setStringToMatchAgainst(List<String> stringToMatchAgainst){
        this.stringToMatchAgainst = stringToMatchAgainst;
        initializeDpTable();
    }
    private List<Integer> evaluateCostBasedOnSurroundingCells(int yIndex, int xIndex) {
        var xGapCost = -100;
        var yGapCost = -100;
        var adapterGapValue = dpTable.get(yIndex, xIndex - 1);
        if (adapterGapValue != null){
        xGapCost = adapterGapValue + cost.getGapCost();
        }
        Integer stringToBeMatchedGapValue = dpTable.get(yIndex - 1, xIndex);
        if (stringToBeMatchedGapValue != null) {
            yGapCost = stringToBeMatchedGapValue + cost.getGapCost();
        }
        var matchCost = cost.getMismatchCost();
        if (hasTheSameCharacterInPreviousEntry(yIndex, xIndex)) {
            var previousValueAligmentMatchCost = dpTable.get(yIndex - 1, xIndex - 1);
            matchCost = previousValueAligmentMatchCost != null ? cost.getMatchCost() + previousValueAligmentMatchCost :
                                                                                                                        cost.getMismatchCost();
        }
        return List.of(xGapCost, yGapCost, matchCost, cost.getStartOverCost());
    }
    private boolean hasTheSameCharacterInPreviousEntry(int yIndex, int xIndex) {
        return Objects.equals(adapterSequence.get(xIndex - 1), stringToMatchAgainst.get(yIndex - 1));
    }
    private static String concatenateListToString(List<String> prefix) {
        var stringBuilder = new StringBuilder(40);
        prefix.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
