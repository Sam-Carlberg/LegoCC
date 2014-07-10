/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.lego.advancedcourse.models;

/**
 *
 * @author slcarlberg
 */
public class NXTInfo {
    
    private String friendlyName;
    private String addr;

    public NXTInfo(String friendlyName, String addr) {
        this.friendlyName = friendlyName;
        this.addr = addr;
    }
    
    public String getName() {
        return friendlyName;
    }
    
    public String getAddress() {
        return addr;
    }
    
}
