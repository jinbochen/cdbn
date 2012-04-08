package edu.umich.eecs545.w12;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * TODO: handle inputs that are larger than memory by loading them only as needed
 * @author Augie
 */
public class Test {

    public static void main(String[] args) throws Exception {
        // Expecting argss
        if (args.length != 4) {
            throw new Exception("Expected 4 arguments: [CDBN model file] [SVM model file] [directory with testing PCD files] [results output file]");
        }

        // Access the CDBN model file
        File modelFile = new File(args[0]);
        // Check if exists
        if (!modelFile.exists()) {
            throw new IOException("Model file does not exist.");
        }
        
        // Access the SVM model file
        File modelSVMFile = new File(args[1]);
        // Check if exists
        if (!modelSVMFile.exists()) {
            throw new IOException("SVM model file does not exist.");
        }

        // Access the testing file directory
        File testingFileDirectory = new File(args[2]);
        // Check the directory
        if (!testingFileDirectory.exists()) {
            throw new IOException("Directory containing testing files does not exist.");
        }
        if (!testingFileDirectory.isDirectory()) {
            throw new IOException("Expecting a directory (not a file) containing testing files.");
        }

        // Access the results output file
        File outputFile = new File(args[3]);
        // Check if exists
        if (outputFile.exists()) {
            throw new IOException("Output file already exists.");
        }
        // Make parent directory if doesn't exist
        if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
            throw new IOException("Could not create output file parent directory.");
        }

        // Get the files from the directory
        List<String> testingFileLocations = new LinkedList<String>();
        for (String fileLocation : testingFileDirectory.list()) {
            // Only using PCD files
            if (!fileLocation.toLowerCase().endsWith(".pcd")) {
                continue;
            }
            testingFileLocations.add(fileLocation);
        }

        // Check number of testing files
        if (testingFileLocations.isEmpty()) {
            throw new Exception("Did not find any testing files in the given directory.");
        }

        // Convert the training data into RGBD point cloud segment objects
        List<Input> segments = new LinkedList<Input>();
        for (String trainingFileLocation : testingFileLocations) {
            // Access the file
            File trainingFile = new File(trainingFileLocation);
            // Expecting files
            if (trainingFile.isDirectory()) {
                throw new Exception("Expecting training files to be files, not directories.");
            }
            // Read in the point cloud from the given file
            segments.add(new Input(trainingFile));
        }

        // Convert the CDBN model file into a CDBN object
        CDBN cdbn = new CDBN(modelFile);
        
        // Convert the SVM model file into an SVM classifier
        svm_model svmModel = LibSVMUtils.read(modelSVMFile);

        // Classify each of the test RGBD point cloud segment objects
        // Results: [expected, actual], [expected, actual], ...
        int[][] results = new int[segments.size()][2];
        int count = 0;
        for (Input segment : segments) {
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
