package it.unipr.iotlab.iot2024.cf.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2024.cf.server.resources.ChlorineResource;
import it.unipr.iotlab.iot2024.cf.server.resources.ImmersionTempResource;


/**
 * The OlympicSwimmingPool class extends CoapServer and simulates a CoAP server
 * that hosts two resources: ImmersionTempResource and ChlorineResource.
 * It starts the server and prints out resource information upon initialization.
 */
public class OlympicSwimmingPool extends CoapServer {

    /**
     * Constructor for OlympicSwimmingPool, initializing the server on a given port
     * and adding two resources: ImmersionTempResource and ChlorineResource.
     *
     * @param port the port number the server will run on
     */
    public OlympicSwimmingPool(int port) {
        super(port);
        
        // Create the resources for immersion temperature and chlorine level
        ImmersionTempResource immersionTempResource = new ImmersionTempResource("ImmersionTempResource");
        ChlorineResource chlorineResource = new ChlorineResource("ChlorineResource");
        
        // Add these resources to the CoAP server
        this.add(immersionTempResource);
        this.add(chlorineResource);
    }

    public static void main(String[] args) {
        // Create an instance of OlympicSwimmingPool on CoAP port 5684
        OlympicSwimmingPool olympicSwimmingPool = new OlympicSwimmingPool(5684);
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.println("Starting Olympic Swimmingpool...\n");

        try {
            // Start the server
            olympicSwimmingPool.start();
            Thread.sleep(2000); // Wait for the server to fully start
        } catch (Exception e) {
            e.printStackTrace(); // Print any exception that occurs
        }

        // Print out resource information (name, URI, and if they are observable)
        olympicSwimmingPool.getRoot().getChildren().stream().forEach(resource -> {
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
