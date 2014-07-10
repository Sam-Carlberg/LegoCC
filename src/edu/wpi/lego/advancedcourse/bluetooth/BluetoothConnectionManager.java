/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.lego.advancedcourse.bluetooth;

import edu.wpi.lego.advancedcourse.models.NXTInfo;
import java.util.List;

/**
 *
 * @author slcarlberg
 */
public class BluetoothConnectionManager {

    public interface StatusListener {

        public void newStatusDescriptionAvailable(String text);

        public void connectionEstablished();

        public void connectionLost();

    }

    public interface ScanListener {

        public void scanInterrupted(Throwable t);

        public void scanComplete(final List<NXTInfo> devices);

        public void scanStarted();

    }

    public void scan(ScanListener listener) {
        listener.scanStarted();
    }

    public void connect(String name, String address) {
        
    }

    public void setStatusListener(StatusListener listener) {
        
    }

    public void queueMessage(float opcode) {
        
    }

    public boolean isConnected() {
        return false;
    }

    public boolean isConnecting() {
        return false;
    }

    public void disconnect() {
        
    }

}
