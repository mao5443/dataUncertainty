/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;


import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;
/**
 *
 * @author cisc
 */
public class ShpFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            //if (extension.equals(Utils.shp)||extension.equals(Utils.csv))
            if (extension.equals(Utils.shp))
            {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Shape files";
    }
}
