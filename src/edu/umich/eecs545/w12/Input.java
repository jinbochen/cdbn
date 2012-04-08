package edu.umich.eecs545.w12;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Will ignore the RGB part of this at first at only focus on the
 * 3D coordinate data. The layers will in fact each be a three dimensional cube.
 * The visible layer will be sparse, with binary-valued points in the space
 * indicating that the object has a boundary at that point (1) or not (0).
 * Together, (1) points will take the 3D shape of the observed object.
 * 
 * Available point data: x, y, z coords, rgb, cameraIndex, distance_from_camera,
 *  segment_number, and label_number
 * 
 * @author Augie
 */
public class Input {

    // Points
    public final Map<Integer, Set<Integer>> points = new HashMap<Integer, Set<Integer>>();
    // Class
    public int label;
    // # of points
    public int pointCount;

    public Input(File file) throws IOException, NullPointerException {
        // Check for null
        if (file == null) {
            throw new NullPointerException("Point cloud file is null.");
        }
        // Check for existence
        if (!file.exists()) {
            throw new IOException("File does not exist.");
        }
        // Make sure it is not a directory
        if (file.isDirectory()) {
            throw new IOException("Expected a file, not a directory.");
        }
        // Open a stream to read from the file
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(file)));
            read(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public Input(BufferedReader in) throws IOException, NullPointerException {
        read(in);
    }

    public final boolean value(int x, int y) {
        return points.containsKey(x) && points.get(x).contains(y);
    }

    private void read(BufferedReader in) throws IOException, NullPointerException {
        if (in == null) {
            throw new NullPointerException("Input is null.");
        }
        // Read the label
        label = Integer.valueOf(in.readLine());
        // Read the number of points
        pointCount = Integer.valueOf(in.readLine());
        // Read the points
        for (int i = 0; i < pointCount; i++) {
            String[] coords = in.readLine().split(",");
            int x = Integer.valueOf(coords[0]);
            if (!points.containsKey(x)) {
                points.put(x, new HashSet<Integer>());
            }
            points.get(x).add(Integer.valueOf(coords[1]));
        }
    }

    public static Input[] readInputs(BufferedReader in) throws IOException, NullPointerException {
        if (in == null) {
            throw new NullPointerException("Input is null.");
        }
        // Read the number of inputs
        int count = Integer.valueOf(in.readLine());
        // Read each of the inputs
        Input[] inputs = new Input[count];
        for (int i = 0; i < count; i++) {
            inputs[i] = new Input(in);
        }
        return inputs;
    }
}
