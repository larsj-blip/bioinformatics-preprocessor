package com.github.larsj_blip.records;

public record ExactSuffixAlignmentResult(int bestMatch, BestMatchLocation bestMatchLocation) {

    @Override
    public int bestMatch() {
        return bestMatch;
    }

    @Override
    public BestMatchLocation bestMatchLocation() {
        return bestMatchLocation;
    }

    public ExactSuffixAlignmentResult {
    }
}
