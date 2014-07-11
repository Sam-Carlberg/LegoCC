/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.lego.advancedcourse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class responsible for sending commands to the EV3 brick.
 *
 * @author slcarlberg
 */
public class CommandSender implements Runnable {

    private final List<Number> commandQueue = new ArrayList<>();
    private boolean fullQueue = false;

    public CommandSender() {
        new Thread(this, "CommandSenderThread").start();
    }

    /**
     * Queues the given command. This can be either an opcode or operand.
     *
     * @param command
     */
    public void queue(Number command) {
        synchronized (commandQueue) {
            commandQueue.add(command);
            commandQueue.notifyAll();
        }
    }

    /**
     * Notifies this that all commands have been queued and it is now safe to
     * send them to the robot.
     */
    public void flagEndOfAddition() {
        fullQueue = true;
    }

    @Override
    public void run() {
        BackingProgramCommunicator bpc = BackingProgramCommunicator.getInstance();
        while (true) {
            synchronized (commandQueue) {
                if (commandQueue.isEmpty()) {
                    try {
                        commandQueue.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
            if (fullQueue) {
                String message = "";
                for (Number op : commandQueue) {
                    if (op instanceof Integer) {
                        message += " " + op;
                    } else {
                        message += ":" + op;
                    }
                }
                bpc.sendMessage(message);
                commandQueue.clear();
                fullQueue = false;
            }
        }
    }

}
