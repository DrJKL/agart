package client;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import src.Utils;

/* ImageFilter.java is used by FileChooserDemo2.java. */
public class ImageFilter extends FileFilter {

  // Accept all directories and all gif, jpg, tiff, or png files.
  @Override
  public boolean accept(File f) {
    return f.isDirectory() || Utils.validExtension(Utils.getExtension(f));
  }

  @Override
  public String getDescription() {
    return "Just Images";
  }
}