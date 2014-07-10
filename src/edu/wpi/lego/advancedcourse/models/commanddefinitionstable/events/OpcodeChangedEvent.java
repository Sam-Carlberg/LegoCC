package edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events;

import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.TableUpdateEvent;
import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.UpdateEventVisitor;

/**
 * An event which represents that a command's opcode has been changed.
 *
 * @author Paul Malmsten <pmalmsten@gmail.com>
 */
public class OpcodeChangedEvent implements TableUpdateEvent {

    public final int originalOpcode;
    public final int newOpcode;

    /**
     * Creates a new OpcodeChangedEvent.
     *
     * @param originalOpcode The original opcode of the changed definition.
     * @param newOpcode The new opcode of the changed definition.
     */
    public OpcodeChangedEvent(int originalOpcode, int newOpcode) {
        this.originalOpcode = originalOpcode;
        this.newOpcode = newOpcode;
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
        result = prime * result + newOpcode;
        result = prime * result + originalOpcode;
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
        OpcodeChangedEvent other = (OpcodeChangedEvent) obj;
        if (newOpcode != other.newOpcode) {
            return false;
        }
        if (originalOpcode != other.originalOpcode) {
            return false;
        }
        return true;
    }

}
