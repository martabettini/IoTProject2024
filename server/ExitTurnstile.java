package it.unipr.iotlab.iot2024.cf.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2024.cf.server.resources.PresenceResource;

/**
 * The ExitTurnstile class extends CoapServer and simulates a CoAP server 
 * that manages a single resource: PresenceResource. This resource tracks the 
 * presence of people exiting through a turnstile. The server runs on a 
 * specified port and provides this resource for CoAP clients to interact with.
 */
public class ExitTurnstile extends CoapServer {

    /**
     * Constructor for ExitTurnstile, initializing the server on a given port
     * and adding the PresenceResource, which tracks the number of people exiting.
     *
     * @param port the port number the server will run on
     */
    public ExitTurnstile(int port) {
        super(port);
        
        // Create the resource for tracking the presence of people exiting
        PresenceResource presenceResource = new PresenceResource("PresenceResource");
        
        // Add the presence resource to the CoAP server
        this.add(presenceResource);
    }

    public static void main(String[] args) {
        // Create an instance of ExitTurnstile on CoAP port 5687
        ExitTurnstile exitTurnstile = new ExitTurnstile(5687);
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.println("Starting Exit Turnstile...\n");

        try {
            // Start the server
            exitTurnstile.start();
            Thread.sleep(2000); // Wait for the server to fully start
        } catch (Exception e) {
            e.printStackTrace(); // Print any exception that occurs
        }

        // Print out resource information (name, URI, and if it is observable)
        exitTurnstile.getRoot().getChildren().stream().forEach(resource -> {
            System.out.printf("Resource %s -> URI: %s (Observable: %b)%n", 
                              resource.getName(), resource.getURI(), resource.isObservable());

            // Print child resources (if any), excluding the default well-known CoAP resource
            if (!resource.getURI().equals("/.well-known")) {
                resource.getChildren().stream().forEach(childResource -> {
                    System.out.printf("Resource %s -> URI: %s (Observable: %b)%n", 
                                      childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });
        
    }
}
