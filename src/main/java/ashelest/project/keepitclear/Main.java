package ashelest.project.keepitclear;

import java.awt.EventQueue;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.*;

import java.awt.BorderLayout;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.Date;
import java.util.Calendar;

import java.io.File;

public class Main {

    private JFrame frmKeepitclear;
    private JTextField textField;
    private JTextField textField_2;
    private JPasswordField passwordField;
    private Configurator configurator;

    public static final String APPLICATION_NAME = "KeepItClear";
    public static final String ICON_STR = "favicon.png";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frmKeepitclear.setVisible(true);
                    window.frmKeepitclear.setExtendedState(window.frmKeepitclear.NORMAL);
                    window.frmKeepitclear.setResizable(false);
                    setTrayIcon(window.frmKeepitclear);
                    window.frmKeepitclear.setIconImage(new ImageIcon("favicon.png").getImage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        configurator = new Configurator();
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmKeepitclear = new JFrame();
        frmKeepitclear.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmKeepitclear.setTitle("KeepItClear");
        frmKeepitclear.setBounds(100, 100, 500, 550);
        frmKeepitclear.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

        JScrollPane scrollPane = new JScrollPane();
        frmKeepitclear.getContentPane().add(scrollPane);

        JPanel panelMain = new JPanel();
        scrollPane.setViewportView(panelMain);
        panelMain.setLayout(new GridLayout(7, 0, 0, 0));

        JPanel panel_11 = new JPanel();
        panelMain.add(panel_11);
        panel_11.setLayout(new GridLayout(3, 0, 0, 0));

        JLabel lblChooseCleanerType = new JLabel("CHOOSE CLEANER TYPE:");
        panel_11.add(lblChooseCleanerType);
        lblChooseCleanerType.setHorizontalAlignment(SwingConstants.CENTER);

        JRadioButton rdbtnNone = new JRadioButton("NONE");
        panel_11.add(rdbtnNone);

        JRadioButton rdbtnRemover = new JRadioButton("REMOVER");
        panel_11.add(rdbtnRemover);

        JPanel panel = new JPanel();
        panelMain.add(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JRadioButton rdbtnMoveFilesTo = new JRadioButton("MOVE FILES TO LOCAL FOLDER");
        panel.add(rdbtnMoveFilesTo);

        JLabel lblPath = new JLabel("Path: ...");
        lblPath.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblPath, BorderLayout.SOUTH);

        JPanel panel_1 = new JPanel();
        panelMain.add(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));

        JRadioButton rdbtnMoveFilesTo_1 = new JRadioButton("MOVE FILES TO DROPBOX");
        panel_1.add(rdbtnMoveFilesTo_1, BorderLayout.NORTH);

        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));

        JPanel panel_3 = new JPanel();
        panel_2.add(panel_3, BorderLayout.NORTH);

        JLabel label = new JLabel("Login:");
        panel_3.add(label);

        textField = new JTextField();
        textField.setColumns(15);
        panel_3.add(textField);

        JLabel label_1 = new JLabel("Password:");
        panel_3.add(label_1);

        passwordField = new JPasswordField();
        passwordField.setColumns(15);
        panel_3.add(passwordField);

        JPanel panel_4 = new JPanel();
        panel_2.add(panel_4, BorderLayout.CENTER);
        panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel lblAccessToken = new JLabel("Access token:");
        panel_4.add(lblAccessToken);

        JLabel label_2 = new JLabel("...");
        panel_4.add(label_2);

        JButton btnChooseFolder = new JButton("Choose folder");
        btnChooseFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setDialogTitle("KeepItClear: Choose local folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    lblPath.setText(chooser.getSelectedFile().toString());
                    configurator.setLocalMoverFolder(chooser.getSelectedFile().toString());
                }
            }
        });
        panel.add(btnChooseFolder, BorderLayout.EAST);

        JPanel panel_5 = new JPanel();
        panelMain.add(panel_5);
        panel_5.setLayout(new GridLayout(2, 1, 0, 0));

        JRadioButton rdbtnMoveFilesTo_2 = new JRadioButton("MOVE FILES TO GOOGLE DRIVE");
        panel_5.add(rdbtnMoveFilesTo_2);

        JPanel panel_6 = new JPanel();
        panel_5.add(panel_6);

        JLabel lblFolderId = new JLabel("Folder ID:");
        panel_6.add(lblFolderId);

        textField_2 = new JTextField();
        panel_6.add(textField_2);
        textField_2.setColumns(30);

        ButtonGroup radios = new ButtonGroup();
        radios.add(rdbtnNone);
        radios.add(rdbtnRemover);
        radios.add(rdbtnMoveFilesTo);
        radios.add(rdbtnMoveFilesTo_1);
        radios.add(rdbtnMoveFilesTo_2);

        JPanel panel_7 = new JPanel();
        panelMain.add(panel_7);
        panel_7.setLayout(new BorderLayout(0, 0));

        JLabel lblSetPeriods = new JLabel("SET PERIODS");
        lblSetPeriods.setHorizontalAlignment(SwingConstants.CENTER);
        panel_7.add(lblSetPeriods, BorderLayout.NORTH);

        JPanel panel_8 = new JPanel();
        panel_7.add(panel_8, BorderLayout.CENTER);
        panel_8.setLayout(new GridLayout(2, 1, 0, 0));

        JPanel panel_9 = new JPanel();
        panel_8.add(panel_9);

        JLabel label_3 = new JLabel("Since:");
        panel_9.add(label_3);

        JSpinner spinner = new JSpinner();
        spinner.setModel(new SpinnerDateModel(new Date(1549580400000L), new Date(-3600000L), new Date(4102441199000L), Calendar.DAY_OF_YEAR));
        panel_9.add(spinner);

        JLabel label_4 = new JLabel("Period:");
        panel_9.add(label_4);

        JSpinner spinner_1 = new JSpinner();
        panel_9.add(spinner_1);

        JLabel label_5 = new JLabel("days");
        panel_9.add(label_5);

        JPanel panel_10 = new JPanel();
        panel_8.add(panel_10);

        JLabel lblDeleteFilesAbandoned = new JLabel("Delete files abandoned for:");
        panel_10.add(lblDeleteFilesAbandoned);

        JSpinner spinner_2 = new JSpinner();
        spinner_2.setModel(new SpinnerNumberModel(1, 1, 179, 1));
        panel_10.add(spinner_2);

        JLabel lblDaysOrLonger = new JLabel("day(s) or longer");
        panel_10.add(lblDaysOrLonger);

        JPanel panel_12 = new JPanel();
        panelMain.add(panel_12);
        panel_12.setLayout(new GridLayout(2, 0, 0, 0));

        JPanel panel_13 = new JPanel();
        panel_12.add(panel_13);

        JLabel lblLogActions = new JLabel("Log actions:");
        panel_13.add(lblLogActions);

        JCheckBox chckbxEnabled = new JCheckBox("ENABLED");
        panel_13.add(chckbxEnabled);
        chckbxEnabled.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    configurator.enableLog();
                } else {
                    configurator.disableLog();
                }
            }
        });

        JPanel panel_14 = new JPanel();
        panel_12.add(panel_14);
        panel_14.setLayout(new BorderLayout(0, 0));

        JLabel lblCleanerSwitch = new JLabel("CLEANER SWITCH:");
        lblCleanerSwitch.setHorizontalAlignment(SwingConstants.CENTER);
        panel_14.add(lblCleanerSwitch, BorderLayout.NORTH);

        JPanel panel_15 = new JPanel();
        panel_14.add(panel_15, BorderLayout.CENTER);

        JButton btnEnable = new JButton("ENABLE");
        panel_15.add(btnEnable);

        JButton btnDisable = new JButton("DISABLE");
        panel_15.add(btnDisable);
        btnDisable.setEnabled(false);

        btnEnable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rdbtnNone.isSelected()) {
                    btnDisable.doClick();
                    JOptionPane.showMessageDialog(null, "Cleaner type was not selected!", "KeepItClear: Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (rdbtnRemover.isSelected()) {
                    configurator.setCleaner(Configurator.CleanerTypes.REMOVER);
                } else if (rdbtnMoveFilesTo.isSelected()) {
                    if (!configurator.getLocalMoverFolder().equals("") && configurator.getLocalMoverFolder() != null) {
                        configurator.setCleaner(Configurator.CleanerTypes.MOVER_LOCAL, configurator.getLocalMoverFolder());
                    } else
                        return;
                } else if (rdbtnMoveFilesTo_1.isSelected()) {
                    String pass = new String(passwordField.getPassword());
                    if (configurator.getDropboxAccess() != null && !configurator.getDropboxAccess().equals("")) {
                        configurator.setCleaner(Configurator.CleanerTypes.MOVER_DROPBOX, configurator.getDropboxAccess());
                        lblAccessToken.setText(configurator.getDropboxAccess());
                    } else if (!textField.getText().equals("") && !pass.equals("")) {
                        configurator.setCleaner(Configurator.CleanerTypes.MOVER_DROPBOX, textField.getText(), pass);
                        lblAccessToken.setText(configurator.getDropboxAccess());
                    } else
                        return;
                } else if (rdbtnMoveFilesTo_2.isSelected()) {
                    if (!textField_2.getText().equals("")) {
                        configurator.setCleaner(Configurator.CleanerTypes.MOVER_GOOGLEDRIVE, textField_2.getText());
                    } else
                        return;
                }
                configurator.setSinceDate((Date) spinner.getValue());
                configurator.setPeriod((Integer) spinner_1.getValue());
                configurator.setSinceLastAccess((Integer) spinner_2.getValue());
                configurator.setToLog(chckbxEnabled.isSelected());
                if (!configurator.setTimer()) {
                    JOptionPane.showMessageDialog(null, "Something went wrong. Cleaner was not set.", "KeepItClear: Error", JOptionPane.ERROR_MESSAGE);
                    configurator.resetType();
                    return;
                } else {
                    JOptionPane.showMessageDialog(null, "Cleaner was enabled successfully!", "KeepItClear: On Duty", JOptionPane.INFORMATION_MESSAGE);
                    configurator.writeConfiguration();
                }
                btnDisable.setEnabled(true);
                btnEnable.setEnabled(false);
                setPanelEnabled(panel_11, false);
                setPanelEnabled(panel, false);
                setPanelEnabled(panel_1, false);
                setPanelEnabled(panel_5, false);
                setPanelEnabled(panel_7, false);
                setPanelEnabled(panel_13, false);
            }
        });

        btnDisable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configurator.dropTimer();
                btnEnable.setEnabled(true);
                btnDisable.setEnabled(false);
                panel_11.setEnabled(true);
                setPanelEnabled(panel_11, true);
                setPanelEnabled(panel, true);
                setPanelEnabled(panel_1, true);
                setPanelEnabled(panel_5, true);
                setPanelEnabled(panel_7, true);
                setPanelEnabled(panel_13, true);
            }
        });

        JPanel panel_16 = new JPanel();
        panelMain.add(panel_16);

        JLabel lblSaveSettings = new JLabel("Save settings: ");
        panel_16.add(lblSaveSettings);

        JButton btnSave = new JButton("SAVE");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configurator.writeConfiguration();
            }
        });
        panel_16.add(btnSave);

        if (configurator.readConfiguration()) {
            switch (configurator.currentType) {
                case NONE:
                    radios.setSelected(rdbtnNone.getModel(), true);
                    return;
                case REMOVER:
                    radios.setSelected(rdbtnRemover.getModel(), true);
                    break;
                case MOVER_LOCAL:
                    radios.setSelected(rdbtnMoveFilesTo.getModel(),true);
                    lblPath.setText(configurator.getLocalMoverFolder());
                    break;
                case MOVER_DROPBOX:
                    radios.setSelected(rdbtnMoveFilesTo_1.getModel(), true);
                    lblAccessToken.setText(configurator.getDropboxAccess());
                    break;
                case MOVER_GOOGLEDRIVE:
                    radios.setSelected(rdbtnMoveFilesTo_2.getModel(), true);
                    textField_2.setText(configurator.getGoogleDriveFolderId());
                    break;
            }
            spinner.setValue(configurator.getSinceDate());
            spinner_1.setValue(configurator.getPeriod());
            spinner_2.setValue(configurator.getSinceLastAccess());
            chckbxEnabled.setSelected(configurator.getToLog());
            btnEnable.doClick();
        }
    }

    private void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    private static void setTrayIcon(JFrame frmKeepItClear) {
        if(!SystemTray.isSupported() ) {
            return;
        }

        PopupMenu trayMenu = new PopupMenu();
        MenuItem item = new MenuItem("Exit");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayMenu.add(item);

        //URL imageURL = ClassLoader.getSystemResource(ICON_STR);

        Image icon = new ImageIcon(ICON_STR).getImage();
        TrayIcon trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.displayMessage(APPLICATION_NAME, "Application started!",
                TrayIcon.MessageType.INFO);

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frmKeepItClear.setVisible(true);
                frmKeepItClear.setExtendedState(frmKeepItClear.NORMAL);
            }
        });

        frmKeepItClear.addWindowListener(new WindowAdapter () {
            public void windowIconified(WindowEvent e) {
                frmKeepItClear.setVisible(false);
                trayIcon.displayMessage(APPLICATION_NAME, "Application is still running in tray.",
                        TrayIcon.MessageType.INFO);
            }
        });
    }

}
