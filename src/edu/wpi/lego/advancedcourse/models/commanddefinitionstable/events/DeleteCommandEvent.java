package edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events;

import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.UpdateEventVisitor;
import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.TableUpdateEvent;

/**
 * An event which represents that a command was deleted from the
 * CommandDefinitionsTable.
 *
 * @author Paul Malmsten <pmalmsten@gmail.com>
 */
public class DeleteCommandEvent implements TableUpdateEvent {

    public final int opcode;

    /**
     * Creates a new DeleteCommandEvent.
     *
     * @param opcode The opcode of the event that was deleted.
     */
    public DeleteCommandEvent(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public void accept(UpdateEventVisitor visitor) {
        visitor.visit(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + opcode;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeleteCommandEvent other = (DeleteCommandEvent) obj;
        if (opcode != other.opcode) {
            return false;
        }
        return true;
    }

}
