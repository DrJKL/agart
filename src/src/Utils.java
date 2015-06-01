package src;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {
    public static final String jpeg = "jpeg";
    public static final String jpg = "jpg";
    public static final String gif = "gif";
    public static final String tiff = "tiff";
    public static final String tif = "tif";
    public static final String png = "png";
    public static final List<String> extensions = Arrays.asList(jpeg, jpg, gif, tiff, tif, png);

    public static boolean validExtension(String extension) {
        return extension != null && extensions.contains(extension);
    }

    /** Get the extension of a file. */
    public static String getExtension(File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        final java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        }
        System.err.println("Couldn't find file: " + path);
        return null;
    }
}