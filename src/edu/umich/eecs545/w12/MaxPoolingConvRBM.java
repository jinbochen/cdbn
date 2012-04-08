package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * From Lee et al. (2009)
 * @author Augie
 */
public class MaxPoolingConvRBM {

    // The number of Gibbs sampling iterations
    public static final int TRAINING_ITERATIONS = 1;
    // The learning rate
    public static final double LEARNING_RATE = 0.1;
    // Useful for accessing parameter information
    public final CDBN cdbn;
    // The hidden layer to be trained
    public final MaxPoolingConvRBMHiddenLayer H;
    // The pooling layer that sits atop the hidden layer
    public final MaxPoolingConvRBMPoolingLayer P;

    public MaxPoolingConvRBM(CDBN cdbn, MaxPoolingConvRBMVisibleLayer parent) {
        this.cdbn = cdbn;
        this.H = new MaxPoolingConvRBMHiddenLayer(this, parent);
        parent.child = this.H;
        this.P = new MaxPoolingConvRBMPoolingLayer(this, H);
        this.H.child = this.P;
    }

    public MaxPoolingConvRBM(CDBN cdbn, MaxPoolingConvRBMVisibleLayer parent, BufferedReader in) throws IOException, NullPointerException {
        // Check null
        if (cdbn == null) {
            throw new NullPointerException("CDBN is null.");
        }
        if (parent == null) {
            throw new NullPointerException("Parent layer is null.");
        }
        if (in == null) {
            throw new NullPointerException("Input stream is null.");
        }
        // Set the CDBN
        this.cdbn = cdbn;
        // Read the hidden layer
        this.H = new MaxPoolingConvRBMHiddenLayer(this, parent, in);
        parent.child = this.H;
        // Read the pooling layer
        this.P = new MaxPoolingConvRBMPoolingLayer(this, in);
        this.H.child = this.P;
    }

    public void train() throws Exception {
        // With contrastive divergence (CD):
        //  Initialize the Markov chain with a training example so it's already close to the final distribution (rather than start from scratch)
        //  Do not wait for the chain to converge... stop after k steps of Gibbs sampling
        // Train the hidden layer given parent layer
        for (int iteration = 0; iteration < TRAINING_ITERATIONS; iteration++) {
            // TODO: gradually reduce the learning rate
            // Sample the hidden layer;
            H.sample();
            // Sample the visible layer
            H.parent.sample();
            // Calculate the KL divergence of the reconstructed image
            double D = 0;
            for (int x = 0; x < cdbn.N_V; x++) {
                for (int y = 0; y < cdbn.N_V; y++) {
                    if (cdbn.inputLayer.input[x][y] > 0) {
                        D += cdbn.inputLayer.input[x][y] * Math.log(cdbn.inputLayer.input[x][y] / H.parent.getActivationPr(x, y));
                    }
                }
            }
            System.out.println("KL divergence: " + D);
            //
        }
        // Train the pooling layer given the hidden layer
        for (int iteration = 0; iteration < TRAINING_ITERATIONS; iteration++) {
        }
        throw new RuntimeException("TODO");
    }

    public void write(PrintStream out) throws IOException, NullPointerException {
        // Check null
        if (out == null) {
            throw new NullPointerException("Output stream is null.");
        }
        // Write the hidden layer
        H.write(out);
        // Write the pooling layer
        P.write(out);
    }
}
