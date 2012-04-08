package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class TestUtils {

    public static final int TEST_DIMENSIONS = 2;
    public static final int TEST_N_V = 28;
    public static final int TEST_N_H = 24;
    public static final int TEST_K = 40;
    public static final int TEST_C = 2;

    public static CDBN createTestCDBN() throws Exception {
        return new CDBN(TEST_DIMENSIONS, TEST_K, TEST_N_V, TEST_N_H, TEST_C);
    }

    public static Input getInput() throws Exception {
        BufferedReader in = null;
        try {
            // Open a stream to the input
            in = new BufferedReader(new InputStreamReader(InputTest.class.getResourceAsStream("/edu/umich/eecs545/w12/2Dinput")));
            // Read the input
            return new Input(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
