package com.github.larsj_blip.records;

public record BestMatchLocation(int xPosition, int yPosition) {

    @Override
    public int xPosition() {
        return xPosition;
    }

    @Override
    public int yPosition() {
        return yPosition;
    }

    public BestMatchLocation {
    }
}
