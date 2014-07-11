/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.lego.advancedcourse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author slcarlberg
 */
public class BackingProgramCommunicator {

    /**
     * The location of the executable file responsible for communicating with
     * the NXT.
     */
//    public static final String BACKING_PROC_LOC = new File("").getAbsolutePath() + "/TestApplication.exe";
    public static final String BACKING_PROC_LOC = "C:/Users/slcarlberg/Documents/Visual Studio 2013/Projects/TestApplication/bin/Debug/TestApplication.exe";

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
     * InputStream for the backing program's standard error stream.
     */
    public final PrintStream err;

    /**
     * The backing process.
     */
    private final Process proc;

    private final BufferedReader reader;
    private final BufferedWriter writer;

    public static final String EXIT = "q";

    private final Thread csWriteThread;
    private final Thread csReadThread;

    private boolean ev3Connected = false;

    /**
     * *
     * @throws IOException if the backing program cannot be executed
     */
    private BackingProgramCommunicator() {
        try {
//            ProcessBuilder builder = new ProcessBuilder(BACKING_PROC_LOC);
//            proc = builder.start();
//            in = new BufferedInputStream(proc.getInputStream());
//            out = new PrintStream(proc.getOutputStream());
//            err = new BufferedInputStream(proc.getErrorStream());
            proc = null;
            in = System.in;
            out = System.out;
            err = System.err;
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new BufferedWriter(new OutputStreamWriter(out));

            this.csWriteThread = new Thread(() -> {
                Scanner userInput = new Scanner(System.in);
                while (true) {
                    if (userInput.hasNext()) {
                        String input = userInput.nextLine();
                        sendMessage(input);
                    }
                }
            }, "Java -> C# Write Thread");

            csReadThread = new Thread(() -> {
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            continue;
                        }
                        handleCSOutput(line);
                    } catch (IOException ex) {
                        break;
                    }
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException("Could not start process", ex);
        }
    }

    private static BackingProgramCommunicator instance = null;

    public static BackingProgramCommunicator getInstance() {
        if (instance == null) {
            instance = new BackingProgramCommunicator();
        }
        return instance;
    }

    public static void main(String[] args) {
        System.out.println("Testing BPC");
        System.out.println(BACKING_PROC_LOC);
        BackingProgramCommunicator bpc = getInstance();
        bpc.csWriteThread.start();
        bpc.csReadThread.start();
    }

    public void sendMessage(String message) {
        try {
            System.out.println("Sending message " + message);
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(BackingProgramCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendCommand(int command) {
        sendCommand((double) command);
    }

    public void sendCommand(double command) {
        sendMessage(command + "");
    }

    public void sendCommands(int... commands) {
        for (int cmd : commands) {
            sendCommand(cmd);
        }
    }

    /**
     * Handles the output given by the C# backend.
     *
     * @param output the output String
     */
    private void handleCSOutput(String output) {
        switch (output) {
            case "connected":
                ev3Connected = true;
                break;
            case "disconnected":
                ev3Connected = false;
                break;
            default:
                break;
        }
        System.out.println("C# says: " + output);
    }

    public boolean isEV3Connected() {
        return ev3Connected;
    }

    /**
     * Kills the underlying process, closes the input and output streams, and
     * sets instance to null.
     */
    public static void reset() throws IOException {
        System.out.println("resetting bpc");
        instance.writer.write(EXIT);
        instance.out.close();
        instance.in.close();
        instance = new BackingProgramCommunicator();
    }

}
