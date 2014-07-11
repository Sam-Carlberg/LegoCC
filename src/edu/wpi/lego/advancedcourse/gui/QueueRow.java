package edu.wpi.lego.advancedcourse.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.LineBorder;
import javax.swing.JFormattedTextField;

public class QueueRow extends JPanel {

    private int opcode;
    private JLabel cmdNameLabel;
    private JButton removeBtn;
    private JButton moveUpBtn;
    private JButton moveDownBtn;
    private Component horizontalGlue;
    private Component horizontalStrut;
    private JFormattedTextField operandField;

    /**
     * Create the panel.
     */
    public QueueRow(int opcode, boolean shouldShowOperandField) {
        this.opcode = opcode;

        setForeground(Color.GRAY);
        setBorder(new LineBorder(new Color(0, 0, 0)));
        setPreferredSize(new Dimension(258, 28));
        setMaximumSize(new Dimension(32767, 30));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        cmdNameLabel = new JLabel("");
        add(cmdNameLabel);

        horizontalGlue = Box.createHorizontalGlue();
        add(horizontalGlue);

        operandField = new JFormattedTextField(NumberFormat.getNumberInstance());
        operandField.setValue(0);
        operandField.setPreferredSize(new Dimension(60, 22));
        operandField.setMaximumSize(new Dimension(60, 2147483647));
        operandField.setVisible(shouldShowOperandField);
        add(operandField);

        horizontalStrut = Box.createHorizontalStrut(20);
        horizontalStrut.setPreferredSize(new Dimension(13, 0));
        horizontalStrut.setMinimumSize(new Dimension(13, 0));
        horizontalStrut.setMaximumSize(new Dimension(13, 32767));
        add(horizontalStrut);

        moveUpBtn = new JButton("\u25B2");
        add(moveUpBtn);

        moveDownBtn = new JButton("\u25BC");
        add(moveDownBtn);

        removeBtn = new JButton("Remove");
        add(removeBtn);

    }

    /**
     * Updates the name displayed by this queue row.
     *
     * @param newName The new name to display.
     */
    public void setName(String newName) {
        cmdNameLabel.setText(newName);
    }

    /**
     * @param al The ActionListener to be called when the row's remove button is
     * clicked.
     */
    public void addRemoveButtonActionListener(ActionListener al) {
        removeBtn.addActionListener(al);
    }

    /**
     * @param al The ActionListern to call when the row's move up button is
     * clicked.
     */
    public void addMoveUpButtonActionListener(ActionListener al) {
        moveUpBtn.addActionListener(al);
    }

    /**
     * @param al The ActionListener to call when the row's move down button is
     * clicked.
     */
    public void addMoveDownButtonActionListener(ActionListener al) {
        moveDownBtn.addActionListener(al);
    }

    /**
     * Returns the opcode of the command this queue row represents.
     *
     * @return The opcode of this command.
     */
    public int getOpcode() {
        return opcode;
    }

    /**
     * Updates the opcode of this QueueRow to match the given value.
     *
     * @param newOpcode The new opcode this QueueRow should assume.
     */
    public void setOpcode(int newOpcode) {
        opcode = newOpcode;
    }

    /**
     * Informs this queue row of whether it should show the operand field.
     *
     * @param shouldShowOperandField True if the operand field should be shown;
     * false otherwise
     */
    public void setShouldShowOperand(boolean shouldShowOperandField) {
        operandField.setVisible(shouldShowOperandField);
        revalidate();
    }

    /**
     * Determines the operand of this QueueRow.
     *
     * @return The operand value for this queue row.
     */
    public double getOperand() {
        return ((Number) operandField.getValue()).doubleValue();
    }
}
