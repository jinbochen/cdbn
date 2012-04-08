package edu.umich.eecs545.w12;

import junit.framework.TestCase;

/**
 *
 * @author Augie
 */
public class MathUtilsTest extends TestCase {
    
    public void testFlipHorizontalAndVertical() throws Exception {
        double[][] test = new double[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        double[][] expected = new double[][]{{8, 7, 6}, {5, 4, 3}, {2, 1, 0}};
        double[][] actual = MathUtils.flipHorizontalAndVertical(test);
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                assertEquals(expected[i][j], actual[i][j]);
            }
        }
    }
}
