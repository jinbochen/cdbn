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
    public void sample() throws Exception {
        throw new RuntimeException("TODO");
    }
    
    @Override
    public double getActivationPr(int x, int y) throws Exception {
        throw new RuntimeException("TODO");
    }
    
    @Override
    public double[][] getSample() throws Exception {
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
