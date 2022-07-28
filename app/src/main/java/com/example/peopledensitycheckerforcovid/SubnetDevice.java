package com.example.peopledensitycheckerforcovid;

import com.stealthcopter.networktools.subnet.Device;

public class SubnetDevice {
   private Device device;
   public SubnetDevice(Device device){this.device = device;}
   public String getIPadd(){return device.ip;}
   public String getHostname(){return device.hostname;}
   public String getMAC(){return device.mac;}
}
