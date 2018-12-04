/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Irindu Indeera
 */
public class TalkingPot {//this class is to create talking pot object
    
    String privateKey;
    int lastSoilMoistureValue;
    int lastLightIntensityValue;
    int lastTemperatureValue;
    boolean change = false;
    
    int LastresponseCode;
    
    
    public TalkingPot(String privateKey, int lastSoilMoistureValue, int lastLightIntensityValue, int lastTemperatureValue) {
        this.privateKey = privateKey;
        this.lastSoilMoistureValue = lastSoilMoistureValue;
        this.lastLightIntensityValue = lastLightIntensityValue;
        this.lastTemperatureValue = lastTemperatureValue;
        
        this.LastresponseCode = 0;
    }
    
    public void setResponseCode(int responseCodeNew) {
        this.LastresponseCode = responseCodeNew;//check for updates
         if (this.LastresponseCode != responseCodeNew) {
            this.setChange(true);
        }
       
    }
    //getters
    public int getLastLightIntensityValue() {
        return this.lastLightIntensityValue;
    }
    
    public int getLastSoilMoistureValue() {
        return this.lastSoilMoistureValue;
    }
    
    public int getLastTemperatureValue() {
        return this.lastTemperatureValue;
    }
    
    public void setLastLightIntensityValue(int lastLightIntensityValue) {
        this.lastLightIntensityValue = lastLightIntensityValue;
        if (this.lastLightIntensityValue != lastLightIntensityValue) {
            this.setChange(true);
        }
    }
    
    public void setLastSoilMoistureValue(int lastSoilMoistureValue) {
        this.lastSoilMoistureValue = lastSoilMoistureValue;
          if (this.lastSoilMoistureValue != lastSoilMoistureValue) {
            this.setChange(true);
        }
    }
    
    public void setLastTemperatureValue(int lastTemperatureValue) {
        this.lastTemperatureValue = lastTemperatureValue;
         if (this.lastTemperatureValue != lastTemperatureValue) {
            this.setChange(true);
        }
    }
    
    public boolean isChange() {
        return change;
    }
    
    public void setChange(boolean change) {
        this.change = change;
    }
    
}
