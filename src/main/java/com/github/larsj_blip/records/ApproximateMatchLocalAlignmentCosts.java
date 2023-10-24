package com.github.larsj_blip.records;

public enum ApproximateMatchLocalAlignmentCosts {
    EQUALITY(10),
    GAP(-1000),
    MISMATCH(-5),
    START_OVER(0);
    public final int cost;

    ApproximateMatchLocalAlignmentCosts(int cost) {

        this.cost = cost;
    }
}
