package it.unipr.iotlab.iot2024.cf.server.sensors;

/**
 * This class contains an attribute to store the temperature and provides methods
 * to access and modify the temperature value.
 */
public class ImmersionTempSensor {

    // Instance variable to store the temperature value
    private double temperature;

    /**
     * Default constructor that initializes the temperature to 0.0 degrees.
     */
    public ImmersionTempSensor() {
        this.temperature = 0.0;
    }

    /**
     * Getter method to retrieve the current temperature value.
     * @return the current temperature value
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Setter method to update the temperature value.
     * @param temperature the new temperature value
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;	
    }
}
