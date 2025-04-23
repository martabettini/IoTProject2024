package it.unipr.iotlab.iot2024.cf.server;

import org.eclipse.californium.core.CoapServer;
import it.unipr.iotlab.iot2024.cf.server.resources.PresenceResource;

/**
 * The EntranceTurnstile class extends CoapServer and simulates a CoAP server 
 * that manages a single resource: PresenceResource. This resource tracks the 
 * presence of people entering through a turnstile.
 */
public class EntranceTurnstile extends CoapServer {

    /**
     * Constructor for EntranceTurnstile, initializing the server on a given port
     * and adding the PresenceResource, which tracks the number of people entering.
     *
     * @param port the port number the server will run on
     */
    public EntranceTurnstile(int port) {
        super(port);
        
        // Create the resource for tracking the presence of people
        PresenceResource presenceResource = new PresenceResource("PresenceResource");
        
        // Add the presence resource to the CoAP server
        this.add(presenceResource);
    }

    public static void main(String[] args) {
        // Create an instance of EntranceTurnstile on CoAP port 5686
        EntranceTurnstile entranceTurnstile = new EntranceTurnstile(5686);
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.println("Starting Entrance Turnstile...\n");

        try {
            // Start the server
            entranceTurnstile.start();
            Thread.sleep(2000); // Wait for the server to fully start
        } catch (Exception e) {
            e.printStackTrace(); // Print any exception that occurs
        }

        // Print out resource information (name, URI, and if it is observable)
        entranceTurnstile.getRoot().getChildren().stream().forEach(resource -> {
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
