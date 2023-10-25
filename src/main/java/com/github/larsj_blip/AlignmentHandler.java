package com.github.larsj_blip;

import com.github.larsj_blip.records.DpTableLocation;
import com.github.larsj_blip.records.LocalAlignmentCost;
import com.github.larsj_blip.records.SuffixAlignmentResult;
import com.google.common.collect.Table;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface AlignmentHandler {

    List<String> getMatchPrefix(List<String> match);
    SuffixAlignmentResult getLocalAlignment();

    void setStringToMatchAgainst(List<String> stringToMatchAgainst);
    List<String> getStringToMatchAgainst();
    Map<Integer, Integer> getFinalRow();

    default int getBestSuffixLength(){
        var finalRowIndex = getStringToMatchAgainst().size() - 1;
        var bestSuffixScore = getFinalRow().entrySet().stream()
                                  .max(Entry.comparingByValue(Integer::compareTo));
        var nextMatchColumnIndex = bestSuffixScore.map(Entry::getKey)
                                       .orElse(0);
        var bestSuffixLength = 0;
        if (getFinalRow().get(nextMatchColumnIndex) !=0) {
            bestSuffixLength = recursiveTableSearch(finalRowIndex, nextMatchColumnIndex, 1);
        }

        return bestSuffixLength;
    }

    private int recursiveTableSearch(int rowIndex, int columnIndex, int lengthSoFar) {
        if (getDpTable().get(rowIndex, columnIndex) == 0 || rowIndex == 0 || columnIndex == 0){
            return lengthSoFar;
        }
        var possibleMoves = List.of(new DpTableLocation(rowIndex - 1, columnIndex - 1), new DpTableLocation(rowIndex - 1,
                                                                                                            columnIndex), new DpTableLocation(rowIndex,
                                                                                                                                              columnIndex-1) );
        for (var move: possibleMoves){
            var rowKey = move.rowPosition();
            var columnKey = move.columnPosition();
            var scoreDifference = getDpTable().get(rowIndex, columnIndex) - getDpTable().get(rowKey,
                                                                                             columnKey);
            if (scoreDifference == getCost().getMatchCost() || scoreDifference == getCost().getGapCost()){
                return recursiveTableSearch(rowKey, columnKey, lengthSoFar + 1);
            }
        }
        return 0;
    }

    LocalAlignmentCost getCost();

    Table<Integer, Integer, Integer> getDpTable();
}
