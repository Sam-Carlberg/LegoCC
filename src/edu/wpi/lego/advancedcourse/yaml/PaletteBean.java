package edu.wpi.lego.advancedcourse.yaml;

import java.util.List;

public class PaletteBean {

    private List<CommandBean> commands;

    /**
     * @return the commands
     */
    public List<CommandBean> getCommands() {
        return commands;
    }

    /**
     * @param commands the commands to set
     */
    public void setCommands(List<CommandBean> commands) {
        this.commands = commands;
    }
}
