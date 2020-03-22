package main.java.View;

import main.java.Controller.ModifyController;
import com.mpatric.mp3agic.NotSupportedException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ModifyView extends JDialog {

    private ModifyController controller;
    private StartView startView = StartView.getInstance();

    // logger setup
    private static Logger logger = Logger.getLogger(ModifyView.class.getSimpleName());
    static {
        try {
            FileHandler warning = new FileHandler("errors.log");
            warning.setFormatter(new SimpleFormatter());
            warning.setLevel(Level.WARNING);
            logger.addHandler(warning);
        } catch (IOException ioE) {
            String errorMessage = logger.getName() + " logger setup error.";
            System.err.println(errorMessage + " Exception message: " + ioE.getMessage());
            main.java.Model.Main.mainLogger.log(Level.WARNING, errorMessage, ioE);
        }
        try {
            FileHandler common = new FileHandler("common.log");
            common.setFormatter(new SimpleFormatter());
            common.setLevel(Level.ALL);
            logger.addHandler(common);
        } catch (IOException ioE) {
            String errorMessage = logger.getName() + " logger setup error.";
            System.err.println(errorMessage + " Exception message: " + ioE.getMessage());
            main.java.Model.Main.mainLogger.log(Level.WARNING, errorMessage, ioE);
        }
    }

    private boolean isManuallyChanged;
    private Object[][] tableData;

    /*
    Number of fields used in Modify Window. Must be correct to extract data from the "Manual" table.
    [0] title; [1] track; [2] artist; [3] album; [4] year; [5] genre;
     */
    private int numberOfFields = 6;

    JPanel mainPanel;

    private JButton proposeArtist = new JButton("Propose an Artist");
    private JButton proposeTitle = new JButton("Propose a Title");
    private JButton saveTableButton = new JButton("Save");
    private JLabel title = new JLabel("Title: ",SwingConstants.CENTER);
    private JTextField titleText = new JTextField();
    private JLabel track = new JLabel("Track #: ",SwingConstants.CENTER);
    private JTextField trackText = new JTextField();
    private JLabel artist = new JLabel("Artist: ",SwingConstants.CENTER);
    private JTextField artistText = new JTextField();
    private JLabel album = new JLabel("Album: ",SwingConstants.CENTER);
    private JTextField albumText = new JTextField();
    private JLabel year = new JLabel("Year: ",SwingConstants.CENTER);
    private JTextField yearText = new JTextField();
    private JLabel genre = new JLabel("Genre: ",SwingConstants.CENTER);
    private JSpinner genreSpinner;
    private JButton accept = new JButton("Accept");
    private JButton cancel = new JButton("Cancel");
    private JLabel tagVersion = new JLabel("Tag version (default is 3v2.4)", SwingConstants.CENTER);
    private JCheckBox tagCheck = new JCheckBox("Use 3v2.3");

    private JTable table;

    public ModifyView(ModifyController controller, boolean isMultipleFiles) {
        super(new JFrame(),"Modify tags",true);
        startView.setModifyView(this);
        this.controller = controller;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.clearSelectedFiles();
                e.getWindow().dispose();
                startView.setModifyView(null);
            }
        });
        isManuallyChanged = false;

        setBounds(500,200,500,400);
        setResizable(false);

        mainPanel = new JPanel();

        if (isMultipleFiles) {
            JTabbedPane tabbedPane = multView();
            addCommonElements();
            add(tabbedPane);

        } else {
            singleView();
            addCommonElements();
            add(mainPanel);
        }
        mainPanel.revalidate();
    }

    private JTabbedPane multView() {
        // Table set-up
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[] {"File"," # ","Title"});
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col!=0;
            }
        };
        table.setGridColor(Color.PINK);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.setColumnSelectionAllowed(false);
        columnModel.getColumn(0).setPreferredWidth(230);
        columnModel.getColumn(1).setPreferredWidth(20);
        columnModel.getColumn(2).setPreferredWidth(200);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setPreferredScrollableViewportSize(new Dimension(455,270));

        // Fill the table with data
        Object[][] data = controller.getTableData();
        for (Object[] innerArray : data) {
            tableModel.addRow(innerArray);
        }

        // Set-up a Manual Settings Panel, add a table there, add a 'Save' button there
        JPanel manualPanel = new JPanel();
        FlowLayout manualPanelLayout = new FlowLayout();
        manualPanelLayout.setAlignment(FlowLayout.CENTER);
        manualPanel.setLayout(manualPanelLayout);
        manualPanel.add(new JScrollPane(table));
        manualPanel.add(saveTableButton);
        saveTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", saveTableButton.getText()));
                isManuallyChanged = true;
                tableData = new Object[table.getRowCount()][2];
                for (int i = 0; i < table.getRowCount(); i++) {
                    tableData[i][0] = table.getValueAt(i,2);
                    tableData[i][1] = table.getValueAt(i,1);
                }
                JOptionPane.showMessageDialog(mainPanel, "Changes saved!", "Details saved",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        mainPanel.setLayout(new GridLayout(8,2));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Modify", mainPanel);
        tabbedPane.addTab("Manual settings", manualPanel);
        trackText.setText("Auto-generated for multiple files");
        trackText.setEditable(false);
        titleText.setText("Auto-generated for multiple files");
        titleText.setEditable(false);
        artistText.setText(controller.getArtistName());

        return tabbedPane;
    }
    private void singleView() {
        mainPanel.setLayout(new GridLayout(9,2));

        mainPanel.add(proposeArtist);
        mainPanel.add(proposeTitle);
        proposeArtist.setToolTipText("Tries to get an artist if you enter a title");
        proposeArtist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", proposeArtist.getText()));
                if (titleText.getText().isEmpty() || titleText.getText().equals(" ? ")) {
                    titleText.setText("Enter a title");
                    artistText.setText(" ? ");
                } else {
                    artistText.setText(controller.excludeString(titleText.getText()));
                }
            }
        });
        proposeTitle.setToolTipText("Tries to get a title if you enter an artist name");
        proposeTitle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", proposeTitle.getText()));
                if (artistText.getText().isEmpty() || artistText.getText().equals(" ? ")) {
                    artistText.setText("Enter an artist");
                    titleText.setText(" ? ");
                } else {
                    titleText.setText(controller.excludeString(artistText.getText()));
                }
            }
        });
    }
    private void addCommonElements() {
        // Genre Spinner set-up
        SpinnerListModel genresModel = new SpinnerListModel(controller.getGenres());
        genreSpinner = new JSpinner(genresModel);

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", cancel.getText()));
                startView.resetSelectedFiles();
                startView.setModifyView(null);
                dispose();
            }
        });
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", accept.getText()));
                try {
                    if (isManuallyChanged) {
                        controller.submit(getDataFromTable(), tagCheck.isSelected());
                    } else {
                        controller.submit(getDataFromFields(), tagCheck.isSelected());
                    }
                    controller.clearSelectedFiles();
                    startView.resetSelectedFiles();
                    startView.setModifyView(null);
                    JOptionPane.showMessageDialog(mainPanel, "File successfully modified!", "Success",JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (NotSupportedException nse) {
                    controller.clearSelectedFiles();
                    startView.resetSelectedFiles();
                    startView.setModifyView(null);
                    JOptionPane.showMessageDialog(mainPanel, nse.getMessage(), "Tag modifying error!",JOptionPane.ERROR_MESSAGE);
                } catch (IOException ioe) {
                    controller.clearSelectedFiles();
                    startView.resetSelectedFiles();
                    startView.setModifyView(null);
                    JOptionPane.showMessageDialog(mainPanel, ioe.getMessage(), "I/O system error!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tagVersion.setToolTipText("Default is 3v2.4. Supports UTF-8");
        tagCheck.setToolTipText("3v2.3 is the most popular version");
        mainPanel.add(title);
        mainPanel.add(titleText);
        mainPanel.add(track);
        mainPanel.add(trackText);
        mainPanel.add(artist);
        mainPanel.add(artistText);
        mainPanel.add(album);
        mainPanel.add(albumText);
        mainPanel.add(year);
        mainPanel.add(yearText);
        mainPanel.add(genre);
        mainPanel.add(genreSpinner);
        mainPanel.add(tagVersion);
        mainPanel.add(tagCheck);
        mainPanel.add(cancel);
        mainPanel.add(accept);
    }
    private Object[][] getDataFromTable() {
        // [0] title; [1] track; [2] artist; [3] album; [4] year; [5] genre;
        Object[][] data = new Object[tableData.length][numberOfFields];
        for (int i = 0; i < tableData.length; i++) {
            data[i] = new Object[] {
                    tableData[i][0],tableData[i][1], artistText.getText(),
                    albumText.getText(), yearText.getText(), genreSpinner.getValue()
            };
        }
        return data;
    }
    private String[] getDataFromFields() {
        return new String[]{
                titleText.getText(), trackText.getText(), artistText.getText(),
                albumText.getText(), yearText.getText(), (String) genreSpinner.getValue()
        };
    }
}
