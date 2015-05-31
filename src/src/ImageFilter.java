package src;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/* ImageFilter.java is used by FileChooserDemo2.java. */
public class ImageFilter extends FileFilter {

    // Accept all directories and all gif, jpg, tiff, or png files.
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        final String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.tiff) || extension.equals(Utils.tif)
                    || extension.equals(Utils.gif) || extension.equals(Utils.jpeg)
                    || extension.equals(Utils.jpg) || extension.equals(Utils.png)) {
                return true;
            }
            return false;
        }

        return false;
    }

    // The description of this filter
    @Override
    public String getDescription() {
        return "Just Images";
    }
}