/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.lego.advancedcourse;

import edu.wpi.lego.advancedcourse.bluetooth.ConnectionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author slcarlberg
 */
public final class BackingProgramCommunicator {

    /**
     * The location of the executable file responsible for communicating with
     * the NXT.
     */
    public static final String BACKING_PROC_LOC = "./C#/MailboxSender.exe";

    /**
     * InputStream for data coming from the backing program. Standard output
     * from the process will be piped here.
     */
    public final InputStream in;

    /**
     * OutputStream for data going to the backing program. Writes to this will
     * be piped to to the program's standard input.
     */
    public final PrintStream out;

    /**
     * The backing process.
     */
    private final Process proc;

    private final BufferedReader reader;
    private final BufferedWriter writer;

    public static final String CONNECT = "connect";
    public static final String CONNECTED = "connected";
    public static final String DISCONNECTED = "diconnected";
    public static final String SEND_SUCCESS = "command sent";
    public static final String CONNECTION_ERR = "could not connect";
    public static final String EXIT = "q";

    private final Thread csReadThread;

    private boolean ev3Connected = false;
    
    private BackingProgramCommunicator() {
        try {
            ProcessBuilder builder = new ProcessBuilder(BACKING_PROC_LOC);
            proc = builder.start();
            in = new BufferedInputStream(proc.getInputStream());
            out = new PrintStream(proc.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new BufferedWriter(new OutputStreamWriter(out));

            csReadThread = new Thread(() -> {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        handleCSOutput(line);
                    }
                } catch (IOException ex) {
                    if (!ex.getMessage().equals("Stream closed")) {
                        Logger.getLogger(BackingProgramCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            csReadThread.setDaemon(true);
        } catch (IOException ex) {
            throw new RuntimeException("Could not start process", ex);
        }
    }

    private static BackingProgramCommunicator instance = null;

    /**
     * Gets the instance.
     */
    public static BackingProgramCommunicator getInstance() {
        if (instance == null) {
            instance = new BackingProgramCommunicator();
            instance.csReadThread.start();
        }
        return instance;
    }

    /**
     * Sends the given message to the backing C# program.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        try {
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(BackingProgramCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the output given by the C# backend.
     *
     * @param output the output String
     */
    private void handleCSOutput(String output) {
        System.out.println("C#: " + output);
        switch (output) {
            case CONNECTED:
                ev3Connected = true;
                conListener.connectionEstablished();
                break;
            case CONNECTION_ERR:
                JOptionPane.showMessageDialog(null, "Could not connect to EV3", "", JOptionPane.ERROR_MESSAGE, null);
            // purposeful fallthrough
            case DISCONNECTED:
                ev3Connected = false;
                conListener.connectionLost();
                break;
            case SEND_SUCCESS:
                break;
        }
    }

    /**
     * Returns a boolean representing the C# backends connection state to the
     * EV3.
     */
    public boolean isEV3Connected() {
        return ev3Connected;
    }

    /**
     * Exits and then kills the underlying process.
     */
    public void exit() {
        sendMessage(EXIT);
        proc.destroy();
    }

    private ConnectionListener conListener;

    /**
     * Sets the connection listener.
     */
    public void setConnectionListener(ConnectionListener l) {
        conListener = l;
    }

}
