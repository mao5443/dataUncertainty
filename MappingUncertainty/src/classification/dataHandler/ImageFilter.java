/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package classification.dataHandler;

/**
 *
 * @author cisc
 */
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class ImageFilter extends FileFilter  {

    @Override
    public boolean accept(File f) {
        
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            //if (extension.equals(Utils.shp)||extension.equals(Utils.csv))
            if (extension.equals(Utils.jpeg)||extension.equals(Utils.jpg))
            {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
       return "JPEG File Interchange Format (*.jpg;*.jpeg)";
    }

}
