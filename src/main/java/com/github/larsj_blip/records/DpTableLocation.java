package com.github.larsj_blip.records;

public record DpTableLocation(int rowPosition, int columnPosition) {

    public int columnPosition() {
        return columnPosition;
    }

    public int rowPosition() {
        return rowPosition;
    }

    public DpTableLocation {
    }
}
