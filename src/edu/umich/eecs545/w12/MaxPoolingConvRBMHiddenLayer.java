package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

/**
 *
 * @author Augie
 */
public class MaxPoolingConvRBMHiddenLayer extends MaxPoolingConvRBMLayer {

    // Reference to the ConvRBM
    private final MaxPoolingConvRBM convRBM;
    // Which layer is the parent of this layer?
    public final MaxPoolingConvRBMVisibleLayer parent;
    // K * N_H^2 hidden units
    // TODO: make this work for any size dimensions
    public final double[][][] h;
    // Hidden unit activation probabilities
    public final double[][][] pr;
    // K weight matrices, each of size N_W by N_W
    public final double[][][] W;
    // K biases
    public final double[] b;
    // Whether the activation probabilities have been calculated
    private boolean isPr = false;

    public MaxPoolingConvRBMHiddenLayer(MaxPoolingConvRBM convRBM, MaxPoolingConvRBMVisibleLayer parent) throws NullPointerException {
        // Check for null
        if (convRBM == null) {
            throw new NullPointerException("Conv-RBM is null.");
        }
        if (parent == null) {
            throw new NullPointerException("Parent layer is null.");
        }
        // Set reference values
        this.convRBM = convRBM;
        this.parent = parent;
        // TODO: make these work based on the dimensions of the parent
        // Create the unit value structure
        this.h = new double[convRBM.cdbn.K][convRBM.cdbn.N_H][convRBM.cdbn.N_H];
        // Create the unit activation probability structure
        this.pr = new double[convRBM.cdbn.K][convRBM.cdbn.N_H][convRBM.cdbn.N_H];
        // Initialize the units to 0
        for (int k = 0; k < convRBM.cdbn.K; k++) {
            MathUtils.fill(this.h[k], 0);
        }
        // Create the weight matrices
        this.W = new double[convRBM.cdbn.K][convRBM.cdbn.N_W][convRBM.cdbn.N_W];
        // Initialize the weights ~ U[-0.1, 0.1]
        for (int k = 0; k < convRBM.cdbn.K; k++) {
            for (int i = 0; i < convRBM.cdbn.N_W; i++) {
                for (int j = 0; j < convRBM.cdbn.N_W; j++) {
                    this.W[k][i][j] = MathUtils.RANDOM.nextDouble() * 0.2 - 0.1;
                }
            }
        }
        // Create the biases
        this.b = new double[convRBM.cdbn.K];
        // Initialize the biases to 0
        Arrays.fill(this.b, 0);
    }

    public MaxPoolingConvRBMHiddenLayer(MaxPoolingConvRBM convRBM, MaxPoolingConvRBMVisibleLayer parent, BufferedReader in) throws IOException, NullPointerException {
        // Check null
        if (convRBM == null) {
            throw new NullPointerException("Conv-RBM is null.");
        }
        if (in == null) {
            throw new NullPointerException("Input stream is null.");
        }
        // Set reference values
        this.convRBM = convRBM;
        this.parent = parent;
        // Create the unit value structure
        this.h = new double[convRBM.cdbn.K][convRBM.cdbn.N_H][convRBM.cdbn.N_H];
        // Create the unit activation probability structure
        this.pr = new double[convRBM.cdbn.K][convRBM.cdbn.N_H][convRBM.cdbn.N_H];
        // Read the weights
        this.W = new double[convRBM.cdbn.K][convRBM.cdbn.N_W][convRBM.cdbn.N_W];
        for (int k = 0; k < convRBM.cdbn.K; k++) {
            for (int i = 0; i < convRBM.cdbn.N_W; i++) {
                for (int j = 0; j < convRBM.cdbn.N_W; j++) {
                    this.W[k][i][j] = Double.valueOf(in.readLine());
                }
            }
        }
        // Read the biases
        this.b = new double[convRBM.cdbn.K];
        for (int i = 0; i < this.b.length; i++) {
            this.b[i] = Double.valueOf(in.readLine());
        }
    }

    @Override
    public void calculatePr() throws Exception {
        // Access the activations of the parent layer
        double[][] v = parent.sample();
        // Sample and store the activations of the hidden units
        for (int k = 0; k < convRBM.cdbn.K; k++) {
            // Calculate the (W(flipped)k (convolution) v)
            double[][] WFlipped = MathUtils.flipHorizontalAndVertical(W[k]);
            double[][] WFlippedConvv = MathUtils.convolutionSmall(v, convRBM.cdbn.N_V, convRBM.cdbn.N_V, WFlipped, convRBM.cdbn.N_W, convRBM.cdbn.N_W);
            // Access the bias of k
            double bk = b[k];
            // Calculate activation of the (i, j)th unit of the kth unit group
            for (int i = 0; i < convRBM.cdbn.N_H; i++) {
                for (int j = 0; j < convRBM.cdbn.N_H; j++) {
                    // Sigmoid of the (i, j)th element of (W(flipped)k convolution v) plus the bias
                    pr[k][i][j] = MathUtils.SIGMOID.value(WFlippedConvv[i][j] + bk);
                }
            }
        }
        // Note that the activation probabilities have been calculated
        isPr = true;
    }
    
    public final double[][] sample(int k) throws Exception {
        // Need to calculate activation probabilities?
        if (!isPr) {
            throw new Exception("Activation probabilities have not yet been calculated.");
        }
        // Draw a sample activation for the (i, j)th unit of the kth unit group
        for (int i = 0; i < convRBM.cdbn.N_H; i++) {
            for (int j = 0; j < convRBM.cdbn.N_H; j++) {
                // Set the unit to one with the calculated probability
                if (MathUtils.RANDOM.nextDouble() <= pr[k][i][j]) {
                    h[k][i][j] = 1;
                } else {
                    h[k][i][j] = 0;
                }
            }
        }
        return h[k];
    }

    public final double[][] getWeights(int k) throws Exception {
        return W[k];
    }

    public final void setWeights(int k, double[][] weights) throws Exception {
        W[k] = weights;
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

    public final void write(PrintStream out) throws IOException, NullPointerException {
        // Check null
        if (out == null) {
            throw new NullPointerException("Output stream is null.");
        }
        // Write weights
        for (int k = 0; k < convRBM.cdbn.K; k++) {
            for (int i = 0; i < convRBM.cdbn.N_W; i++) {
                for (int j = 0; j < convRBM.cdbn.N_W; j++) {
                    out.println(W[k][i][j]);
                }
            }
        }
        // Write biases
        for (int i = 0; i < b.length; i++) {
            out.println(b[i]);
        }
    }
}
