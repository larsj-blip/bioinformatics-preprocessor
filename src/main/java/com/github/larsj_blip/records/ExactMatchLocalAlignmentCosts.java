package com.github.larsj_blip.records;

public enum ExactMatchLocalAlignmentCosts {
//    should always prefer starting over instead of accepting gaps or mismatches
    EQUALITY(10),
    GAP(-1000),
    MISMATCH(-1000),
    START_OVER(0);
    public final int cost;

    ExactMatchLocalAlignmentCosts(int cost) {

        this.cost = cost;
    }
}
