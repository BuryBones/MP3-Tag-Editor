package main.java.Model;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MP3Data {

    /*
    This class contains the mp3 file itself, it's directory, tag object and fields to be written to the tag
     */

    // logger setup
    private static Logger logger = Logger.getLogger(MP3Data.class.getSimpleName());
    static
    {
            logger.addHandler(main.java.Model.Main.warning);
            logger.addHandler(main.java.Model.Main.common);
    }

    private Mp3File mp3File;
    private ID3v2 tag;

    private File directory;

    private String name = "";
    private String artist = "";
    private String album = "";
    private String year = "";
    private String number = "";
    private String genre = "";

    MP3Data(File file, File directory) throws UnsupportedTagException, IOException, InvalidDataException {
        this.directory = directory;
        mp3File = new Mp3File(file);
        if (mp3File.hasId3v2Tag()) {
            tag = mp3File.getId3v2Tag();

            name = tag.getTitle();
            artist = tag.getArtist();
            album = tag.getAlbum();
            year = tag.getYear();
            number = tag.getTrack();
            genre = tag.getGenreDescription();
        } else {
            tag = new ID3v24Tag();
            mp3File.setId3v2Tag(tag);
        }
        logger.info("MP3Data object created from file: " + file.getName());
    }

    String getName() {
        return name;
    }
    String getArtist() {
        return artist;
    }
    String getAlbum() {
        return album;
    }
    String getYear() {
        return year;
    }
    String getNumber() {
        return number;
    }
    String getGenre() {
        return genre;
    }

    void setName(String name) {
        this.name = name;
    }
    void setArtist(String artist) {
        this.artist = artist;
    }
    void setAlbum(String album) {
        this.album = album;
    }
    void setYear(String year) {
        this.year = year;
    }
    void setNumber(String number) {
        this.number = number;
    }
    void setGenre(String genre) {
        this.genre = genre;
    }

    private void updateTag(boolean version2_3) {
        if (version2_3) tag = new ID3v23Tag();
        tag.setAlbum(album);
        tag.setArtist(artist);
        try {
            tag.setGenreDescription(genre);
        } catch (IllegalArgumentException ae) {
            logger.log(Level.WARNING,String.format("Genre tag '%s' wasn't set! 'Other' is used instead.", genre),ae);
            genre = "Other";
            tag.setGenreDescription(genre);
        }
        tag.setTitle(name);
        tag.setTrack(number);
        tag.setYear(year);
        logger.info(String.format("Tag updated. Title: %s; Artist: %s; Album: %s; Track#: %s; Year: %s; Genre: %s",
                name, artist, album, number, year, genre));
    }

    void saveChanges(boolean version2_3) throws IOException, NotSupportedException {

        StringBuilder fileNameSb = new StringBuilder();
        fileNameSb.append(artist + " - " + name);
        updateTag(version2_3);
        mp3File.setId3v2Tag(tag);
        File savingFolder = new File(directory.toString() + "\\" + "Modified");
        if (!savingFolder.isDirectory()) {
            if (!savingFolder.mkdir()) throw new IOException("Failed to create a folder " + savingFolder.getAbsolutePath());
        }
        if (!fileNameSb.toString().endsWith(".mp3")) fileNameSb.append(".mp3");
        mp3File.save(savingFolder.getAbsolutePath() + "\\" + fileNameSb);
        logger.info(String.format("MP3 File %s saved to %s", fileNameSb, savingFolder));
    }

    String getShortName() {
        Path path = Paths.get(mp3File.getFilename());
        return path.getFileName().toString();
    }

    String[][] showProperties() {
        String[][] result;
        if ( mp3File.hasId3v2Tag()) {
            result = new String[][]{
                    {"File:",getShortName()},
                    {"ID3v2","yes"},
                    {"Track",tag.getTrack()},
                    {"Title",tag.getTitle()},
                    {"Album",tag.getAlbum()},
                    {"Artist",tag.getArtist()},
                    {"Album Artist",tag.getAlbumArtist()},
                    {"Original Artist",tag.getOriginalArtist()},
                    {"Date",tag.getDate()},
                    {"Year",tag.getYear()},
                    {"Genre Description",tag.getGenreDescription()},
                    {"Lyrics",tag.getLyrics()},
                    {"Part of Set",tag.getPartOfSet()},
                    {"Comment",tag.getComment()},
                    {"iTunes Comment",tag.getItunesComment()},
                    {"Composer",tag.getComposer()},
                    {"Publisher",tag.getPublisher()},
                    {"Copyright",tag.getCopyright()},
                    {"Encoder",tag.getEncoder()},
                    {"Grouping",tag.getGrouping()},
                    {"Version",tag.getVersion()},
                    {"BPM",String.valueOf(tag.getBPM())},
            };
        } else {
            result = new String[][]{
                    {"ID3v2", "no"},
            };
        }
        return result;
    }

    String generateOrderNumber(ArrayList<MP3Data> list) {
        int listSize = list.size();
        int digits = 0;
        while (listSize != 0) {
            listSize /= 10;
            digits++;
        }
        String format = "%0" + digits + "d";
        return String.format(format,list.indexOf(this)+1);
    }
}

