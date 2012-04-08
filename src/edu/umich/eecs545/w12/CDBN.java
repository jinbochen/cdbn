package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import libsvm.svm_node;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * From Lee et al. (2009)
 * 
 * @author Augie
 */
public class CDBN {

    // Print debug info?
    public static final boolean DEBUG = true;
    // Version
    public static final String VERSION = "1.0";
    // Default parameter values
    public static final int DEFAULT_STACKED_CONVRBMS = 2;
    // How many stacked conv-RBM's do we want to have?
    public int stackedConvRBMCount = DEFAULT_STACKED_CONVRBMS;
    // How many dimensions is the input data?
    public final int inputDimensions;
    // The number of hidden layer unit groups
    public final int K;
    // The dimension length of the input layer
    public final int N_V;
    // The dimension length of the hidden layer
    public final int N_H;
    // The cubed root of the size of the filter associated with each group
    //  N_W = N_V - N_H + 1
    public final int N_W;
    // The parameter the specifies the size of a dimension of the pooling layer (should be 2 or 3)
    public final int C;
    // The dimension length of the pooling layer
    // N_P = N_H / C
    public final int N_P;
    // The input layer
    public final MaxPoolingConvRBMInputLayer inputLayer;
    // The hidden layers of the C-DBN
    public final LinkedList<MaxPoolingConvRBM> stackedRBMs = new LinkedList<MaxPoolingConvRBM>();
    // Whether the CDBN has been trained
    private boolean trained = false;

    public CDBN(int inputDimensions, int K, int N_V, int N_H, int C) throws Exception {
        this.inputDimensions = inputDimensions;
        this.K = K;
        this.N_V = N_V;
        this.N_H = N_H;
        this.N_W = N_V - N_H + 1;
        this.C = C;
        // Check that C divides N_H without remainder
        if (N_H % C > 0) {
            throw new Exception("C divides N_H with remainder.");
        }
        this.N_P = N_H / C;
        // Create the input layer
        inputLayer = new MaxPoolingConvRBMInputLayer(this);
    }

    // Creates the Conv-DBN from the model in the given file
    public CDBN(File file) throws IOException, NullPointerException {
        // Check for null
        if (file == null) {
            throw new NullPointerException("File is null.");
        }
        // Does the file not exist?
        if (!file.exists()) {
            throw new IOException("The file does not exist.");
        }
        // Open a stream from the file
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(file)));
            // Read version
            String version = in.readLine();
            // Check version
            if (!version.equals(VERSION)) {
                throw new IOException("Can't read this version.");
            }
            // Read input dimension count
            inputDimensions = Integer.valueOf(in.readLine());
            // Read visible layer dimension size
            N_V = Integer.valueOf(in.readLine());
            // Read hidden layer dimension size
            N_H = Integer.valueOf(in.readLine());
            // Calculate the value of N_W given the values of N_V and N_H
            N_W = N_V - N_H + 1;
            // Read the number of hidden layer groups
            K = Integer.valueOf(in.readLine());
            // Read pooling layer dimension size parameter
            C = Integer.valueOf(in.readLine());
            // Calculate the dimension length of the pooling layer
            N_P = N_H / C;
            // Create the input layer
            inputLayer = new MaxPoolingConvRBMInputLayer(this);
            // Read conv-RBM's
            int numConvRBMs = Integer.valueOf(in.readLine());
            for (int i = 0; i < numConvRBMs; i++) {
                // What is the parent of this conv-RBM?
                MaxPoolingConvRBMVisibleLayer parent;
                if (i == 0) {
                    parent = inputLayer;
                } else {
                    parent = stackedRBMs.getLast().P;
                }
                stackedRBMs.add(new MaxPoolingConvRBM(this, parent, in));
            }
            // Set trained
            trained = true;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public svm_node[] getSVMFeatures(Input input) throws Exception {
        // Check null
        if (input == null) {
            throw new NullPointerException("Input is null.");
        }
        throw new RuntimeException("TODO");
    }

    // Train the C-DBN
    public void train(Collection<Input> inputs) throws Exception {
        // Only train if not yet trained
        if (trained) {
            throw new Exception("C-DBN already trained.");
        }
        // Check for null
        if (inputs == null) {
            throw new NullPointerException("Input collection is null.");
        }
        // Check for empty
        if (inputs.isEmpty()) {
            throw new Exception("Input collection is empty.");
        }
        // Train the C-DBN
        try {
            // Iterated training of higher-level conv-RBMs
            int trainedConvRBMs = 0;
            while (trainedConvRBMs < stackedConvRBMCount) {
                // What is the parent layer? Could be the input layer or a pooling layer from another ConvRBM
                MaxPoolingConvRBMVisibleLayer parent;
                if (stackedRBMs.isEmpty()) {
                    parent = inputLayer;
                } else {
                    parent = stackedRBMs.getLast().P;
                }
                // New convolutional RBM (hidden layer training wrapper)
                MaxPoolingConvRBM convRBM = new MaxPoolingConvRBM(this, parent);
                // Train the next layer up using each of the segments
                for (Input input : inputs) {
                    // Set the input layer input
                    inputLayer.setInput(input);
                    // Train the hidden layer weights from the fixed parent representation
                    convRBM.train();
                }
                stackedRBMs.add(convRBM);
                // Increment the number of trained conv-RBMs
                trainedConvRBMs++;
            }
        } finally {
            trained = true;
        }
    }

    // Outputs the trained model to the given file.
    public void write(File file) throws IOException, NullPointerException {
        // Check for null
        if (file == null) {
            throw new NullPointerException("File is null.");
        }
        // The model must have been trained
        if (!trained) {
            throw new IOException("Model has not been trained.");
        }
        // Does the file already exist?
        if (file.exists()) {
            throw new IOException("The output file already exists.");
        }
        // Open a stream to the file
        PrintStream out = null;
        try {
            out = new PrintStream(FileUtils.openOutputStream(file));
            // Write version
            out.println(VERSION);
            // Write input dimensions
            out.println(inputDimensions);
            // Write visible layer dimension size
            out.println(N_V);
            // Write hidden layer dimension size
            out.println(N_H);
            // Write the number of hidden layer unit groups
            out.println(K);
            // Write pooling layer dimension size parameter
            out.println(C);
            // Write conv-RBMs
            out.println(stackedRBMs.size());
            for (MaxPoolingConvRBM convRBM : stackedRBMs) {
                convRBM.write(out);
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    
    public static void println(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }
    
    public static void print(String msg) {
        if (DEBUG) {
            System.out.print(msg);
        }
    }
}
