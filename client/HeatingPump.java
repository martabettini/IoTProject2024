package it.unipr.iotlab.iot2024.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;

import com.google.gson.Gson;

import it.unipr.iotlab.iot2024.cf.server.sensors.ImmersionTempSensor;


/**
 * The HeatingPump class is a CoAP client that interacts with an ImmersionTempResource
 * to monitor and adjust the temperature of a heating system. It observes the temperature 
 * resource, checks if the temperature falls below a specified threshold, and if so, 
 * it sends POST requests to increase the temperature to an ideal value.
 */
public class HeatingPump {

    // Base CoAP endpoint URL
    private static final String COAP_ENDPOINT = "coap://127.0.0.1:";
    // Resource path for the ImmersionTempResource
    private static final String RESOURCE_NAME = "/ImmersionTempResource";
    // Temperature threshold below which the heating pump activates
    private static int threshold = 19;
    // Ideal temperature to which the system aims to raise the temperature
    private static int idealTemp = 25;

    /**
     * Main method to start the CoAP client, observe temperature changes, and adjust 
     * the heating system based on the received temperature.
     *
     * @param args command-line arguments
     * @param port the port number on which the CoAP server is running
     */
    public static void main(String[] args, int port) {
        String endpoint = COAP_ENDPOINT + port + RESOURCE_NAME;
        CoapClient heatingPump = new CoapClient(endpoint);
        Gson gson = new Gson();
        
        // Create an instance of ImmersionTempSensor to manage temperature data
        ImmersionTempSensor immersionTempSensor = new ImmersionTempSensor();
    
        // Set up an observer for the ImmersionTempResource
        heatingPump.observe(new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
                // Handle the response from the CoAP server
                String tmpPayload = new String(response.getResponseText());
                int port = response.advanced().getSourcePort();
                System.out.println("Received Temperature: " + tmpPayload + " from "+ port + "\n");
                
                // Convert the JSON response to an ImmersionTempSensor object
                ImmersionTempSensor tmp = gson.fromJson(tmpPayload, ImmersionTempSensor.class);
                immersionTempSensor.setTemperature(tmp.getTemperature());
                System.out.println("Received Observable Temperature: " + immersionTempSensor.getTemperature() + "\n");
                
                // Check if the received temperature is below the threshold
                if (immersionTempSensor.getTemperature() < threshold) {
                    System.out.println("Received Temperature is below threshold.\n");
                    
                    // Increment the temperature until it reaches the ideal temperature
                    for (int i = (int) immersionTempSensor.getTemperature(); i < idealTemp; i++) {
                        immersionTempSensor.setTemperature(i);
                        
                        // Create a POST request to update the temperature
                        Request request = new Request(CoAP.Code.POST);
                        request.setConfirmable(true);
                        String payload = gson.toJson(immersionTempSensor);
                        request.setPayload(payload.getBytes());
                        
                        try {
                            // Send the POST request
                            heatingPump.advanced(request);
                            System.out.println("Posted new Temperature: " + immersionTempSensor.getTemperature() + " on "+ port + "\n");
                         
                        } catch (Exception e) {
                            System.out.println("ERROR in sending POST.\n");
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onError() {
                // Handle errors that occur during observation
                System.err.println("Error occurred while observing the resource.");
            }
        });
        
        // Keep the client running to continue observing the resource
        try {
            Thread.sleep(300000); // Observe for 5 minutes
        } catch (InterruptedException e) {
            e.printStackTrace(); // Handle the interruption exception
        }
    }
}
