/*
 * Copyright 2015-2020 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.cellbase.lib.builders.variation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.avro.VariantType;

import org.opencb.cellbase.core.serializer.CellBaseFileSerializer;
import org.opencb.cellbase.core.serializer.CellBaseJsonFileSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;



/**
 * Created by parce on 03/12/15.
 */
public class VariationParserTest {

    private static Path variationParserTestDirectory;
    private static Path variationParserTestOutputDirectory;

    @BeforeAll
    public static void setUpClass() throws Exception {
        variationParserTestDirectory = Paths.get(VariationParserTest.class.getResource("/variationParser").getPath());
        variationParserTestOutputDirectory = variationParserTestDirectory.resolve("output");
        variationParserTestOutputDirectory.toFile().mkdir();
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        variationParserTestDirectory.resolve(VariationBuilder.PREPROCESSED_VARIATION_FILENAME + ".gz").toFile().delete();
        variationParserTestDirectory.resolve(VariationFeatureFile.PREPROCESSED_VARIATION_FEATURE_FILENAME + ".gz").toFile().delete();
        variationParserTestDirectory.resolve(VariationTranscriptFile.PREPROCESSED_TRANSCRIPT_VARIATION_FILENAME + ".gz").toFile().delete();
        variationParserTestDirectory.resolve(VariationSynonymFile.PREPROCESSED_VARIATION_SYNONYM_FILENAME + ".gz").toFile().delete();
        variationParserTestOutputDirectory.toFile().delete();
    }

    // TODO: fix test
    @Disabled
    @Test
    public void testParse() throws Exception {
        TestSerializer testSerializer = new TestSerializer();
        VariationBuilder parser = new VariationBuilder(variationParserTestDirectory, testSerializer);
        parser.parse();

        Set<String> outputFileNames = checkOutputFileNames(testSerializer.serializedVariants);
        checkVariants(testSerializer.serializedVariants);
    }

    private Set<String> checkOutputFileNames(Map<String, List<Variant>> serializedVariantsMap) {
        Set<String> outputFileNames = serializedVariantsMap.keySet();
        assertTrue(outputFileNames.contains("variation_chr14"));
        assertTrue(outputFileNames.contains("variation_chr1"));
        assertTrue(outputFileNames.contains("variation_chr7"));
        return outputFileNames;
    }

    private void checkVariants(Map<String, List<Variant>> serializedVariantsMap) {
        // chr1 variants
        List<Variant> chr1Variations = serializedVariantsMap.get("variation_chr1");
        assertEquals(2, chr1Variations.size());
        // first alternate
        Variant variant = chr1Variations.stream().filter(v -> v.getAlternate().equals("A")).findFirst().get();
        checkVariant(variant, "", "A", 112954965, 112954965, "1", "rs1412931", VariantType.INDEL);
        VariantAnnotation annotation = variant.getAnnotation();
//        List<ConsequenceType> consequenceTypes = annotation.getConsequenceTypes();
//        assertEquals(6, consequenceTypes.size());
        // TODO consequence types details
        assertNull(annotation.getPopulationFrequencies());
        // TODO hgvs
        // TODO check xrefs
        variant = chr1Variations.stream().filter(v -> v.getAlternate().equals("C")).findFirst().get();
        checkVariant(variant, "", "C", 112954965, 112954965, "1", "rs1412931", VariantType.INDEL);
        annotation = variant.getAnnotation();
//        consequenceTypes = annotation.getConsequenceTypes();
//        assertEquals(6, consequenceTypes.size());
        // TODO consequence types details
        assertNull(annotation.getPopulationFrequencies());
        // TODO hgvs
        // TODO check xrefs

        List<Variant> chr7Variations = serializedVariantsMap.get("variation_chr7");
        assertEquals(1, chr7Variations.size());
        variant = chr7Variations.get(0);
        checkVariant(variant, "G", "A", 54421937, 54421937, "1", "rs1404666", VariantType.SNV);
        annotation = variant.getAnnotation();
//        consequenceTypes = annotation.getConsequenceTypes();
//        assertEquals(2, consequenceTypes.size());
        // TODO consequence types details
        assertNull(annotation.getPopulationFrequencies());
        // TODO frequencies
        // TODO hgvs
        // TODO check xrefs

        List<Variant> chr14Variations = serializedVariantsMap.get("variation_chr14");
        assertEquals(2, chr14Variations.size());
        variant = chr14Variations.stream().filter(v -> v.getAlternate().equals("C")).findFirst().get();
        checkVariant(variant, "A", "C", 77697967, 77697967, "1", "rs375566", VariantType.SNV);
        annotation = variant.getAnnotation();
//        consequenceTypes = annotation.getConsequenceTypes();
//        assertEquals(4, consequenceTypes.size());
        // TODO consequence types details
        assertNull(annotation.getPopulationFrequencies());
        // TODO frequencies
        // TODO hgvs
        // TODO check xrefs
        variant = chr14Variations.stream().filter(v -> v.getAlternate().equals("G")).findFirst().get();
        checkVariant(variant, "A", "G", 77697967, 77697967, "1", "rs375566", VariantType.SNV);
        annotation = variant.getAnnotation();
//        consequenceTypes = annotation.getConsequenceTypes();
//        assertEquals(4, consequenceTypes.size());
        // TODO consequence types details
        assertNull(annotation.getPopulationFrequencies());
        // TODO frequencies
        // TODO hgvs
        // TODO check xrefs
    }

    private void checkVariant(Variant variant, String expectedReference, String expectedAlternate, Integer expectedStart,
                              Integer expectedEnd, String expectedStrand, String expectedId, VariantType expectedVariantType) {
        assertEquals(expectedReference, variant.getReference());
        assertEquals(expectedAlternate, variant.getAlternate());
        assertEquals(expectedStart, variant.getStart());
        assertEquals(expectedEnd, variant.getEnd());
        assertEquals(expectedStrand, variant.getStrand());
        assertEquals(1, variant.getIds().size());
        assertEquals(expectedId, variant.getIds().get(0));
        assertEquals(expectedVariantType, variant.getType());
    }

    @Test
    public void testParseUsingPreprocessedFiles() throws Exception {
        TestSerializer testSerializer = new TestSerializer();
        VariationBuilder parser = new VariationBuilder(variationParserTestDirectory, testSerializer);
        parser.parse();

        Set<String> outputFileNames = checkOutputFileNames(testSerializer.serializedVariants);
        checkVariants(testSerializer.serializedVariants);
    }

    // TODO: fix test
    @Disabled
    @Test
    public void testParseToJson() throws Exception {
        CellBaseJsonFileSerializer serializer = new CellBaseJsonFileSerializer(variationParserTestDirectory.resolve("output"), "", false);
        VariationBuilder parser = new VariationBuilder(variationParserTestDirectory, serializer);
        parser.parse();
    }

    class TestSerializer implements CellBaseFileSerializer {

        Map<String, List<Variant>> serializedVariants = new HashMap<>();

        @Override
        public void serialize(Object object) {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void serialize(Object object, String fileName) {
            List fileNameList = serializedVariants.getOrDefault(fileName, new ArrayList<>());
            fileNameList.add(object);
            serializedVariants.put(fileName, fileNameList);
        }

        public Path getOutdir() {
            return null;
        }

        public String getFileName() {
            return null;
        }

    }
}
