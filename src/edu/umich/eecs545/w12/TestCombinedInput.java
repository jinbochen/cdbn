package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class TestCombinedInput {

    public static void main(String[] args) throws Exception {
        // Expecting argss
        if (args.length != 4) {
            throw new Exception("Expected 4 arguments: [CDBN model file] [SVM model file] [test samples file] [results output file]");
        }
        int argCount = 0;

        // Access the CDBN model file
        File modelFile = new File(args[argCount++]);
        // Check if exists
        if (!modelFile.exists()) {
            throw new IOException("Model file does not exist.");
        }
        
        // Access the SVM model file
        File modelSVMFile = new File(args[argCount++]);
        // Check if exists
        if (!modelSVMFile.exists()) {
            throw new IOException("SVM model file does not exist.");
        }

        // Access the testing file directory
        File testingFile = new File(args[argCount++]);
        if (!testingFile.exists()) {
            throw new IOException("Testing samples file does not exist.");
        }
        if (testingFile.isDirectory()) {
            throw new IOException("Expected a file, not a directory.");
        }

        // Access the results output file
        File outputFile = new File(args[argCount++]);
        // Check if exists
        if (outputFile.exists()) {
            throw new IOException("Output file already exists.");
        }
        // Make parent directory if doesn't exist
        if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
            throw new IOException("Could not create output file parent directory.");
        }

        // Convert the training file into input objects
        List<Input> inputs = new LinkedList<Input>();
        // Open a stream to the input file
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(testingFile)));
            // Read the number of samples
            int sampleCount = Integer.valueOf(in.readLine());
            // Read the samples
            for (int i = 0; i < sampleCount; i++) {
                inputs.add(new Input(in));
            }
        } finally {
            IOUtils.closeQuietly(in);
        }

        // Convert the CDBN model file into a CDBN object
        CDBN cdbn = new CDBN(modelFile);
        
        // Convert the SVM model file into an SVM classifier
        svm_model svmModel = LibSVMUtils.read(modelSVMFile);

        // Classify each of the test inputs
        // Results: [expected, actual], [expected, actual], ...
        int[][] results = new int[inputs.size()][2];
        int count = 0;
        for (Input segment : inputs) {
            // Expected label
            results[count][0] = segment.label;
            // Get the pooling layer(s) activations as features
            svm_node[] nodes = cdbn.getSVMFeatures(segment);
            // Predict the label for this segment
            results[count][1] = Long.valueOf(Math.round(svm.svm_predict(svmModel, nodes))).intValue();
            // Increment result count
            count++;
        }

        // Write a file containing the test and true classes to disk
        PrintStream out = null;
        try {
            out = new PrintStream(FileUtils.openOutputStream(outputFile));
            out.println("expected,actual");
            for (int[] result : results) {
                out.println(result[0] + "," + result[1]);
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
