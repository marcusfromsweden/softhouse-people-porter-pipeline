package com.softhouse.technicaltests.peopleporterpipeline.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.apache.camel.test.junit5.TestSupport.createDirectory;
import static org.apache.camel.test.junit5.TestSupport.deleteDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeoplePorterRouteTest extends CamelTestSupport {

    private static final String TEST_INPUT_FILENAME = "test-input.txt";
    private static final String EXPECTED_OUTPUT_FILENAME = "expected-test-output.xml";

    private static final String INPUT_DIR = "target/test-input";
    private static final String OUTPUT_DIR = "target/test-output";
    private static final String ERROR_DIR = "target/test-error";

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new PeoplePorterRoute(
                "file:" + INPUT_DIR + "?noop=false&delete=true",
                "file:" + OUTPUT_DIR + "?fileName=people.xml",
                "file:" + ERROR_DIR
        );
    }

    @BeforeEach
    void setup() throws Exception {
        deleteDirectory(INPUT_DIR);
        deleteDirectory(OUTPUT_DIR);
        deleteDirectory(ERROR_DIR);
        createDirectory(INPUT_DIR);

        // Copy test-input.txt from test resources into Camel input folder
        Path source = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource(TEST_INPUT_FILENAME)).toURI());
        Path target = Path.of(INPUT_DIR, TEST_INPUT_FILENAME);
        Files.copy(source, target);
    }

    @Test
    void testXmlOutputMatchesExpected() throws Exception {
        Thread.sleep(2000); // Let Camel route process the file

        Path actualOutput = Path.of(OUTPUT_DIR, "people.xml");
        assertTrue(Files.exists(actualOutput), "Expected output file was not created");

        String actualXml = normalizeXml(Files.readString(actualOutput));
        String expectedXml = normalizeXml(loadResource(EXPECTED_OUTPUT_FILENAME));

        assertEquals(expectedXml, actualXml, "The generated XML does not match the expected output");
    }

    private String loadResource(String resourceName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + resourceName);
            }
            return new String(is.readAllBytes());
        }
    }

    private String normalizeXml(String xml) {
        return xml.replaceAll(">\\s+<", "><").trim();
    }
}
