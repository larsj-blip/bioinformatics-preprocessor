package com.github.larsj_blip.records;

public record SuffixAlignmentResult(int bestMatchScore, DpTableLocation bestMatchLocation) {

    public int bestMatchScore() {
        return bestMatchScore;
    }

    @Override
    public DpTableLocation bestMatchLocation() {
        return bestMatchLocation;
    }

    public SuffixAlignmentResult {
    }
}
