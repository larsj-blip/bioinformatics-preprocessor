package com.github.larsj_blip.records;

import java.util.List;
import lombok.Builder;
import lombok.Data;


public record StringComparisonResult(boolean isMatch, int matchLength, String match, String matchPrefix){

}
