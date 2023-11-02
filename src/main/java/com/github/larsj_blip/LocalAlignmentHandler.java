package com.github.larsj_blip;

import com.github.larsj_blip.records.DpTableLocation;
import com.github.larsj_blip.records.LocalAlignmentCost;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class LocalAlignmentHandler implements AlignmentHandler {

    private final List<String> adapterSequence;
    private Table<Integer, Integer, Integer> dpTable;
    private List<String> stringToMatchAgainst;
    private LocalAlignmentCost cost;

    public List<String> getMatchPrefix(List<String> match) {
        return this.stringToMatchAgainst.subList(0, this.stringToMatchAgainst.size() - match.size());
    }

    public SuffixAlignmentResult getLocalAlignment() {
        var bestMatch = 0;
        var bestMatchLocation = new DpTableLocation(0, 0);
        for (var rowIndex = 1; rowIndex < this.stringToMatchAgainst.size(); rowIndex++) {
            for (var columnIndex = 1; columnIndex < this.adapterSequence.size(); columnIndex++) {
                var cost =
                    evaluateCostBasedOnSurroundingCells(rowIndex, columnIndex).stream().max(Integer::compareTo);
                if (cost.isPresent()) {
                    var nonNullCost = cost.get();
                    dpTable.put(rowIndex, columnIndex, nonNullCost);
                    if (nonNullCost > bestMatch) {
                        bestMatch = nonNullCost;
                        bestMatchLocation = new DpTableLocation(columnIndex, rowIndex);
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

    public void setStringToMatchAgainst(List<String> stringToMatchAgainst) {
        this.stringToMatchAgainst = stringToMatchAgainst;
        initializeDpTable();
    }

    @Override
    public Map<Integer, Integer> getFinalRow() {
        return dpTable.row(stringToMatchAgainst.size() - 1);
    }





    private List<Integer> evaluateCostBasedOnSurroundingCells(int yIndex, int xIndex) {
        var xGapCost = -100;
        var yGapCost = -100;
        var adapterGapValue = dpTable.get(yIndex, xIndex - 1);
        if (adapterGapValue != null) {
            xGapCost = adapterGapValue + cost.getGapCost();
        }
        Integer stringToBeMatchedGapValue = dpTable.get(yIndex - 1, xIndex);
        if (stringToBeMatchedGapValue != null) {
            yGapCost = stringToBeMatchedGapValue + cost.getGapCost();
        }
        var previousValueAlignmentMatchCost = dpTable.get(yIndex - 1, xIndex - 1);
        var matchCost = 0;
        if (previousValueAlignmentMatchCost != null){
            if (hasTheSameCharacterInPreviousEntry(yIndex, xIndex)){
                matchCost = previousValueAlignmentMatchCost + cost.getMatchCost();
            } else{
                matchCost = previousValueAlignmentMatchCost + cost.getMismatchCost();
            }
        }
        return List.of(xGapCost, yGapCost, matchCost, cost.getStartOverCost());
    }

    private boolean hasTheSameCharacterInPreviousEntry(int yIndex, int xIndex) {
        return Objects.equals(adapterSequence.get(xIndex - 1), stringToMatchAgainst.get(yIndex - 1));
    }
}
