package edu.wpi.lego.advancedcourse.models;

import java.util.Observable;

/**
 * Represents a command that may be send to a Lego Mindstorms NXT.
 *
 * @author Paul Malmsten <pmalmsten@gmail.com>
 */
public class CommandDefinition extends Observable {

    private String m_name;

    /**
     * Creates a new CommandDefinition.
     *
     * @param name The name of the command.
     */
    public CommandDefinition(String name) {
        m_name = name;
    }

    /**
     * Updates the name of this CommandDefinition and notifies all observers.
     *
     * @param name The new name of the CommandDefinition.
     */
    public void setName(String name) {
        m_name = name;
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the name of this CommandDefinition.
     *
     * @param name The String name associated with this object.
     */
    public String getName() {
        return m_name;
    }
}
