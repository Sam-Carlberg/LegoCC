package edu.wpi.lego.advancedcourse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.yaml.snakeyaml.Yaml;

import edu.wpi.lego.advancedcourse.bluetooth.ConnectionListener;
import edu.wpi.lego.advancedcourse.gui.CommandPaletteRow;
import edu.wpi.lego.advancedcourse.gui.QueueRow;
import edu.wpi.lego.advancedcourse.models.CommandDefinition;
import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable;
import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.TableUpdateEvent;
import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.UpdateEventVisitor;
import edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events.CreateCommandEvent;
import edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events.DeleteCommandEvent;
import edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events.OpcodeChangedEvent;
import edu.wpi.lego.advancedcourse.yaml.CommandBean;
import edu.wpi.lego.advancedcourse.yaml.PaletteBean;
import java.awt.event.MouseAdapter;

public class CommandAndControl {

    private static final String PREF_SAVE_FILE_PATH = "saveFilePath";
    final Preferences prefs = Preferences.userNodeForPackage(getClass());

    private JFrame frmLegoCommandandcontrol;
    private JPanel QueuePanel;
    private final ButtonGroup modeGroup = new ButtonGroup();
    private final CommandDefinitionsTable definitionsTable = new CommandDefinitionsTable();
    private SendEnableManager enableManager;
    private CountdownTimer sendTimer;
    private BackingProgramCommunicator bpc;

    private static final Logger root = Logger.getLogger("edu.wpi.lego.advancedcourse");

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            root.addHandler(new FileHandler("lego_CnC_errors.log", true));
        } catch (SecurityException | IOException ex) {
            root.log(Level.SEVERE, "Unable to open log file", ex);
        }
        EventQueue.invokeLater(() -> {
            try {
                CommandAndControl window = new CommandAndControl();
                window.frmLegoCommandandcontrol.setVisible(true);
            } catch (Exception e) {
                root.log(Level.SEVERE, "Exception encountered during GUI initialization", e);
            }
        });
    }

    /**
     * Create the application.
     */
    public CommandAndControl() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        bpc = BackingProgramCommunicator.getInstance();
        frmLegoCommandandcontrol = new JFrame();
        frmLegoCommandandcontrol.setTitle("Lego CommandAndControl v0.5");
        frmLegoCommandandcontrol.setBounds(100, 100, 836, 447);
        frmLegoCommandandcontrol.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frmLegoCommandandcontrol.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenuItem mntmSaveAs = new JMenuItem("Save As");

        mnFile.add(mntmSaveAs);

        JMenuItem mntmLoad = new JMenuItem("Load");

        mnFile.add(mntmLoad);

        JMenu mnMode = new JMenu("Mode");
        menuBar.add(mnMode);

        JRadioButtonMenuItem modeMenuSimpleItem = new JRadioButtonMenuItem("Simple");
        modeGroup.add(modeMenuSimpleItem);
        mnMode.add(modeMenuSimpleItem);

        final JRadioButtonMenuItem modeMenuAdvancedItem = new JRadioButtonMenuItem("Advanced");
        modeMenuAdvancedItem.setSelected(true);
        modeGroup.add(modeMenuAdvancedItem);
        mnMode.add(modeMenuAdvancedItem);

        frmLegoCommandandcontrol.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel centerPanel = new JPanel();
        frmLegoCommandandcontrol.getContentPane().add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

        JPanel commandPaletteContainer = new JPanel();
        commandPaletteContainer.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        centerPanel.add(commandPaletteContainer);
        commandPaletteContainer.setLayout(new BoxLayout(commandPaletteContainer, BoxLayout.Y_AXIS));

        JPanel commandPaletteTitlePanel = new JPanel();
        commandPaletteContainer.add(commandPaletteTitlePanel);
        commandPaletteTitlePanel.setLayout(new BoxLayout(commandPaletteTitlePanel, BoxLayout.X_AXIS));

        JLabel lblCommandPalettename = new JLabel("Command Palette (ID, Name):");
        commandPaletteTitlePanel.add(lblCommandPalettename);

        Component horizontalGlue = Box.createHorizontalGlue();
        commandPaletteTitlePanel.add(horizontalGlue);

        JScrollPane commandPaletteScrollPane = new JScrollPane();
        commandPaletteScrollPane.setBorder(null);
        commandPaletteContainer.add(commandPaletteScrollPane);

        final JPanel commandPalettePanel = new JPanel();
        commandPalettePanel.setBorder(null);
        commandPaletteScrollPane.setViewportView(commandPalettePanel);
        commandPalettePanel.setLayout(new BoxLayout(commandPalettePanel, BoxLayout.Y_AXIS));

        final JPanel createCommandPanel = new JPanel();
        commandPaletteContainer.add(createCommandPanel);
        createCommandPanel.setLayout(new BoxLayout(createCommandPanel, BoxLayout.X_AXIS));

        JButton btnCreateNewCommand = new JButton("Create New Command");
        btnCreateNewCommand.addActionListener((ActionEvent e) -> {
            int i = 0;
            while (definitionsTable.getTable().containsKey(i)) {
                i++;
            }
            definitionsTable.createCommand(i, "");
        });
        createCommandPanel.setVisible(true);
        createCommandPanel.add(btnCreateNewCommand);

        Component horizontalGlue_3 = Box.createHorizontalGlue();
        createCommandPanel.add(horizontalGlue_3);

        JPanel QueueContainer = new JPanel();
        QueueContainer.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        centerPanel.add(QueueContainer);
        QueueContainer.setLayout(new BoxLayout(QueueContainer, BoxLayout.Y_AXIS));

        JPanel QueueTitlePanel = new JPanel();
        QueueContainer.add(QueueTitlePanel);
        QueueTitlePanel.setLayout(new BoxLayout(QueueTitlePanel, BoxLayout.X_AXIS));

        JLabel lblCommandQueueopcode = new JLabel("Command Queue (Operand):");
        QueueTitlePanel.add(lblCommandQueueopcode);

        Component horizontalGlue_1 = Box.createHorizontalGlue();
        QueueTitlePanel.add(horizontalGlue_1);

        JScrollPane QueueScrollPane = new JScrollPane();
        QueueScrollPane.setBorder(null);
        QueueContainer.add(QueueScrollPane);

        QueuePanel = new JPanel();
        QueueScrollPane.setViewportView(QueuePanel);
        QueuePanel.setLayout(new BoxLayout(QueuePanel, BoxLayout.Y_AXIS));

        JPanel statusPanel = new JPanel();
        frmLegoCommandandcontrol.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        final JLabel statusLabel = new JLabel("Disconnected.");
        statusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (!bpc.isEV3Connected()) {
                        bpc.sendMessage(BackingProgramCommunicator.CONNECT);
                    }
                }
            }
        });
        statusPanel.add(statusLabel);

        Component horizontalGlue_2 = Box.createHorizontalGlue();
        statusPanel.add(horizontalGlue_2);

        JPanel sendPane = new JPanel();
        statusPanel.add(sendPane);
        sendPane.setLayout(new BoxLayout(sendPane, BoxLayout.X_AXIS));

        final JLabel timePrefixLabel = new JLabel("Time Remaining:");
        timePrefixLabel.setVisible(false);
        sendPane.add(timePrefixLabel);

        Component horizontalStrut = Box.createHorizontalStrut(20);
        sendPane.add(horizontalStrut);
        horizontalStrut.setPreferredSize(new Dimension(5, 0));

        final JLabel timeLabel = new JLabel("0:00");
        timeLabel.setVisible(false);
        sendPane.add(timeLabel);

        Component horizontalStrut_1 = Box.createHorizontalStrut(20);
        sendPane.add(horizontalStrut_1);
        horizontalStrut_1.setPreferredSize(new Dimension(5, 0));

        final JButton sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendPane.add(sendButton);

        // Handle switching modes
        modeMenuSimpleItem.addActionListener(e -> {
            QueuePanel.removeAll();

            new HashSet<>(definitionsTable.getTable().keySet()).stream().forEach((i) -> {
                definitionsTable.deleteCommand(i);
            });
            commandPalettePanel.removeAll();

            // Hide 'create command' button
            createCommandPanel.setVisible(false);

            addSimpleCommands();
        });

        modeMenuAdvancedItem.addActionListener(e -> {
            for (Component c : commandPalettePanel.getComponents()) {
                CommandPaletteRow cpr = (CommandPaletteRow) c;
                cpr.setEditable(true);
            }

            // Allow user to edit all visible queue rows
            for (Component c : QueuePanel.getComponents()) {
                QueueRow qr = (QueueRow) c;
                qr.setShouldShowOperand(true);
            }

            // Enable create new command button
            createCommandPanel.setVisible(true);
        });

        // Handle saving and loading
        mntmSaveAs.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(prefs.get(PREF_SAVE_FILE_PATH, null));
            int retval = chooser.showSaveDialog(frmLegoCommandandcontrol);
            if (retval == JFileChooser.APPROVE_OPTION) {

                String filePath = chooser.getSelectedFile().getAbsolutePath();

                try {
                    saveCommandPalette(filePath);

                    prefs.put(PREF_SAVE_FILE_PATH, filePath);
                } catch (IOException ex) {
                    root.log(Level.SEVERE, "IO error occurred during save", ex);
                }
            }
        });

        mntmLoad.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(prefs.get(PREF_SAVE_FILE_PATH, null));
            int retval = chooser.showOpenDialog(frmLegoCommandandcontrol);
            if (retval == JFileChooser.APPROVE_OPTION) {

                String filePath = chooser.getSelectedFile().getAbsolutePath();

                try {
                    loadCommandPalette(commandPalettePanel, filePath);
                    prefs.put(PREF_SAVE_FILE_PATH, filePath);
                } catch (IOException ex) {
                    root.log(Level.SEVERE, "Unable to read file", ex);
                    JOptionPane.showMessageDialog(frmLegoCommandandcontrol,
                            "An error occurred while attempting to read the selected file: " + ex.getMessage(),
                            "Unable to Read File",
                            JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        definitionsTable.addObserver((Observable o, Object tue) -> {
            ((TableUpdateEvent) tue).accept(new UpdateEventVisitor() {

                @Override
                public void visit(final OpcodeChangedEvent opcodeChangedEvent) {
                    // Update available opcode choices in palette
                    for (Component c : commandPalettePanel.getComponents()) {
                        ((CommandPaletteRow) c).releaseOpcode(opcodeChangedEvent.originalOpcode);
                        ((CommandPaletteRow) c).reserveOpcode(opcodeChangedEvent.newOpcode);
                    }

                    // Update any affected items pending for submission
                    for (Component c : QueuePanel.getComponents()) {
                        QueueRow qr = (QueueRow) c;

                        if (qr.getOpcode() == opcodeChangedEvent.originalOpcode) {
                            qr.setOpcode(opcodeChangedEvent.newOpcode);
                        }
                    }
                }

                @Override
                public void visit(final DeleteCommandEvent deleteCommandEvent) {
                    // Remove all instances of the deleted command
                    ListMutator<Component> removeAssociatedInstances = (List<Component> list) -> {
                        Iterator<Component> i = list.iterator();

                        while (i.hasNext()) {
                            QueueRow qr = (QueueRow) i.next();

                            // If the queued command matches this row's current opcode,
                            // remove it
                            if (qr.getOpcode() == deleteCommandEvent.opcode) {
                                i.remove();
                            }
                        }
                    };

                    // Apply remove operation
                    modifyQueue(removeAssociatedInstances);

                    // Add removed opcode back to all other row's choices
                    for (Component c : commandPalettePanel.getComponents()) {
                        ((CommandPaletteRow) c).releaseOpcode(deleteCommandEvent.opcode);
                    }
                }

                @Override
                public void visit(final CreateCommandEvent createCommandAction) {
                    final CommandDefinition newDefinition = definitionsTable.getTable().get(createCommandAction.opcode);
                    EventQueue.invokeLater(() -> {
                        for (Component c : commandPalettePanel.getComponents()) {
                            ((CommandPaletteRow) c).reserveOpcode(createCommandAction.opcode);
                        }

                        // Create new row
                        final CommandPaletteRow newRow = new CommandPaletteRow(modeMenuAdvancedItem.isSelected());
                        newRow.setOpcode(createCommandAction.opcode);
                        newRow.setCommandName(createCommandAction.name);

                        // Remove all existing opcodes from the new row's choices
                        for (Component c : commandPalettePanel.getComponents()) {
                            newRow.reserveOpcode(((CommandPaletteRow) c).getOpcode());
                        }

                        // Handle removing a command definition
                        newRow.addDeleteListener((ActionEvent e) -> {
                            definitionsTable.deleteCommand(newRow.getOpcode());
                            commandPalettePanel.remove(newRow);
                            commandPalettePanel.revalidate();
                            commandPalettePanel.repaint();
                        });

                        // Handle changing a command's name
                        newRow.addNameChangedListener(new DocumentListener() {

                            @Override
                            public void removeUpdate(DocumentEvent arg0) {
                                // Update name
                                updateCommandName(newDefinition, arg0.getDocument());
                            }

                            @Override
                            public void insertUpdate(DocumentEvent arg0) {
                                // Update name
                                updateCommandName(newDefinition, arg0.getDocument());
                            }

                            @Override
                            public void changedUpdate(DocumentEvent arg0) {
                                // Ignored.
                            }

                            private void updateCommandName(CommandDefinition cd, Document d) {
                                try {
                                    cd.setName(d.getText(0, d.getLength()));
                                } catch (BadLocationException e) {
                                    root.log(Level.SEVERE, "Unexpected exception", e);
                                }
                            }
                        });

                        // Handle changing of opcode values
                        newRow.addOpcodeChangedListener((ChangeEvent e) -> {
                            int originalOpcode = newRow.getLastUsedOpcode();
                            int newOpcode = newRow.getOpcode();
                            definitionsTable.changeCommandOpcode(originalOpcode, newOpcode);
                        });

                        // Handle adding command to queue
                        newRow.addAddToQueueListener((ActionEvent e) -> {
                            final QueueRow qrow = new QueueRow(newRow.getOpcode(), modeMenuAdvancedItem.isSelected());
                            qrow.setName(newRow.getCommandName());

                            // Register row for command definition name change events
                            newDefinition.addObserver((Observable arg0, Object arg1) -> qrow.setName(((CommandDefinition) arg0).getName()));

                            // Handle moving this row down
                            qrow.addMoveDownButtonActionListener((ActionEvent e1) -> {
                                ListMutator<Component> moveItemDown = (List<Component> list) -> {
                                    int pos = list.indexOf(qrow);

                                    // Move it down
                                    if (pos < list.size() - 1) {
                                        list.remove(pos);
                                        list.add(pos + 1, qrow);
                                    }
                                };

                                // Apply to the queue
                                modifyQueue(moveItemDown);
                            });

                            // Handle moving this row up
                            qrow.addMoveUpButtonActionListener((ActionEvent e1) -> {
                                ListMutator<Component> moveItemUp = (List<Component> list) -> {
                                    int pos = list.indexOf(qrow);

                                    // Move it up
                                    if (pos > 0) {
                                        list.remove(pos);
                                        list.add(pos - 1, qrow);
                                    }
                                };

                                // Apply to the queue
                                modifyQueue(moveItemUp);
                            });

                            // Handle removing this row
                            qrow.addRemoveButtonActionListener((ActionEvent arg0) -> {
                                QueuePanel.remove(qrow);
                                QueuePanel.revalidate();
                                QueuePanel.repaint();
                            });

                            QueuePanel.add(qrow);
                            QueuePanel.revalidate();
                        });

                        commandPalettePanel.add(newRow);
                        commandPalettePanel.revalidate();
                    });
                }
            });
        });

        // Auto-restore save file
        String saveFilePath = prefs.get(PREF_SAVE_FILE_PATH, null);
        if (saveFilePath != null) {
            try {
                loadCommandPalette(commandPalettePanel, saveFilePath);
            } catch (IOException e1) {
                root.log(Level.WARNING, "Unable to auto-restore save file", e1);
                addSimpleCommands();
            }
        } else {
            addSimpleCommands();
        }

        enableManager = new SendEnableManager(isAllowed -> sendButton.setEnabled(isAllowed));

        sendTimer = new CountdownTimer(20, new CountdownTimer.CountdownHandler() {

            @Override
            public void onStart() {
                enableManager.updateCountdownStatus(true);
                // Show countdown timer
                timePrefixLabel.setVisible(true);
                timeLabel.setVisible(true);
            }

            @Override
            public void onTick(int remainingSeconds) {
                int minutes = remainingSeconds / 60;
                int secondsRemainder = remainingSeconds % 60;
                timeLabel.setText(String.format("%d:%02d", minutes, secondsRemainder));
            }

            @Override
            public void onTimeout() {
                // Hide countdown timer
                timePrefixLabel.setVisible(false);
                timeLabel.setVisible(false);

                enableManager.updateCountdownStatus(false);
            }
        });

        bpc.setConnectionListener(new ConnectionListener() {
            @Override
            public void newStatusDescriptionAvailable(String text) {
                statusLabel.setText(text);
            }

            @Override
            public void connectionEstablished() {
                enableManager.updateConnectionStatus(true);
            }

            @Override
            public void connectionLost() {
                enableManager.updateConnectionStatus(false);
            }
        });

        // Handle sending messages
        sendButton.addActionListener(e -> {
            for (Component c : QueuePanel.getComponents()) {
                QueueRow qrow = (QueueRow) c;
                String cmd = qrow.getOpcode() + "";
                if (modeMenuAdvancedItem.isSelected()) {
                    cmd += ":" + qrow.getOperand();
                }
                bpc.sendMessage(cmd);
            }
            sendTimer.resetAndStart();
        });

        // Add auto-save on close
        frmLegoCommandandcontrol.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                // Attempt auto-save
                String saveFilePath = prefs.get(PREF_SAVE_FILE_PATH, null);

                // If no save path exists, pop up message
                if (saveFilePath == null) {
                    int n = JOptionPane.showConfirmDialog(
                            frmLegoCommandandcontrol,
                            "Would you like to save your command palette?",
                            "Save Command Palette",
                            JOptionPane.YES_NO_OPTION);

                    // They would like to save
                    if (n == JOptionPane.YES_OPTION) {
                        JFileChooser chooser = new JFileChooser();
                        int retCode = chooser.showSaveDialog(frmLegoCommandandcontrol);

                        // They approved a path, use it
                        if (retCode == JFileChooser.APPROVE_OPTION) {
                            String filePath = chooser.getSelectedFile().getAbsolutePath();

                            try {
                                saveCommandPalette(filePath);
                                prefs.put(PREF_SAVE_FILE_PATH, filePath);
                            } catch (IOException ex) {
                                root.log(Level.WARNING, "Unable to auto-save command palette", ex);
                            }
                        }

                    }
                }

                // Save
                if (saveFilePath != null) {
                    try {
                        saveCommandPalette(saveFilePath);
                    } catch (IOException e1) {
                        root.log(Level.WARNING, "Unable to auto-save command palette", e1);
                    }
                }

                // Close window
                frmLegoCommandandcontrol.dispose();
                bpc.exit();
            }
        });
    }

    /**
     * Creates an initial set of commands for simple mode
     */
    private void addSimpleCommands() {
        definitionsTable.createCommand(0, "Forward");
        definitionsTable.createCommand(1, "Backward");
        definitionsTable.createCommand(2, "Left");
        definitionsTable.createCommand(3, "Right");
    }

    /**
     * Represents an object that can mutate a list.
     *
     * @author Paul Malmsten <pmalmsten@gmail.com>
     *
     * @param <T> The type that the mutated list may contain.
     */
    public interface ListMutator<T> {

        public void mutate(List<T> list);
    }

    /**
     * Applies the given mutator to update the rows of the queue panel.
     *
     * @param mutator The mutator to apply to the rows of the queue panel.
     * Objects may be cast to QueueRow.
     */
    private void modifyQueue(ListMutator<Component> mutator) {
        // Save and remove all rows
        List<Component> rows = new ArrayList<>(Arrays.asList(QueuePanel.getComponents()));
        QueuePanel.removeAll();

        // Update list
        mutator.mutate(rows);

        rows.forEach((c) -> QueuePanel.add(c));

        // Redraw
        QueuePanel.revalidate();
        QueuePanel.repaint();
    }

    /**
     * Saves the state of the command palette to disk.
     *
     * @param destinationFilePath The path at which to save command definitions.
     * @throws IOException Thrown when an unrecoverable I/O error occurs during
     * the attempt to save.
     */
    private void saveCommandPalette(String destinationFilePath)
            throws IOException {
        Yaml yaml = new Yaml();

        // Create bean for saving
        PaletteBean pb = new PaletteBean();
        pb.setCommands(new ArrayList<>());
        definitionsTable.getTable().entrySet().stream().map((entry) -> {
            CommandBean cb = new CommandBean();
            cb.setOpcode(entry.getKey());
            cb.setName(entry.getValue().getName());
            return cb;
        }).forEach((cb) -> {
            pb.getCommands().add(cb);
        });
        // Save bean (open file, do not append)
        try (BufferedWriter out = new BufferedWriter(new FileWriter(destinationFilePath, false))) {
            out.write(yaml.dump(pb));
        }
    }

    /**
     * Restores the command palette from the given YAML file. The YAML file must
     * have been generated by saveCommandPalette() to ensure proper parsing.
     *
     * @param CommandPalettePanel The panel which represents all definitions
     * (only cleared).
     * @param filePath The absolute path to the file to load.
     * @throws IOException Thrown when an unrecoverable error occurs when
     * attempting to load a file.
     */
    private void loadCommandPalette(final JPanel CommandPalettePanel, String filePath) throws IOException {
        Yaml y = new Yaml();

        BufferedReader br = new BufferedReader(new FileReader(filePath));

        PaletteBean pb = y.loadAs(br, PaletteBean.class);

        new HashSet<>(definitionsTable.getTable().entrySet()).forEach(e -> definitionsTable.deleteCommand(e.getKey()));
        CommandPalettePanel.removeAll();

        pb.getCommands().forEach(cb -> definitionsTable.createCommand(cb.getOpcode(), cb.getName()));
    }
}
