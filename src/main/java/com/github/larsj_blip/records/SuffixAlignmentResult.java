package com.github.larsj_blip.records;

public record SuffixAlignmentResult(int bestMatch, DpTableLocation bestMatchLocation) {

    @Override
    public int bestMatch() {
        return bestMatch;
    }

    @Override
    public DpTableLocation bestMatchLocation() {
        return bestMatchLocation;
    }

    public SuffixAlignmentResult {
    }
}
