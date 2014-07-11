/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.lego.advancedcourse.bluetooth;

/**
 *
 * @author slcarlberg
 */
public interface StatusListener {

    public void newStatusDescriptionAvailable(String text);

    public void connectionEstablished();

    public void connectionLost();

}
