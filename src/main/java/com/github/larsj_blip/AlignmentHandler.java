package com.github.larsj_blip;

import com.github.larsj_blip.records.DpTableLocation;
import com.github.larsj_blip.records.LocalAlignmentCost;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Map;

public interface AlignmentHandler {

    List<String> getAdapterSequence();

    List<String> getMatchPrefix(List<String> match);

    SuffixAlignmentResult getLocalAlignment();

    void setStringToMatchAgainst(List<String> stringToMatchAgainst);

    List<String> getStringToMatchAgainst();

    Map<Integer, Integer> getFinalRow();

    default int getBestSuffixLength() {
        var finalRowIndex = getStringToMatchAgainst().size() - 1;
        var finalRow = getFinalRow();
        var finalColumnIndex = finalRow.size() - 1;
        var bestSuffixScore = finalRow.get(finalColumnIndex);
        var bestSuffixLength = 0;
        if (bestSuffixScore != 0) {
            var indexOfSuffixStart = getInitialIndexOfSuffix(finalRowIndex, finalColumnIndex);
            bestSuffixLength = getAdapterSequence().size() - indexOfSuffixStart;
        }

        return bestSuffixLength;
    }

    private int getInitialIndexOfSuffix(int rowKey, int columnKey) {
        if (getDpTable().get(rowKey, columnKey) == 0) {
            return columnKey;
        }
        var gapMoves = List.of(new DpTableLocation(rowKey - 1, columnKey),
                                    new DpTableLocation(rowKey, columnKey - 1));
        var scoreDifference = getDpTable().get(rowKey, columnKey) - getDpTable().get(rowKey-1,columnKey-1);
        if (scoreDifference == getCost().getMatchCost()
            || scoreDifference == getCost().getMismatchCost()) {
            return getInitialIndexOfSuffix(rowKey - 1, columnKey - 1);
        }
        for (var move : gapMoves) {
            var nextRowKey = move.rowPosition();
            var nextColumnKey = move.columnPosition();
            var gapScoreDifference = getDpTable().get(rowKey, columnKey) - getDpTable().get(nextRowKey, nextColumnKey);
            if ( gapScoreDifference == getCost().getGapCost()){
                return getInitialIndexOfSuffix(nextRowKey, nextColumnKey);
            }
        }
        return 0;
    }

    LocalAlignmentCost getCost();

    Table<Integer, Integer, Integer> getDpTable();
}
