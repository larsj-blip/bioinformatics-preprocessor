package com.github.larsj_blip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import com.github.larsj_blip.matchers.ApproximateSuffixMatcher;
import com.github.larsj_blip.matchers.ExactSuffixMatcher;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DnaSequencePreprocessorTest {

    public static final Path PATH_TO_SIMPLE_FAKE_DNA_SEQUENCE = Path.of("dna_sequence_samples",
                                                                        "dna_sequence_containing_common_fiveprime_adapter_sequence.txt");
    public static final Path PATH_TO_TASK1_SEQUENCE = Path.of("dna_sequence_samples", "s_3_sequence_1M.txt");
    public static final String TEST_ADAPTER_SEQUENCE = "atctgtatat";
    public static final String REAL_ADAPTER_SEQUENCE =
        "TGGAATTCTCGGGTGCCAAGGAACTCCAGTCACACAGTGATCTCGTATGCCGTCTTCTGCTTG";
    public static final int AMOUNT_OF_EXACT_TEST_MATCHES = 2;
    public static final int AMOUNT_OF_TEST_MATCHES_WITH_TEN_PERCENT_ERROR_MARGIN = 3;

    @Test
    void shouldReadDnaSequencesFromFile() throws IOException {
        var preprocessor = initializeExactMatchTestPreprocessor();
        assertThat(preprocessor.getRawDnaSequences(), is(not(empty())));
        assertThat(preprocessor.getRawDnaSequences(), hasItem(TEST_ADAPTER_SEQUENCE));
    }

    @Test
    void shouldMatchSuffixOfSequenceToPrefixOf3PrimeAdapterSequence() throws IOException {
        var preprocessor = initializeExactMatchTestPreprocessor();
        preprocessor.preprocess();
        var amountOfMatches = preprocessor.getAmountOfMatches();
        assertThat(amountOfMatches, is(equalTo(AMOUNT_OF_EXACT_TEST_MATCHES)));
    }
    @Test
    void shouldMatchSuffixOfRealSequencesToPrefixOf3PrimeAdapterSequence() throws IOException {
        var preprocessor = initializeExactMatchRealPreprocessor();
        preprocessor.preprocess();
        var amountOfMatches = preprocessor.getAmountOfMatches();
        assertThat(amountOfMatches, is(equalTo(592402)));
    }

    @Test
    void shouldMatchImperfectlyMatchingAdapterSequencesInPrefixOfDnaSequences() throws IOException {
        var preprocessor = initializeApproximateMatchPreprocessor();
        preprocessor.loadSequences();
        preprocessor.preprocess();
        var amountOfMatches = preprocessor.getAmountOfMatches();
        assertThat(amountOfMatches, is(equalTo(AMOUNT_OF_TEST_MATCHES_WITH_TEN_PERCENT_ERROR_MARGIN)));
    }


    @Test
    void shouldGetLengthDistributionFromResultSetAsXAndYCoordinates() throws IOException {
        var preprocessor = initializeExactMatchRealPreprocessor();
        preprocessor.preprocess();
        var lengthDistribution = preprocessor.getLengthDistribution();
        preprocessor.plotLengthDistribution();
        assertThat(lengthDistribution, is(not(anEmptyMap())));
    }

    private static DnaSequencePreprocessor initializeApproximateMatchPreprocessor() {
        var approximateSuffixMatcher = new ApproximateSuffixMatcher(10, TEST_ADAPTER_SEQUENCE);
        return DnaSequencePreprocessor.builder()
                               .suffixMatcher(approximateSuffixMatcher)
                               .resourceLocation(PATH_TO_SIMPLE_FAKE_DNA_SEQUENCE)
                               .build();
    }

    private static DnaSequencePreprocessor initializeExactMatchRealPreprocessor() throws IOException {

        var preprocessor = DnaSequencePreprocessor.builder()
                               .suffixMatcher(new ExactSuffixMatcher(REAL_ADAPTER_SEQUENCE))
                               .resourceLocation(PATH_TO_TASK1_SEQUENCE)
                               .build();
        preprocessor.loadSequences();
        return preprocessor;
    }

    private static DnaSequencePreprocessor initializeExactMatchTestPreprocessor() throws IOException {

        var preprocessor = DnaSequencePreprocessor.builder()
                               .suffixMatcher(new ExactSuffixMatcher(TEST_ADAPTER_SEQUENCE))
                               .resourceLocation(PATH_TO_SIMPLE_FAKE_DNA_SEQUENCE)
                               .build();
        preprocessor.loadSequences();
        return preprocessor;
    }



    @Test
    void shouldIdentifyPossibleAdapterSequencePrefixFromExistingDnaSequences() {
    }

    @Test
    void shouldIdentifyBarcodeInDnaSequence() {
    }

    @Test
    void shouldGroupDnaSamplesByBarcode() {
    }
}