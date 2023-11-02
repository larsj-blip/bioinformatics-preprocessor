package com.github.larsj_blip;

import static org.knowm.xchart.QuickChart.getChart;
import com.github.larsj_blip.records.StringComparisonResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;

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
        for (String sequence : rawDnaSequences) {
            var result = suffixMatcher.matchAgainst(sequence);
            processedDnaData.add(result);
            if (result.isMatch()) {
                amountOfMatches += 1;
            }
        }
    }

    public Map<Integer, Long> getLengthDistribution() {
        return processedDnaData.stream()
                   .collect(Collectors.groupingBy(StringComparisonResult::matchLength, Collectors.counting()));
    }

    public void plotLengthDistribution() throws IOException {
        var lengthDistribution = getLengthDistribution();
        var lengths =
            lengthDistribution.keySet().stream().toList();
        var counts =
            lengths.stream().map(lengthDistribution::get).toList();
        var chart = QuickChart.getChart("length distribution", "sequence length",
                                        "occurences", "seriesname",
                                        lengths, counts);
        BitmapEncoder.saveBitmap(chart, "./whadduheeek", BitmapFormat.PNG);
    }

    private List<String> readFromInputStream(InputStream inputStream)
        throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                       .toList();
        }
    }
}