package com.github.larsj_blip.records;

import lombok.Data;
import lombok.Getter;
@Data
public class ExactMatchLocalAlignmentCosts implements LocalAlignmentCost{

    private static final int EQUALITY = 10;
    private static final int GAP = -1000;
    private static final int MISMATCH = -1000;
    private static final int START_OVER = 0;

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
