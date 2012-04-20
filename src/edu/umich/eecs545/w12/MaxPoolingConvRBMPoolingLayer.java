package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author Augie
 */
public class MaxPoolingConvRBMPoolingLayer extends MaxPoolingConvRBMVisibleLayer {

    // Reference to the ConvRBM
    private MaxPoolingConvRBM convRBM;
    // The hidden layer upon which this layer sits
    private MaxPoolingConvRBMHiddenLayer H;
    // A sample activation for the units in this layer
    // The activation probabilities for the units in this layer
    private final double[][][] pr;
    // Whether the activation probabilities have been calculated
    private boolean isPr = false;

    public MaxPoolingConvRBMPoolingLayer(MaxPoolingConvRBM convRBM, MaxPoolingConvRBMHiddenLayer H) throws NullPointerException {
        // Check for null
        if (convRBM == null) {
            throw new NullPointerException("Conv-RBM is null.");
        }
        if (H == null) {
            throw new NullPointerException("Hidden layer is null.");
        }
        this.convRBM = convRBM;
        this.H = H;
        this.pr = new double[convRBM.cdbn.K][convRBM.cdbn.N_P][convRBM.cdbn.N_P];
    }

    public MaxPoolingConvRBMPoolingLayer(MaxPoolingConvRBM convRBM, BufferedReader in) throws IOException, NullPointerException {
        // Check null
        if (in == null) {
            throw new NullPointerException("Input stream is null.");
        }
        if (convRBM == null) {
            throw new NullPointerException("Conv-RBM is null.");
        }
        throw new RuntimeException("TODO");
    }

    @Override
    public void calculatePr() throws Exception {
        // From Lee et al. (2009):
        //  Compute the maximum activation probability of the units in a small region of the hidden layer
        
    }

    // Does not clear weights
    @Override
    public void clear() throws Exception {
        // Note that the probabilities have not been calculated
        isPr = false;
        // Clear the child layer (and so on)
        if (child != null) {
            child.clear();
        }
    }

    @Override
    public double pr(int x, int y) throws Exception {
        throw new RuntimeException("TODO");
    }

    @Override
    public double[][] sample() throws Exception {
        // Need to calculate activation probabilities?
        if (!isPr) {
            throw new Exception("Activation probabilities have not yet been calculated.");
        }
        throw new RuntimeException("TODO");
    }

    public void write(PrintStream out) throws IOException, NullPointerException {
        // Check null
        if (out == null) {
            throw new NullPointerException("Output stream is null.");
        }
        throw new RuntimeException("TODO");
    }
}
