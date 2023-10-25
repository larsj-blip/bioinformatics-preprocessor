package com.github.larsj_blip.records;

public class ApproximateMatchLocalAlignmentCosts implements LocalAlignmentCost{

    public static final int EQUALITY = 10;
    public static final int GAP = -5;
    public static final int MISMATCH = -5;
    public static final int START_OVER = 0;
    @Override
    public int getGapCost() {
        return GAP;
    }

    @Override
    public int getMismatchCost() {
        return MISMATCH;
    }

    @Override
    public int getMatchCost() {
        return EQUALITY;
    }

    @Override
    public int getStartOverCost() {
        return START_OVER;
    }
}
