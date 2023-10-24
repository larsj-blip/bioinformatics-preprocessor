package com.github.larsj_blip.records;

public interface LocalAlignmentCost {

    public int getGapCost();
    public int getMismatchCost();
    public int getMatchCost();
    public int getStartOverCost();


}
