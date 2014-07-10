/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.lego.advancedcourse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class responsible for sending commands to the EV3 brick.
 *
 * @author slcarlberg
 */
public class CommandSender implements Runnable {

    private final Queue<Float> messageQueue = new ConcurrentLinkedQueue<>();

    public void queueMessage(float numberMessage) {
        synchronized (messageQueue) {
            messageQueue.add(numberMessage);
            messageQueue.notifyAll();
        }
    }
    
    public void queueMessage(float... messages) {
        synchronized(messageQueue) {
            for(float f : messages) messageQueue.add(f);
        }
    }

    @Override
    public void run() {
        BackingProgramCommunicator bpc = BackingProgramCommunicator.getInstance();
        while (true) {
            synchronized (messageQueue) {
                if (messageQueue.isEmpty()) {
                    try {
                        messageQueue.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
            Float nextValue = messageQueue.peek();
            if (nextValue != null) {
                
            }
        }
    }

}
