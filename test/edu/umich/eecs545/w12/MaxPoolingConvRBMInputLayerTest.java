package edu.umich.eecs545.w12;

import junit.framework.TestCase;

/**
 *
 * @author Augie
 */
public class MaxPoolingConvRBMInputLayerTest extends TestCase {

    public void testSample() throws Exception {
        // Set up
        CDBN cdbn = TestUtils.createTestCDBN();
        MaxPoolingConvRBM convRBM = new MaxPoolingConvRBM(cdbn, cdbn.inputLayer);
        // Set the input
        cdbn.inputLayer.setInput(TestUtils.getInput());
        // Sample the hidden layer
        convRBM.H.sample();
        // Sample the input layer
        cdbn.inputLayer.sample();
    }
}
