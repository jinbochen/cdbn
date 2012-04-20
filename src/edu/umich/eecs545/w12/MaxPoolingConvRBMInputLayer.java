package edu.umich.eecs545.w12;

/**
 * 
 * @author Augie
 */
public class MaxPoolingConvRBMInputLayer extends MaxPoolingConvRBMVisibleLayer {

    // Parental reference
    public final CDBN cdbn;
    // The working input
    public final double[][] input;
    // A reconstruction of the input
    public final double[][] sample;
    // The activation probability of the reconstructed input
    public final double[][] pr;
    // Bias
    public double c;
    // Whether an input has been set
    private boolean isInput = false;

    public MaxPoolingConvRBMInputLayer(CDBN cdbn) {
        this.cdbn = cdbn;
        // Create the input structure
        input = new double[cdbn.N_V][cdbn.N_V];
        // Initialize the input structure to 0
        MathUtils.fill(input, 0);
        // Create the activation probability structure
        pr = new double[cdbn.N_V][cdbn.N_V];
        // Initialize the activation probability structure to 0
        MathUtils.fill(pr, 0);
        // Create the sample structure
        sample = new double[cdbn.N_V][cdbn.N_V];
        // Initialize the sample structure to 0
        MathUtils.fill(sample, 0);
    }

    @Override
    public final void calculatePr() throws Exception {
        // Make sure there is an input before sampling
        if (!isInput) {
            throw new Exception("Input is not set.");
        }
        // Make sure there is a child layer
        if (child == null) {
            throw new NullPointerException("The input layer is not connected to a hidden layer.");
        }
        // Typecast the child to the hidden layer
        MaxPoolingConvRBMHiddenLayer hChild = ((MaxPoolingConvRBMHiddenLayer) child);
        // Calculate sum over all k(Wk (convolution) hk)
        double[][] sumWkConvhk = new double[cdbn.N_V][cdbn.N_V];
        MathUtils.fill(sumWkConvhk, 0);
        for (int k = 0; k < cdbn.K; k++) {
            // Calculate Wk (convolution) hk
            double[][] conv = MathUtils.convolutionBig(hChild.sample(k), cdbn.N_H, cdbn.N_H, hChild.getWeights(k), cdbn.N_W, cdbn.N_W);
            // Sum
            for (int i = 0; i < cdbn.N_V; i++) {
                for (int j = 0; j < cdbn.N_V; j++) {
                    sumWkConvhk[i][j] += conv[i][j];
                }
            }
        }
        // Calculate activation of the (i, j)th visible unit
        for (int i = 0; i < cdbn.N_V; i++) {
            for (int j = 0; j < cdbn.N_V; j++) {
                // Sigmoid of the (i, j)th element of sum over all k(Wk (convolution) hk) + the bias
                pr[i][j] = MathUtils.SIGMOID.value(sumWkConvhk[i][j] + c);
            }
        }
    }

    @Override
    public final double pr(int x, int y) throws Exception {
        // Make sure there is an input before sampling
        if (!isInput) {
            throw new Exception("Input is not set.");
        }
        return pr[x][y];
    }

    @Override
    public final double[][] sample() throws Exception {
        // Make sure there is an input before sampling
        if (!isInput) {
            throw new Exception("Input is not set.");
        }
        // Calculate activation of the (i, j)th visible unit
        for (int i = 0; i < cdbn.N_V; i++) {
            for (int j = 0; j < cdbn.N_V; j++) {
                // Set the unit to one with the calculated probability
                if (MathUtils.RANDOM.nextDouble() <= pr[i][j]) {
                    sample[i][j] = 1;
                } else {
                    sample[i][j] = 0;
                }
            }
        }
        return sample;
    }

    // Does not clear weights
    @Override
    public final void clear() throws Exception {
        // Note that the input is not set
        isInput = false;
        // Clear the child layer (and so on)
        if (child != null) {
            child.clear();
        }
    }

    public final void setInput(Input in) throws Exception {
        // Clears out everything but the weights for the entire DBN
        clear();
        // Set the input
        for (int i = 0; i < cdbn.N_V; i++) {
            for (int j = 0; j < cdbn.N_V; j++) {
                if (in.value(j, j)) {
                    input[i][j] = 1;
                } else {
                    input[i][j] = 0;
                }
            }
        }
        // Note that the input has been set
        isInput = true;
        // The activation probabilities and sample are the same as input
        MathUtils.copy(input, pr);
        MathUtils.copy(input, sample);
    }
}
