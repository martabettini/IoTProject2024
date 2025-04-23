package it.unipr.iotlab.iot2024.cf.server.sensors;

/**
 * This class contains an attribute to store the chlorine concentration and provides methods
 * to access and modify the concentration value.
 */
public class ChlorineSensor {

    // Instance variable to store the concentration value of chlorine
    private double concentration;

    /**
     * Default constructor that initializes the chlorine concentration to 0.0.
     */
    public ChlorineSensor() {
        this.concentration = 0.0;
    }

    /**
     * Getter method to retrieve the current chlorine concentration value.
     * @return the current chlorine concentration value
     */
    public double getConcentration() {
        return concentration;
    }

    /**
     * Setter method to update the chlorine concentration value.
     * @param concentration the new chlorine concentration value
     */
    public void setConcentration(double concentration) {
        this.concentration = concentration;	
    }
}
