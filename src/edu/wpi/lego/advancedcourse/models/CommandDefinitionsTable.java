package edu.wpi.lego.advancedcourse.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events.CreateCommandEvent;
import edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events.DeleteCommandEvent;
import edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events.OpcodeChangedEvent;

/**
 * Represents a listing of all commands that one may send to a Lego robot.
 *
 * @author Paul Malmsten <pmalmsten@gmail.com>
 *
 */
public class CommandDefinitionsTable extends Observable {

    /**
     * Represents an object that can visit TableUpdateEvents.
     */
    public interface UpdateEventVisitor {

        void visit(CreateCommandEvent createCommandAction);

        void visit(DeleteCommandEvent deleteCommandEvent);

        void visit(OpcodeChangedEvent opcodeChangedEvent);

    }

    /**
     * Represents an event which occurs when the command definition table is
     * updated.
     */
    public interface TableUpdateEvent {

        public void accept(UpdateEventVisitor visitor);
    }

    private final Map<Integer, CommandDefinition> m_table = new HashMap<Integer, CommandDefinition>();

    /**
     * Creates a new command definition.
     *
     * @param opcode The opcode to use.
     * @param name The name of the new command.
     */
    public void createCommand(int opcode, String name) {
        m_table.put(opcode, new CommandDefinition(name));

        setChanged();
        notifyObservers(new CreateCommandEvent(opcode, name));
    }

    /**
     * Deletes an existing command definition.
     *
     * @param opcode The opcode of the command to delete.
     */
    public void deleteCommand(int opcode) {
        m_table.remove(opcode);

        setChanged();
        notifyObservers(new DeleteCommandEvent(opcode));
    }

    /**
     * Updates the opcode of an existing command definition.
     *
     * @param originalOpcode The opcode of the command to update.
     * @param newOpcode The new opcode the indicated command should assume.
     */
    public void changeCommandOpcode(int originalOpcode, int newOpcode) {
        m_table.put(newOpcode, m_table.remove(originalOpcode));

        setChanged();
        notifyObservers(new OpcodeChangedEvent(originalOpcode, newOpcode));
    }

    /**
     * Retrieves the current state of the command definitions table.
     *
     * @return A mapping from opcode to command definition.
     */
    public Map<Integer, CommandDefinition> getTable() {
        return Collections.unmodifiableMap(m_table);
    }

}
