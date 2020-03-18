package main.java.Model;

import main.java.Controller.StartController;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class StartModel {


    private StartController controller;
    private ModifyModel modifyModel;

    public void setController(StartController controller) {
        this.controller = controller;
    }

    /*
    'directory' is used to show all files of acceptable format in the same folder with selected file.
    It is needed as well by MP3Data object to save a modified file.
     */
    private File directory;
    private boolean isFileSelected = false;
    /*
    'selectedFiles' is used to update status bar in the start frame,
    and to determine which version of ModifyView to show.
    The files are not mp3files yet
     */
    private ArrayList<File> selectedFiles = new ArrayList<>();
    /*
    Upon pressing the 'Modify' button files MP3Data objects are made from selected files
    and kept in the following ArrayList
     */
    private ArrayList<MP3Data> mp3Files = new ArrayList<>();

    public void setDirectory(File directory) {
        this.directory = directory;
    }
    public File getDirectory() {
        return directory;
    }

    public boolean isFileSelected() {
        return isFileSelected;
    }
    public void setFileSelected(boolean fileSelected) {
        isFileSelected = fileSelected;
    }

    public ArrayList<File> getSelectedFiles() {
        return selectedFiles;
    }
    public void setSelectedFiles(ArrayList<File> selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    public ArrayList<MP3Data> getMp3Files() {
        return mp3Files;
    }
    public void setMp3Files(ArrayList<MP3Data> mp3Files) {
        this.mp3Files = mp3Files;
    }

    public ExtensionFileFilter getFilter(String extension, String description) {
        return new ExtensionFileFilter(extension,description);
    }
    public File[] getFilesToShow() {
        File dir = new File(directory.getAbsolutePath());
        File[] filesInDirectory = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        });
        return filesInDirectory;
    }
    public void clearSelectedFiles() {
        selectedFiles.clear();
        mp3Files.clear();
        isFileSelected = false;
    }
    public String[][] showProperties() throws UnsupportedTagException, InvalidDataException, IOException {
        File file = selectedFiles.get(0);
        MP3Data data = null;
        try {
            data = new MP3Data(file,file);

        } catch (UnsupportedTagException tagE) {
            String toShow = file.getName();
            throw new UnsupportedTagException(toShow);
        } catch (InvalidDataException | IllegalArgumentException dataE) {
            String toShow = file.getName();
            throw new InvalidDataException(toShow);
        } catch (IOException ioE) {
            String toShow = file.getName();
            throw new IOException(toShow);
        }
        return data.showProperties();
    }

    public void modify() throws UnsupportedTagException, InvalidDataException, IOException {
        if (mp3Files == null) mp3Files = new ArrayList<>(selectedFiles.size());
        for (File file : selectedFiles) {
            try {
                mp3Files.add(new MP3Data(file, directory));
            } catch (UnsupportedTagException tagE) {
                String toShow = file.getName();
                throw new UnsupportedTagException(toShow);
            } catch (InvalidDataException | IllegalArgumentException dataE) {
                String toShow = file.getName();
                throw new InvalidDataException(toShow);
            } catch (IOException ioE) {
                String toShow = file.getName();
                throw new IOException(toShow);
            }
        }
        modifyModel = new ModifyModel(this,mp3Files.size() > 1);
    }
}
