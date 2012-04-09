package edu.umich.eecs545.w12;

/**
 *
 * @author Augie
 */
public abstract class MaxPoolingConvRBMLayer {

    public MaxPoolingConvRBMLayer child;

    public abstract void calculatePr() throws Exception;

    public abstract void clear() throws Exception;
}
