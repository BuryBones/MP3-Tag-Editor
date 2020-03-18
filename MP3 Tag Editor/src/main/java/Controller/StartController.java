package main.java.Controller;

import main.java.Model.StartModel;
import main.java.View.StartView;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class StartController {

    private StartView view;
    private StartModel model;

    public void setView(StartView view) {
        this.view = view;
    }
    public void setModel(StartModel model) {
        this.model = model;
    }

    public void modify() throws UnsupportedTagException, InvalidDataException, IOException {
        model.modify();
    }

    public File getDirectory() {
        return model.getDirectory();
    }
    public void setDirectory(File dir) {
        model.setDirectory(dir);
    }

    public FileFilter getFilter(String exception, String description) {
        return model.getFilter(exception, description);
    }
    public File[] getFilesToShow() {
        return model.getFilesToShow();
    }
    public void clearSelectedFiles() {
        model.clearSelectedFiles();
    }

    public void setSelectedFiles(ArrayList<File> list) {
        model.setSelectedFiles(list);
    }
    public void setFileSelected(boolean b) {
        model.setFileSelected(b);
    }
    public boolean isFileSelected() {
        return model.isFileSelected();
    }

    public String[][] showProperties() throws UnsupportedTagException, InvalidDataException, IOException {
        return model.showProperties();
    }
}
