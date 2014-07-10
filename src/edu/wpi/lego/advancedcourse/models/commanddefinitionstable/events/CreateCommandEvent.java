package edu.wpi.lego.advancedcourse.models.commanddefinitionstable.events;

import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.UpdateEventVisitor;
import edu.wpi.lego.advancedcourse.models.CommandDefinitionsTable.TableUpdateEvent;

/**
 * Represents a message that a new command definition was created.
 */
public class CreateCommandEvent implements TableUpdateEvent {

    public final Integer opcode;
    public final String name;

    /**
     * Creates a new CreateCommandAction.
     *
     * @param opcode The integer opcode representing the new command.
     * @param name The name of the new command.
     */
    public CreateCommandEvent(Integer opcode, String name) {
        this.opcode = opcode;
        this.name = name;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((opcode == null) ? 0 : opcode.hashCode());
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
        CreateCommandEvent other = (CreateCommandEvent) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (opcode == null) {
            if (other.opcode != null) {
                return false;
            }
        } else if (!opcode.equals(other.opcode)) {
            return false;
        }
        return true;
    }

}
