package com.github.larsj_blip;

import com.github.larsj_blip.records.StringComparisonResult;
import java.util.List;

public interface SuffixMatcher {

    public StringComparisonResult matchAgainst(String stringToMatchAgainst);
    private String getMatchPrefix(List<String> match){
        return "";
    }
}
