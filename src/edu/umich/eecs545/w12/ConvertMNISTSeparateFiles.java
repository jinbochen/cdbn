package edu.umich.eecs545.w12;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Augie
 */
public class ConvertMNISTSeparateFiles {

    public static final boolean PRINT = false;

    public static void main(String[] args) throws Exception {
        // Check args
        if (args.length != 3) {
            throw new Exception("Expecting 3 args: [MNIST raw image file] [MNIST label file] [output directory]");
        }
        int argCount = 0;

        // Get raw images file
        File imagesFile = new File(args[argCount++]);
        if (!imagesFile.exists()) {
            throw new IOException("Images file does not exist.");
        }
        if (imagesFile.isDirectory()) {
            throw new IOException("Expecting a file, not a directory.");
        }

        // Get labels file
        File labelsFile = new File(args[argCount++]);
        if (!labelsFile.exists()) {
            throw new IOException("Labels file does not exist.");
        }
        if (labelsFile.isDirectory()) {
            throw new IOException("Expecting a file, not a directory.");
        }

        // Open handle to output directory
        File outputDirectory = new File(args[argCount++]);
        if (outputDirectory.exists() && outputDirectory.list().length > 0) {
            throw new IOException("Output directory is not empty.");
        }
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new IOException("Could not create output directory.");
        }

        // Read in the images one at a time
        InputStream imagesIn = null, labelsIn = null;
        try {
            labelsIn = FileUtils.openInputStream(labelsFile);
            imagesIn = FileUtils.openInputStream(imagesFile);

            // Read the labels header
            byte[] labelsMagicNumberBytes = new byte[4];
            labelsIn.read(labelsMagicNumberBytes);
            int labelsMagicNumber = ByteBuffer.wrap(labelsMagicNumberBytes).asIntBuffer().get();

            byte[] labelsCountBytes = new byte[4];
            labelsIn.read(labelsCountBytes);
            int labelsCount = ByteBuffer.wrap(labelsCountBytes).asIntBuffer().get();

            // Read the images header
            byte[] imagesMagicNumberBytes = new byte[4];
            imagesIn.read(imagesMagicNumberBytes);
            int imagesMagicNumber = ByteBuffer.wrap(imagesMagicNumberBytes).asIntBuffer().get();

            byte[] imagesCountBytes = new byte[4];
            imagesIn.read(imagesCountBytes);
            int imagesCount = ByteBuffer.wrap(imagesCountBytes).asIntBuffer().get();

            byte[] imagesRowsBytes = new byte[4];
            imagesIn.read(imagesRowsBytes);
            int imageRows = ByteBuffer.wrap(imagesRowsBytes).asIntBuffer().get();

            byte[] imagesColsBytes = new byte[4];
            imagesIn.read(imagesColsBytes);
            int imageCols = ByteBuffer.wrap(imagesColsBytes).asIntBuffer().get();

            print("Samples: " + labelsCount);
            print("Image size: " + imageRows + " by " + imageCols);

            // Counts should be the same
            if (labelsCount != imagesCount) {
                throw new Exception("# labels and # images do not match.");
            }

            // Create a new PCD file for each of the images
            for (int i = 0; i < labelsCount; i++) {
                // Read the label
                int label = labelsIn.read();
                print("\n");
                print(String.valueOf(label));
                print("\n");

                // Read the image
                int[][] image = new int[imageRows][imageCols];
                // Read the image one row at a time
                for (int row = 0; row < imageRows; row++) {
                    for (int col = 0; col < imageCols; col++) {
                        double value = (double) imagesIn.read() / 255d;
                        if (value > 0.1) {
                            image[row][col] = 1;
                        } else {
                            image[row][col] = 0;
                        }
                        if (value > 0.1) {
                            print("1");
                        } else {
                            print(" ");
                        }
                    }
                    print("\n");
                }
                print("\n");

                // Open a stream to the output file
                File outputFile = new File(outputDirectory, "sample" + i);
                if (!outputFile.createNewFile()) {
                    throw new IOException("Could not create output file.");
                }
                PrintStream out = null;
                try {
                    out = new PrintStream(FileUtils.openOutputStream(outputFile));
                    // Write the label
                    out.println(label);
                    // Where there are 1's in the image, create a point in the point cloud
                    for (int row = 0; row < imageRows; row++) {
                        for (int col = 0; col < imageCols; col++) {
                            if (image[row][col] == 1) {
                                out.println(row + "," + col);
                            }
                        }
                    }
                } finally {
                    try {
                        out.flush();
                    } catch (Exception e) {
                    }
                    IOUtils.closeQuietly(out);
                }
            }
        } finally {
            IOUtils.closeQuietly(imagesIn);
            IOUtils.closeQuietly(labelsIn);
        }
    }

    private static void print(String msg) {
        if (PRINT) {
            System.out.print(msg);
        }
    }
}
