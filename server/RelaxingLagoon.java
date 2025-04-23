package it.unipr.iotlab.iot2024.cf.server;

import org.eclipse.californium.core.CoapServer;


import it.unipr.iotlab.iot2024.cf.server.resources.ChlorineResource;
import it.unipr.iotlab.iot2024.cf.server.resources.ImmersionTempResource;


/**
 * The RelaxingLagoon class extends CoapServer and simulates a CoAP server 
 * with two resources: ImmersionTempResource and ChlorineResource. It starts
 * the server, making these resources available for CoAP clients to interact with.
 */
public class RelaxingLagoon extends CoapServer {

    // Immersion temperature resource
    private ImmersionTempResource immersionTempResource;

    // Chlorine level resource
    private ChlorineResource chlorineResource;

    /**
     * Constructor for RelaxingLagoon, initializing the server on a given port and adding
     * two resources: ImmersionTempResource and ChlorineResource.
     *
     * @param port the port number the server will run on
     */
    public RelaxingLagoon(int port) {
        super(port);
        
        // Create the two resources for immersion temperature and chlorine level
        immersionTempResource = new ImmersionTempResource("ImmersionTempResource");
        chlorineResource = new ChlorineResource("ChlorineResource");
        
        // Add these resources to the CoAP server
        this.add(immersionTempResource);
        this.add(chlorineResource);
    }


    public static void main(String[] args) {
        // Create an instance of RelaxingLagoon on CoAP port 5683
        RelaxingLagoon relaxingLagoon = new RelaxingLagoon(5683);
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.println("Starting Relaxing Lagoon...\n");

        try {
            // Start the server
            relaxingLagoon.start();
            Thread.sleep(2000); // Wait for the server to fully start
        } catch (Exception e) {
            e.printStackTrace(); // Print any exception that occurs
        }

        // Print out resource information (name, URI, and if they are observable)
        relaxingLagoon.getRoot().getChildren().stream().forEach(resource -> {
            System.out.printf("Resource %s -> URI: %s (Observable: %b)%n", 
                              resource.getName(), resource.getURI(), resource.isObservable());
            
            // Print children resources (if any), skipping the well-known CoAP resource
            if (!resource.getURI().equals("/.well-known")) {
                resource.getChildren().stream().forEach(childResource -> {
                    System.out.printf("Resource %s -> URI: %s (Observable: %b)%n", 
                                      childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });     
    }
}
