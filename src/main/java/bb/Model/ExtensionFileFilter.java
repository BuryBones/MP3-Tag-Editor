package bb.Model;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExtensionFileFilter extends FileFilter {

    String description;
    String [] extensions;

    public ExtensionFileFilter(String description, String extension) {
        this.description = description;
        this.extensions = new String[] {extension.toLowerCase()};
    }
    public ExtensionFileFilter(String description, String[] extensions) {
        if (description == null) {
            this.description = extensions[0];
        } else {
            this.description = description;
        }
        this.extensions = (String[]) extensions;
        for (int i = 0; i < this.extensions.length; i++) {
            this.extensions[i] = this.extensions[i].toLowerCase();
        }
    }
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } else {
            String path = f.getAbsolutePath().toLowerCase();
            for (int i = 0, n = extensions.length; i < n; i++) {
                String extension = "." + extensions[i];
                if (path.endsWith(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
