package edu.wpi.lego.advancedcourse.gui.factories;

import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;

public final class CommandPaletteRowFactory {

    /**
     * @wbp.factory
     */
    public static JTextField createNameField() {
        JTextField textField = new JTextField();
        textField.setText("Forward");
        textField.setColumns(10);
        return textField;
    }

    /**
     * @wbp.factory
     */
    public static JFormattedTextField createOpcodeField() {
        JFormattedTextField formattedTextField = new JFormattedTextField();
        formattedTextField.setText("0");
        return formattedTextField;
    }

    /**
     * @wbp.factory
     */
    public static JButton createAddToQueueButton() {
        JButton button = new JButton("->");
        return button;
    }

    /**
     * @wbp.factory
     */
    public static JButton createRemoveButton() {
        JButton button = new JButton("Delete");
        return button;
    }
}
