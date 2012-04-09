package edu.umich.eecs545.w12;

/**
 *
 * @author Augie
 */
public abstract class MaxPoolingConvRBMVisibleLayer extends MaxPoolingConvRBMLayer {

    public abstract double pr(int x, int y) throws Exception;
    
    public abstract double[][] sample() throws Exception;
}
