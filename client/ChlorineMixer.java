package it.unipr.iotlab.iot2024.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;

import com.google.gson.Gson;

import it.unipr.iotlab.iot2024.cf.server.sensors.ChlorineSensor;

/**
 * The ChlorineMixer class is a CoAP client that interacts with a ChlorineResource
 * to monitor and adjust the chlorine concentration in a system. It observes the chlorine
 * resource, checks if the concentration falls below a specified threshold, and if so,
 * it sends POST requests to increase the concentration to an ideal value.
 */
public class ChlorineMixer {

    // Base CoAP endpoint URL
    private static final String COAP_ENDPOINT = "coap://127.0.0.1:";
    // Resource path for the ChlorineResource
    private static final String RESOURCE_NAME = "/ChlorineResource";
    // Chlorine concentration threshold below which the mixer activates
    private static double threshold = 0.6;
    // Ideal chlorine concentration to which the system aims to raise the level
    private static double idealChlorine = 1.5;

    /**
     * Main method to start the CoAP client, observe chlorine concentration changes, and adjust 
     * the chlorine concentration based on the received data.
     *
     * @param args command-line arguments
     * @param port the port number on which the CoAP server is running
     */
    public static void main(String[] args, int port) {
        String endpoint = COAP_ENDPOINT + port + RESOURCE_NAME;
        CoapClient chlorineMixer = new CoapClient(endpoint);
        Gson gson = new Gson();
        
        // Create an instance of ChlorineSensor to manage concentration data
        ChlorineSensor chlorineSensor = new ChlorineSensor();
    
        // Set up an observer for the ChlorineResource
        chlorineMixer.observe(new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
                // Handle the response from the CoAP server
                String tmpPayload = new String(response.getResponseText());
                int port = response.advanced().getSourcePort();
                
                System.out.println("Received Concentration: " + tmpPayload + " from "+ port + "\n");
                
                // Convert the JSON response to a ChlorineSensor object
                ChlorineSensor tmp = gson.fromJson(tmpPayload, ChlorineSensor.class);
                chlorineSensor.setConcentration(tmp.getConcentration());
                System.out.println("Received Observable Concentration: " + chlorineSensor.getConcentration() + "\n");
                
                // Check if the received concentration is below the threshold
                if (chlorineSensor.getConcentration() < threshold) {
                    System.out.println("Received Concentration is below threshold.\n");
                    
                    // Increment the concentration until it reaches the ideal value
                    for (double i = chlorineSensor.getConcentration(); i < idealChlorine; i += 0.1) {
                        chlorineSensor.setConcentration(i);
                        
                        // Create a POST request to update the chlorine concentration
                        Request request = new Request(CoAP.Code.POST);
                        request.setConfirmable(true);
                        String payload = gson.toJson(chlorineSensor);
                        request.setPayload(payload.getBytes());
                        
                        try {
                            // Send the POST request
                            chlorineMixer.advanced(request);
                            System.out.println("Posted new Concentration: " + chlorineSensor.getConcentration() + " on "+ port +"\n");
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


