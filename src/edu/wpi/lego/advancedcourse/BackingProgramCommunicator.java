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
    public static final String BACKING_PROC_LOC = "C:\\Users\\slcarlberg\\Documents\\Visual Studio 2013\\Projects\\TestApplication\\bin\\Debug\\TestApplication.exe";

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
    public final InputStream err;

    /**
     * The backing process.
     */
    private final Process proc;

    private final BufferedReader reader;
    private final BufferedWriter writer;

    /*
     * Command line arguments for the backing executable.
     */
    /**
     * Usage:
     * <p>
     * <code>-c [commands...]
     */
    public static final String COMMAND = "-c";

    /**
     * Combine with {@link #CONNECT_ADDR -a} to connect to a robot.<p>
     * Usage:
     * <p>
     * <code>-n name -a addr
     */
    public static final String CONNECT_NAME = "-n";

    /**
     * Combine with {@link #CONNECT_NAME -n} to connect to a robot.<p>
     * Usage:
     * <p>
     * <code>-n name -a addr
     */
    public static final String CONNECT_ADDR = "-a";
    public static final String SCAN = "-s";
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
            ProcessBuilder builder = new ProcessBuilder(BACKING_PROC_LOC);
            proc = builder.start();
            in = new BufferedInputStream(proc.getInputStream());
            out = new PrintStream(proc.getOutputStream());
            err = new BufferedInputStream(proc.getErrorStream());
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new BufferedWriter(new OutputStreamWriter(out));

            this.csWriteThread = new Thread(() -> {
                Scanner userInput = new Scanner(System.in);
                while (true) {
                    if (userInput.hasNext()) {
                        try {
                            String input = userInput.nextLine();
                            sendMessage(input);
                        } catch (IOException ex) {
                            Logger.getLogger(BackingProgramCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
                        System.out.println("C# says: " + line);
                    } catch (IOException ex) {
                        break;
                    }
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(BackingProgramCommunicator.class.getName()).log(Level.SEVERE, null, ex);
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

    private void sendMessage(String input) throws IOException {
        writer.write(input + "\n");
        writer.flush();
    }
    
    public void sendCommands(int... commands) {
        for(int cmd : commands) {
            try {
                sendMessage(cmd + "");
            } catch (IOException ex) {
                Logger.getLogger(BackingProgramCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        instance.writer.write("exit");
        instance.out.close();
        instance.in.close();
        instance = null;
    }

}
