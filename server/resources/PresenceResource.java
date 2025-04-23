package it.unipr.iotlab.iot2024.cf.server.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

/**
 * The PresenceResource class represents a CoAP resource that tracks the presence of people entering a pool. 
 * It allows clients to retrieve the current count of people inside via GET requests, or
 * to add new entries via POST requests.
 */
public class PresenceResource extends CoapResource {

    // Constants for resource metadata
    private static final String OBJECT_TITLE = "PresenceResource"; // Title of the resource
    private static final long UPDATE_TIME_MS = 10000; // Maximum time between updates (in milliseconds)

    // List to store the identifiers of people who entered
    private List<Integer> enteredPeople;

    // Gson object to format responses and parse requests in JSON
    private Gson gson;

    /**
     * Constructor for PresenceResource, initializing the resource with a name.
     *
     * @param name the name of the resource
     */
    public PresenceResource(String name) {
        super(name);
        init();
    }

    /**
     * Initializes the resource, setting up attributes, initializing the list of entered people,
     * and configuring observability and confirmation for client-server communication.
     */
    private void init() {
        // Set the title attribute of the resource
        getAttributes().setTitle(OBJECT_TITLE);

        // Initialize Gson for JSON conversion and the list of people who entered
        this.gson = new Gson();
        this.enteredPeople = new ArrayList<>();

        // Make this resource observable by clients
        setObservable(true);

        // Set the observation type to CON, meaning the server will confirm each notification
        setObserveType(CoAP.Type.CON);
    }

    /**
     * Handles GET requests from clients, returning the number of people who have entered.
     * The response includes the count of people as a JSON-encoded number.
     *
     * @param exchange the CoAP exchange object representing the request-response interaction
     */
    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.accept(); // Acknowledge the request
        // Set the maximum age of the response in seconds
        exchange.setMaxAge(UPDATE_TIME_MS / 1000);
        try {
            // Convert the size of the enteredPeople list to JSON format (number of people entered)
            String responseBody = this.gson.toJson(this.enteredPeople.size());
            // Respond with the count in JSON format and set the content type
            exchange.respond(CoAP.ResponseCode.CONTENT, responseBody, MediaTypeRegistry.APPLICATION_JSON);
        } catch (Exception e) {
            System.out.println("ERROR GET"); // Log an error if something goes wrong
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR); // Respond with an internal server error
        }
    }

    /**
     * Handles POST requests, allowing clients to add a new person's identifier to the list of entered people.
     * The request body is expected to contain the identifier as a plain text number (integer).
     *
     * @param exchange the CoAP exchange object representing the request-response interaction
     */
    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
            // Get the identifier from the POST request payload (as a string)
            String identifier = new String(exchange.getRequestPayload());
            // Parse the identifier as an integer and add it to the enteredPeople list
            this.enteredPeople.add(Integer.parseInt(identifier));
            System.out.println("Added new identifier: " + identifier + "\n");
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
