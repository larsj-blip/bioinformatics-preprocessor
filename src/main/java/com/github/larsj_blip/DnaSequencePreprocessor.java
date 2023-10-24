package com.github.larsj_blip;

import com.github.larsj_blip.records.StringComparisonResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnaSequencePreprocessor {
    private Path resourceLocation;
    @Getter
    private List<String> rawDnaSequences;
    private SuffixMatcher suffixMatcher;
    @Getter
    private List<StringComparisonResult> processedDnaData;

//    TODO: should not be settable
    @Getter
    @Builder.Default
    private int amountOfMatches = 0;


    public void loadSequences() throws IOException {
        var classLoader = getClass().getClassLoader();
        var inputStream = classLoader.getResourceAsStream(resourceLocation.toString());
        this.rawDnaSequences = readFromInputStream(inputStream);
        this.processedDnaData = new ArrayList<>();
    }

    public void preprocess() {
        for (String sequence: rawDnaSequences){
            var result = suffixMatcher.matchAgainst(sequence);
            processedDnaData.add(result);
            if (result.isMatch()) {
                amountOfMatches += 1;
            }
        }
    }

    private List<String> readFromInputStream(InputStream inputStream)
        throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
            .toList();
        }
    }
}