package main.java.View;

import main.java.Controller.StartController;
import main.java.Model.Main;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StartView {

    // Singleton
    private static StartView original = new StartView();
    public static StartView getInstance() {
        if ( original == null ) {
            original = new StartView();
        }
        return original;
    }

    // Controller
    private StartController controller;
    public void setController(StartController controller) {
        this.controller = controller;
    }

    // logger setup
    private static Logger logger = Logger.getLogger(StartView.class.getSimpleName());
    static
    {
        logger.addHandler(main.java.Model.Main.warning);
        logger.addHandler(main.java.Model.Main.common);
    }

    private ModifyView modifyView;
    private JFrame frame;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JLabel status = new JLabel("No Files Selected",SwingConstants.CENTER);

    public JList<File> fileList;

    private StartView() {
        frame = getFrame();

        frame.setLayout(new GridBagLayout());

        Insets insets = new Insets(5,3,5,3);
        GridBagConstraints leftConst = new GridBagConstraints(0,0,1,1,0.7d,1.0d,GridBagConstraints.FIRST_LINE_START,GridBagConstraints.BOTH,insets,0,0);
        GridBagConstraints rightConst = new GridBagConstraints(1,0,1,1,0.3d,1.0d,GridBagConstraints.FIRST_LINE_END,GridBagConstraints.BOTH,insets,0,0);

        leftPanel = getLeftPanel();
        rightPanel = getRightPanel();

        frame.add(leftPanel,leftConst);
        frame.add(rightPanel,rightConst);
        frame.revalidate();
    }

    // Setup & inner methods
    private JFrame getFrame() {
        JFrame result = new JFrame("BB MP3 Tag Editor (v" + Main.getVersion() + ")");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDimension = toolkit.getScreenSize();

        result.setBounds(100, 50, screenDimension.width / 4 * 3, screenDimension.height / 10 * 8);
        Dimension minimum = new Dimension(500, 400);
        result.setMinimumSize(minimum);
        result.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        result.setVisible(true);
        return result;
    }
    private JPanel getLeftPanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        JButton select = new JButton("Select File");
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", select.getText()));
                if (controller.isFileSelected()) controller.clearSelectedFiles();
                JFileChooser chooser = getFileChooser();
                chooser.showDialog(result,"Select");
            }
        });
        JButton modify = new JButton("Modify Tags");
        modify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", modify.getText()));
                if (controller.isFileSelected()) {
                    try {
                        controller.modify();
                    } catch (UnsupportedTagException tagEx) {
                        String message = "Unsupported tag. Error in file: " + tagEx.getMessage();
//                        controller.clearSelectedFiles();
                        resetSelectedFiles();
                        JOptionPane.showMessageDialog(result, message, "MP3 tag error!",JOptionPane.ERROR_MESSAGE);
                    } catch (InvalidDataException dataEx) {
                        String message = "Invalid data. Error in file: " + dataEx.getMessage();
//                        controller.clearSelectedFiles();
                        resetSelectedFiles();
                        JOptionPane.showMessageDialog(result, message, "Invalid file",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioEx) {
                        String message = "Error while reading the following file: " + ioEx.getMessage();
//                        controller.clearSelectedFiles();
                        resetSelectedFiles();
                        JOptionPane.showMessageDialog(result, message, "Input/output error!",JOptionPane.ERROR_MESSAGE);
                    }
                    if (modifyView != null) {
                        modifyView.setVisible(true);
                    } else {
                        resetSelectedFiles();
                        JOptionPane.showMessageDialog(result, "Failed to modify selected files!", "Error!",JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(result, "File is not selected. Select a file and try again.", "File not selected!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        fileList = new JList<>();
        JScrollPane filesScrollPane = new JScrollPane(fileList);
        fileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (fileList.getSelectedValue() != null) {
                        controller.setSelectedFiles(new ArrayList<>(fileList.getSelectedValuesList()));
                        controller.setFileSelected(true);
                        updateStatus(fileList.getSelectedValuesList().size());
                    } else {
                        controller.clearSelectedFiles();
                        updateStatus(0);
                    }
                }
            }
        });

        Insets insets = new Insets(0, 0, 0, 0);
        GridBagConstraints selectConst = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, insets, 0, 0);
        GridBagConstraints modifyConst = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, insets, 0, 0);
        GridBagConstraints filesScrollPaneConst = new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);

        result.add(select,selectConst);
        result.add(modify,modifyConst);
        result.add(filesScrollPane,filesScrollPaneConst);

        return result;
    }
    private JPanel getRightPanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        DefaultTableModel tabMod = new DefaultTableModel();
        tabMod.setColumnCount(2);
        tabMod.setColumnIdentifiers(new String[] {"File:"," not selected"});

        JTable statusTable = new JTable(tabMod);
        statusTable.setDefaultEditor(Object.class,null);
        statusTable.getTableHeader().setReorderingAllowed(false);
        statusTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        statusTable.setFillsViewportHeight(true);

        TableColumnModel colMod = statusTable.getColumnModel();
        colMod.getColumn(0).setMinWidth(50);
        colMod.getColumn(0).setPreferredWidth(130);
        colMod.getColumn(1).setMinWidth(200);
        colMod.getColumn(1).setPreferredWidth(500);

        JScrollPane tableScrollPane = new JScrollPane(statusTable);

        Insets insets = new Insets(0, 0, 0, 0);
        GridBagConstraints showConst = new GridBagConstraints(0, 0, 1, 1, 1.0d, 0.1d, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, insets, 0, 0);
        GridBagConstraints tableScrollPaneConst = new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.7d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
        GridBagConstraints statusConst = new GridBagConstraints(0, 2, 1, 1, 1.0d, 0.1d, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
        GridBagConstraints exitConst = new GridBagConstraints(0, 3, 1, 1, 1.0d, 0.1d, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.BOTH, insets, 0, 0);

        JButton show = new JButton("Show File Info");
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", show.getText()));
                if(controller.isFileSelected()) {
                    try {
                        String[][] combinedTableData = controller.showProperties();
                        String[] columnIdentifiers = combinedTableData[0];
                        String[][] tableData = Arrays.copyOfRange(combinedTableData, 1, combinedTableData.length);
                        tabMod.setDataVector(tableData,columnIdentifiers);
                    } catch (UnsupportedTagException tagEx) {
                        String message = "Unsupported tag. Error in file: " + tagEx.getMessage();
//                        controller.clearSelectedFiles();
                        resetSelectedFiles();
                        JOptionPane.showMessageDialog(result, message, "MP3 tag error!",JOptionPane.ERROR_MESSAGE);
                    } catch (InvalidDataException dataEx) {
                        String message = "Invalid data. Error in file: " + dataEx.getMessage();
//                        controller.clearSelectedFiles();
                        resetSelectedFiles();
                        JOptionPane.showMessageDialog(result, message, "Invalid file",JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioEx) {
                        String message = "Error while reading the following file: " + ioEx.getMessage();
//                        controller.clearSelectedFiles();
                        resetSelectedFiles();
                        JOptionPane.showMessageDialog(result, message, "Input/output error!",JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(result, "File is not selected. Select a file and try again.", "File not selected!",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("%s button pressed", exit.getText()));
                frame.dispose();
                System.exit(0);
            }
        });

        result.add(show,showConst);
        result.add(tableScrollPane,tableScrollPaneConst);
        result.add(status,statusConst);
        result.add(exit,exitConst);

        return result;
    }
    private JFileChooser getFileChooser() {
        JFileChooser result = new JFileChooser();
        File theDirectory = controller.getDirectory();
        if ( (theDirectory != null) && (theDirectory.isDirectory()) ) {
            result.setCurrentDirectory(theDirectory);
        }
        result.setFileFilter(controller.getFilter(".mp3", "mp3"));
        result.setAcceptAllFileFilterUsed(false);
        result.setDialogTitle("Select a file");
        result.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setDirectory(result.getCurrentDirectory());
                fileList.setListData(controller.getFilesToShow());
                fileList.setSelectedValue(result.getSelectedFile(),true);
            }
        });
        return result;
    }

    // Public methods
    public void resetSelectedFiles() {
        fileList.clearSelection();
    }
    public void updateStatus(int filesNumber) {
        if (filesNumber != 0) {
            status.setText(filesNumber + " files selected");
        } else {
            status.setText("No files selected");
        }
    }
    public void setModifyView(ModifyView modifyView) {
        this.modifyView = modifyView;
    }
}
