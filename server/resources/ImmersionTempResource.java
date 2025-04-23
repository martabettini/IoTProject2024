package it.unipr.iotlab.iot2024.cf.server.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import it.unipr.iotlab.iot2024.cf.server.sensors.ImmersionTempSensor;

/**
 * This class provides functionality for handling
 * GET and POST requests, allowing clients to read or update the temperature.
 */
public class ImmersionTempResource extends CoapResource {

    // Constants for resource metadata
    private static final String OBJECT_TITLE = "ImmersionTempResource"; // Title of the resource
    private static final long UPDATE_TIME_MS = 10000; // Maximum time between updates (in milliseconds)

    // Sensor object to handle immersion temperature
    private ImmersionTempSensor immersionTempSensor;

    // Gson object to format responses and parse requests in JSON
    private Gson gson;

    /**
     * Constructor for ImmersionTempResource, initializing the resource with a name.
     *
     * @param name the name of the resource
     */
    public ImmersionTempResource(String name) {
        super(name);
        init();
    }

    /**
     * Initializes the resource, setting up attributes, the immersion temperature sensor,
     * and configuring observability and confirmation for client-server communication.
     */
    private void init() {
        // Set the title attribute of the resource
        getAttributes().setTitle(OBJECT_TITLE);

        // Initialize Gson for JSON conversion and the immersion temperature sensor
        this.gson = new Gson();
        this.immersionTempSensor = new ImmersionTempSensor();
        this.immersionTempSensor.setTemperature(17); // Set initial temperature to 17 degrees

        // Make this resource observable by clients
        setObservable(true);

        // Set the observation type to CON, meaning the server will confirm each notification
        setObserveType(CoAP.Type.CON);
    }

    /**
     * Handles GET requests from clients, returning the current temperature as a JSON response.
     * The response also includes a max-age to indicate how long the response is valid.
     *
     * @param exchange the CoAP exchange object representing the request-response interaction
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.accept(); // Acknowledge the request
        // Set the maximum age of the response in seconds
        exchange.setMaxAge(UPDATE_TIME_MS / 1000);
        try {
            // Convert the immersion temperature sensor data to JSON format
            String responseBody = this.gson.toJson(this.immersionTempSensor);
            // Respond with the temperature data in JSON format and set the content type
            exchange.respond(CoAP.ResponseCode.CONTENT, responseBody, MediaTypeRegistry.APPLICATION_JSON);
        } catch (Exception e) {
            System.out.println("ERROR GET"); // Log an error if something goes wrong
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR); // Respond with an internal server error
        }
    }

    /**
     * Handles POST requests, allowing clients to update the immersion temperature.
     * The request body is expected to contain the new temperature in JSON format.
     * After successfully updating, a CHANGED response is sent, and observers are notified.
     *
     * @param exchange the CoAP exchange object representing the request-response interaction
     */
    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
            // Get the payload from the POST request
            String payload = new String(exchange.getRequestPayload());
            // Parse the payload JSON into an ImmersionTempSensor object
            ImmersionTempSensor tmp = this.gson.fromJson(payload, ImmersionTempSensor.class);
            // Update the sensor's temperature with the new value
            this.immersionTempSensor.setTemperature(tmp.getTemperature());
            // Respond with a CHANGED status indicating the resource was updated
            exchange.respond(CoAP.ResponseCode.CHANGED);
            // Notify all clients observing this resource that the state has changed
            changed();
        } catch (Exception e) {
            System.out.println("ERROR POST"); // Log an error if something goes wrong
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR); // Respond with an internal server error
        }
    }
}

