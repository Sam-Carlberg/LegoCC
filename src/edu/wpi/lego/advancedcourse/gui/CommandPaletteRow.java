package edu.wpi.lego.advancedcourse.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractSpinnerModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import edu.wpi.lego.advancedcourse.gui.factories.CommandPaletteRowFactory;

public class CommandPaletteRow extends JPanel {

    private JTextField nameField;
    private JSpinner opcodeSpinner;
    private JButton addToQueueBtn;
    private JButton deleteBtn;

    public static class ReservableIntegerSpinnerModel extends AbstractSpinnerModel {

        private Integer m_lastSetValue = null;
        private Integer m_value = 0;
        private Set<Integer> m_reservedSet = new HashSet<Integer>();

        @Override
        public Object getNextValue() {
            Integer next = m_value + 1;

            while (m_reservedSet.contains(next)) {
                next++;
            }

            return (next >= 0) ? next : null;
        }

        @Override
        public Object getPreviousValue() {
            Integer prev = m_value - 1;

            while (m_reservedSet.contains(prev)) {
                prev--;
            }

            return (prev >= 0) ? prev : null;
        }

        @Override
        public Object getValue() {
            return m_value;
        }

        @Override
        public void setValue(Object value) {
            m_lastSetValue = m_value;
            m_value = (Integer) value;
            fireStateChanged();
        }

        /**
         * Reserves the given value, such that it is skipped by this spinner.
         *
         * @param value The value to reserve.
         */
        public void reserve(int value) {
            m_reservedSet.add(Integer.valueOf(value));
        }

        /**
         * Releases the given reserved value, such that this spinner may assume
         * it.
         *
         * @param value The previously reserved value to release.
         */
        public void release(int value) {
            m_reservedSet.remove(Integer.valueOf(value));
        }

        /**
         * Returns the value that this model assumed just before the most recent
         * call to setValue().
         *
         * @return The value that this model assumed just before the most recent
         * call to setValue();
         */
        public Object getLastSetValue() {
            return m_lastSetValue;
        }
    }

    private ReservableIntegerSpinnerModel reservableIntegerModel;

    /**
     * Creates a new CommandPaletteRow.
     *
     * @param isEditable True if this CommandPaletteRow may be edited by a user;
     * false otherwise.
     */
    public CommandPaletteRow(boolean isEditable) {
        reservableIntegerModel = new ReservableIntegerSpinnerModel();

        setBorder(new LineBorder(Color.GRAY));
        setMaximumSize(new Dimension(32767, 30));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        opcodeSpinner = new JSpinner();
        ((JSpinner.DefaultEditor) opcodeSpinner.getEditor()).getTextField().setDisabledTextColor(Color.BLACK);
        opcodeSpinner.setPreferredSize(new Dimension(40, 22));
        opcodeSpinner.setMinimumSize(new Dimension(50, 22));
        opcodeSpinner.setModel(reservableIntegerModel);
        opcodeSpinner.setEnabled(isEditable);
        add(opcodeSpinner);

        nameField = CommandPaletteRowFactory.createNameField();
        nameField.setDisabledTextColor(Color.DARK_GRAY);
        nameField.setText("");
        nameField.setColumns(10);
        nameField.setEnabled(isEditable);
        add(nameField);

        Component horizontalStrut = Box.createHorizontalStrut(20);
        add(horizontalStrut);

        addToQueueBtn = CommandPaletteRowFactory.createAddToQueueButton();
        addToQueueBtn.setText("\u25B6");
        add(addToQueueBtn);

        deleteBtn = CommandPaletteRowFactory.createRemoveButton();
        deleteBtn.setVisible(isEditable);
        add(deleteBtn);

    }

    /**
     * Returns the friendly name of this command.
     *
     * @return The friendly name of this command.
     */
    public String getCommandName() {
        return nameField.getText();
    }

    /**
     * Sets the friendly name of this command.
     *
     * @param newName The new friendly name to display.
     */
    public void setCommandName(String newName) {
        nameField.setText(newName);
    }

    public int getOpcode() {
        return ((Integer) opcodeSpinner.getModel().getValue()).intValue();
    }

    public void setOpcode(int newOpcode) {
        opcodeSpinner.getModel().setValue(newOpcode);
    }

    /**
     * @return The opcode this command definition assumed just before the most
     * recent update to its value.
     */
    public int getLastUsedOpcode() {
        return (Integer) reservableIntegerModel.getLastSetValue();
    }

    /**
     * @param al The ActionListener to call when this command is deleted.
     */
    public void addDeleteListener(ActionListener al) {
        deleteBtn.addActionListener(al);
    }

    /**
     * @param al The ChangeListener to call when this command's opcode is
     * changed.
     */
    public void addOpcodeChangedListener(ChangeListener cl) {
        opcodeSpinner.addChangeListener(cl);
    }

    /**
     * @param al The ActionListener to call when the add to queue button is
     * clicked.
     */
    public void addAddToQueueListener(ActionListener al) {
        addToQueueBtn.addActionListener(al);
    }

    /**
     * @param dl The DocumentListener to inform of changes made to the document
     * that this row's name field represents.
     */
    public void addNameChangedListener(DocumentListener dl) {
        nameField.getDocument().addDocumentListener(dl);
    }

    public void reserveOpcode(int opcode) {
        reservableIntegerModel.reserve(opcode);
    }

    public void releaseOpcode(int opcode) {
        reservableIntegerModel.release(opcode);
    }

    /**
     * Sets whether the input fields of this command palette row may accept
     * input.
     *
     * @param isEditable True if this command palette row may accept input;
     * false otherwise.
     */
    public void setEditable(final boolean isEditable) {
        nameField.setEnabled(isEditable);
        opcodeSpinner.setEnabled(isEditable);
        deleteBtn.setVisible(isEditable);
        revalidate();
        repaint();
    }
}
