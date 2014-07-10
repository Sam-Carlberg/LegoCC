package edu.wpi.lego.advancedcourse.models;

/**
 * Represents a command that has been queued for delivery.
 *
 * @author Paul Malmsten <pmalmsten@gmail.com>
 */
public class CommandInstance {

    private int m_opcode;
    private float m_operand;

    /**
     * Creates a new CommandInstance.
     *
     * @param opcode The opcode of the command.
     * @param operand The operand to send along with the command.
     */
    public CommandInstance(int opcode, float operand) {
        m_opcode = opcode;
        m_operand = operand;
    }

    /**
     * @return The opcode of this command instance.
     */
    public int getOpcode() {
        return m_opcode;
    }

    /**
     * @param opcode The new opcode to use.
     */
    public void setOpcode(int opcode) {
        m_opcode = opcode;
    }

    /**
     * @return The operand of this command instance.
     */
    public float getOperand() {
        return m_operand;
    }

    /**
     * @param operand The operand to use.
     */
    public void setOperand(float operand) {
        m_operand = operand;
    }

}
