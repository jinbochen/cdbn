package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class InputTest extends TestCase {

    public void test2DSample() throws Exception {
        BufferedReader in = null;
        try {
            // Open a stream to the sample
            in = new BufferedReader(new InputStreamReader(InputTest.class.getResourceAsStream("/edu/umich/eecs545/w12/2Dinput")));
            // Read the input
            Input input = new Input(in);
            // Check the input
            assertEquals(5, input.label);
            // Check the # of points
            assertEquals(145, input.pointCount);
            // Check a few of the points
            assertTrue(input.value(5, 16));
            assertTrue(input.value(6, 20));
            assertTrue(input.value(24, 10));
            // Check a non-point
            assertFalse(input.value(1, 1));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public void test2DSamples() throws Exception {
        BufferedReader in = null;
        try {
            // Open a stream to the sample
            in = new BufferedReader(new InputStreamReader(InputTest.class.getResourceAsStream("/edu/umich/eecs545/w12/2Dinputs")));
            // Read the inputs
            Input[] inputs = Input.readInputs(in);
            // Check the first input
            {
                // Check the input
                assertEquals(5, inputs[0].label);
                // Check the # of points
                assertEquals(145, inputs[0].pointCount);
                // Check a few of the points
                assertTrue(inputs[0].value(5, 16));
                assertTrue(inputs[0].value(6, 20));
                assertTrue(inputs[0].value(24, 10));
                // Check a non-point
                assertFalse(inputs[0].value(1, 1));
            }
            // Check the second input
            {
                // Check the input
                assertEquals(7, inputs[1].label);
                // Check the # of points
                assertEquals(105, inputs[1].pointCount);
                // Check a few of the points
                assertTrue(inputs[1].value(7, 6));
                assertTrue(inputs[1].value(23, 12));
                assertTrue(inputs[1].value(26, 12));
                // Check a non-point
                assertFalse(inputs[1].value(1, 1));
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
