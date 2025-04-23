package it.unipr.iotlab.iot2024.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;

/**
 * The MemberClient class is a CoAP client that interacts with CoAP resources to 
 * monitor and update the number of people tracked by a turnstile. It provides 
 * methods to observe changes, and to send GET and POST requests to the CoAP server.
 */
public class MemberClient {

    // Base CoAP endpoint URL
    private static final String COAP_ENDPOINT = "coap://127.0.0.1:";
    // Resource path for the PresenceResource
    private static final String RESOURCE_NAME = "/PresenceResource";

    /**
     * Main method to start observing the PresenceResource. This method connects 
     * to the CoAP server, sets up an observer on the resource, and prints updates 
     * on the number of people detected.
     *
     * @param args command-line arguments
     * @param port the port number on which the CoAP server is running
     */
    public static void main(String[] args, int port) {
        String endpoint = COAP_ENDPOINT + port + RESOURCE_NAME;
        CoapClient memberClient = new CoapClient(endpoint);
        
        // Set up an observer for the resource
        memberClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                // Handle the response from the CoAP server
                String identifierPayload = new String(response.getResponseText());
                System.out.println("Number of people: " + identifierPayload);
            }

            @Override
            public void onError() {
                // Handle any errors that occur during observation
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
    
    /**
     * Sends a GET request to the PresenceResource to retrieve the current count of people.
     *
     * @param port the port number on which the CoAP server is running
     */
    public static void enteredMemberGET(int port) {
        String endpoint = COAP_ENDPOINT + port + RESOURCE_NAME;
        CoapClient memberClient = new CoapClient(endpoint);
        
        // Create a GET request
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        
        try {
            // Send the request and get the response
            CoapResponse response = memberClient.advanced(request);
            String payload = new String(response.getPayload());
            System.out.println("Current count of people: " + payload);
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions that occur
        }
    }
    
    /**
     * Sends a POST request to the PresenceResource to update the count of people 
     * entering the turnstile.
     *
     * @param identifier the identifier of the person entering
     * @param port the port number on which the CoAP server is running
     */
    public static void enteredMemberPOST(int identifier, int port) {
        String endpoint = COAP_ENDPOINT + port + RESOURCE_NAME;
        CoapClient memberClient = new CoapClient(endpoint);
        
        // Create a POST request
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        String payload = Integer.toString(identifier);
        request.setPayload(payload.getBytes());
        
        try {
            // Send the POST request
            memberClient.advanced(request);
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions that occur
        }
    }
}
