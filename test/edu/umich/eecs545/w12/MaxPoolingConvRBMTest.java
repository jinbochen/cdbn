package edu.umich.eecs545.w12;

import junit.framework.TestCase;

/**
 *
 * @author Augie
 */
public class MaxPoolingConvRBMTest extends TestCase {

    public void testTrain() throws Exception {
        // Set up
        CDBN cdbn = TestUtils.createTestCDBN();
        MaxPoolingConvRBM convRBM = new MaxPoolingConvRBM(cdbn, cdbn.inputLayer);
        // Set the input
        cdbn.inputLayer.setInput(TestUtils.getInput());
        // Train the RBM
        convRBM.train();
    }
}
