package main.java.Model;

import main.java.Controller.ModifyController;
import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.NotSupportedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyModel {

    // logger setup
    private static Logger logger = Logger.getLogger(ModifyModel.class.getSimpleName());
    static
    {
        logger.addHandler(main.java.Model.Main.warning);
        logger.addHandler(main.java.Model.Main.common);
    }

    private StartModel startModel;
    private ModifyController controller;

    public ModifyModel(StartModel startModel, boolean isMultipleFiles) {
        this.startModel = startModel;
        controller = new ModifyController(this,isMultipleFiles);
        this.isMultipleFiles = isMultipleFiles;

        logger.info(String.format("ModifyModel, isMultipleFiles: %b", isMultipleFiles));
    }

    private boolean isMultipleFiles;

    public void clearSelectedFiles() {
        startModel.clearSelectedFiles();
    }

    private String title = "";
    private String track = "";
    private String artist = "";
    private String album = "";
    private String year = "";
    private String genre = "";
    private String originalArtist = "";

    // The following method is used if there were no manual changes in modify 'manual' table
    public void submit(String[] submitData, boolean use3v23) throws IOException, NotSupportedException {
        // [0] title; [1] track; [2] artist; [3] album; [4] year; [5] genre;
        logger.info(String.format("Submit invoked (NO manual changes) multipleFiles: %b", isMultipleFiles));
        if (isMultipleFiles) {
            for ( MP3Data d : startModel.getMp3Files() ) {
                d.setArtist(submitData[2]);
                d.setAlbum(submitData[3]);
                d.setYear(submitData[4]);
                d.setGenre(submitData[5]);
                d.setNumber(d.generateOrderNumber(startModel.getMp3Files()));
                d.setName(controller.excludeString(d.getShortName(),submitData[2]));

                d.saveChanges(use3v23);
            }
        } else {
            MP3Data singleFile = startModel.getMp3Files().get(0);
            singleFile.setName(submitData[0]);
            singleFile.setArtist(submitData[2]);
            singleFile.setAlbum(submitData[3]);
            singleFile.setNumber(submitData[1]);
            singleFile.setYear(submitData[4]);
            singleFile.setGenre(submitData[5]);

            singleFile.saveChanges(use3v23);
        }
    }
    // the following method is used when modify 'manual' table was changed and saved
    public void submit (Object[][] submitData, boolean use3v23) throws IOException, NotSupportedException {
        // [0] title; [1] track; [2] artist; [3] album; [4] year; [5] genre;
        logger.info("Submit invoked (WITH manual changes)");
        for ( int i = 0; i < startModel.getMp3Files().size(); i++ ) {
            MP3Data d = startModel.getMp3Files().get(i);
            Object[] fileData = submitData[i];
            d.setName((String)fileData[0]);
            d.setNumber((String)fileData[1]);
            d.setArtist((String)fileData[2]);
            d.setAlbum((String)fileData[3]);
            d.setYear((String)fileData[4]);
            d.setGenre((String)fileData[5]);

            d.saveChanges(use3v23);
        }
    }
    private String[] parse(String s) {
        return s.toLowerCase().split("[- ]+");
    }
    private String findBandName(ArrayList<String[]> fileNames) {
        String[] zeroLine = fileNames.get(0);
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < zeroLine.length; i++ ) {
            String searchedWord = zeroLine[i];
            boolean presentInEveryName = false;
            toNextSearchedWord:
            for ( int k = 1; k < fileNames.size(); k++ ) {
                String[] otherSongName = fileNames.get(k);
                if (Arrays.stream(otherSongName).anyMatch(searchedWord::equalsIgnoreCase)) {
                    presentInEveryName = true;
                } else {
                    presentInEveryName = false;
                    break toNextSearchedWord;
                }
            }
            if (presentInEveryName) {
                sb.append(searchedWord);
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    public String getArtistName() {
        ArrayList<MP3Data> array = startModel.getMp3Files();
        ArrayList<String[]> fileNamesInWords = new ArrayList<>(array.size());
        for ( MP3Data data : array) {
            fileNamesInWords.add(parse(data.getShortName()));
        }
        String result = findBandName(fileNamesInWords);
        if (result.length() < 1 ) return result;
        StringBuilder sb = new StringBuilder();
        sb.append(result.substring(0,1).toUpperCase());
        for (int i = 1; i < result.length() ; i++) {
            if (result.charAt(i) != ' ' && result.charAt(i-1) == ' ') {
                sb.append(result.substring(i,i+1).toUpperCase());
            } else {
                sb.append(result.substring(i,i+1));
            }
        }
        return sb.toString();
    }

    public String excludeString(String excludeFrom, String excludeThat) {
        if (excludeFrom == null) return "";
        if (excludeThat == null) return excludeFrom;
        String result = "";
        Pattern pattern = Pattern.compile("[ -]*" + excludeThat + "[ -]*",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(excludeFrom);
        if (matcher.find()) {
            result = matcher.replaceAll("");
            result = result.endsWith(".mp3") ? result.substring(0,result.length()-4) : result;
        }
        return result.trim();
    }
    public String excludeString(String excludeThat) {
        if (excludeThat == null) return "";
        String result = "";
        Pattern pattern = Pattern.compile("[ -]*" + excludeThat + "[ -]*",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(startModel.getMp3Files().get(0).getShortName());
        if (matcher.find()) {
            result = matcher.replaceAll("");
            result = result.endsWith(".mp3") ? result.substring(0,result.length()-4) : result;
        }
        return result.trim();
    }

    public Object[][] getTableData() {
        ArrayList<MP3Data> list = startModel.getMp3Files();
        Object[][] result = new Object[list.size()][3];
        for (int i = 0; i < list.size(); i++) {
            MP3Data d = list.get(i);
            Object[] innerArray = {
                    d.getShortName(),
                    d.generateOrderNumber(list),
                    excludeString(d.getShortName(), getArtistName())
            };
            result[i] = innerArray;
        }
        return result;
    }

    public String[] getGenres() {
        return ID3v1Genres.GENRES;
    }
}
