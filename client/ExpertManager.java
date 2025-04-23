package it.unipr.iotlab.iot2024.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;

import com.google.gson.Gson;

import it.unipr.iotlab.iot2024.cf.server.sensors.ChlorineSensor;
import it.unipr.iotlab.iot2024.cf.server.sensors.ImmersionTempSensor;

/**
 * The ExpertManager class is a CoAP client that manages and monitors various resources
 * across different locations (entrance turnstile, exit turnstile, and pools).
 * It observes resources related to people presence, temperature, and chlorine concentration.
 * Based on the observed data, it performs emergency operations.
 */
public class ExpertManager {

    // CoAP endpoint URLs for different resources
    private static final String COAP_ENDPOINT_ENTRANCE_TURNSTILE = "coap://127.0.0.1:5686/PresenceResource";
    private CoapClient entranceTurnstileClient;
    private static final String COAP_ENDPOINT_EXIT_TURNSTILE = "coap://127.0.0.1:5687/PresenceResource";
    private CoapClient exitTurnstileClient;

    private static final String COAP_ENDPOINT_RL_TEMP= "coap://127.0.0.1:5683/ImmersionTempResource";
    private CoapClient RLTempClient;
    private static final String COAP_ENDPOINT_RL_CHLOR= "coap://127.0.0.1:5683/ChlorineResource";
    private CoapClient RLChlorineClient;

    private static final String COAP_ENDPOINT_OS_TEMP= "coap://127.0.0.1:5684/ImmersionTempResource";
    private CoapClient OSTempClient;
    private static final String COAP_ENDPOINT_OS_CHLOR= "coap://127.0.0.1:5684/ChlorineResource";
    private CoapClient OSChlorineClient;

    private static final String COAP_ENDPOINT_CP_TEMP= "coap://127.0.0.1:5685/ImmersionTempResource";
    private CoapClient CPTempClient;
    private static final String COAP_ENDPOINT_CP_CHLOR= "coap://127.0.0.1:5685/ChlorineResource";
    private CoapClient CPChlorineClient;

    // Thresholds for temperature and chlorine concentration
    private static double tempThreshold = 17;
    private static double chlorineThreshold = 0;
    private static double idealTempThreshold = 25;
    private static double idealChlorineThreshold = 1.5;

    /**
     * Initializes CoAP clients for each resource.
     */
    private void init() {
        this.entranceTurnstileClient = new CoapClient(COAP_ENDPOINT_ENTRANCE_TURNSTILE);
        this.exitTurnstileClient = new CoapClient(COAP_ENDPOINT_EXIT_TURNSTILE);
        this.RLTempClient = new CoapClient(COAP_ENDPOINT_RL_TEMP);
        this.RLChlorineClient = new CoapClient(COAP_ENDPOINT_RL_CHLOR);
        this.OSTempClient = new CoapClient(COAP_ENDPOINT_OS_TEMP);
        this.OSChlorineClient = new CoapClient(COAP_ENDPOINT_OS_CHLOR);
        this.CPTempClient = new CoapClient(COAP_ENDPOINT_CP_TEMP);
        this.CPChlorineClient = new CoapClient(COAP_ENDPOINT_CP_CHLOR);    
    }

    /**
     * Main method to start the ExpertManager, initialize CoAP clients, observe resources, and handle data.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        ExpertManager expertManager = new ExpertManager();
        expertManager.init();
        
        Gson gson = new Gson();
        
        // Observe the entrance turnstile resource
        expertManager.entranceTurnstileClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received number of entered people: " + tmpPayload);
            }

            @Override
            public void onError() {
                System.err.println("Error observing entrance turnstile.");
            }
        });
        
        // Observe the exit turnstile resource
        expertManager.exitTurnstileClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received number of exited people: " + tmpPayload);
            }

            @Override
            public void onError() {
                System.err.println("Error observing exit turnstile.");
            }
        });
        
        // Observe the temperature resource in the Relaxing Lagoon
        expertManager.RLTempClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received Temperature: " + tmpPayload);
                ImmersionTempSensor tmp = gson.fromJson(tmpPayload, ImmersionTempSensor.class);
                
                System.out.println("Received Observable Temperature from Relaxing Lagoon: " + tmp.getTemperature() + "\n");
                
                if (tmp.getTemperature() < tempThreshold) {
                    System.out.println("Received Temperature from Relaxing Lagoon is below threshold. EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    
                    tmp.setTemperature(idealTempThreshold);
                    Request request = new Request(CoAP.Code.POST);
                    request.setConfirmable(true);
                    String payload = gson.toJson(tmp);
                    request.setPayload(payload.getBytes());
                    
                    try {
                        expertManager.RLTempClient.advanced(request);
                        System.out.println("Post with new Temperature from Relaxing Lagoon: " + idealTempThreshold + " EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    } catch (Exception e) {
                        System.out.println("ERROR in sending POST.\n");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing Relaxing Lagoon temperature.");
            }
        });
        
        // Observe the chlorine resource in the Relaxing Lagoon
        expertManager.RLChlorineClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received Chlorine: " + tmpPayload);
                ChlorineSensor tmp = gson.fromJson(tmpPayload, ChlorineSensor.class);
                
                System.out.println("Received Observable Chlorine from Relaxing Lagoon: " + tmp.getConcentration() + "\n");
                
                if (tmp.getConcentration() < chlorineThreshold) {
                    System.out.println("Received Chlorine from Relaxing Lagoon is below threshold. EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    
                    tmp.setConcentration(idealChlorineThreshold);
                    Request request = new Request(CoAP.Code.POST);
                    request.setConfirmable(true);
                    String payload = gson.toJson(tmp);
                    request.setPayload(payload.getBytes());
                    
                    try {
                        expertManager.RLChlorineClient.advanced(request);
                        System.out.println("Post with new Chlorine from Relaxing Lagoon: " + idealChlorineThreshold + " EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    } catch (Exception e) {
                        System.out.println("ERROR in sending POST.\n");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing Relaxing Lagoon chlorine.");
            }
        });
        
        // Observe the temperature resource in the Olympic Swimming Pool
        expertManager.OSTempClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received Temperature: " + tmpPayload);
                ImmersionTempSensor tmp = gson.fromJson(tmpPayload, ImmersionTempSensor.class);
                
                System.out.println("Received Observable Temperature from Olympic Swimming Pool: " + tmp.getTemperature() + "\n");
                
                if (tmp.getTemperature() < tempThreshold) {
                    System.out.println("Received Temperature from Olympic Swimming Pool is below threshold. EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    
                    tmp.setTemperature(idealTempThreshold);
                    Request request = new Request(CoAP.Code.POST);
                    request.setConfirmable(true);
                    String payload = gson.toJson(tmp);
                    request.setPayload(payload.getBytes());
                    
                    try {
                        expertManager.OSTempClient.advanced(request);
                        System.out.println("Post with new Temperature from Olympic Swimming Pool: " + idealTempThreshold + " EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    } catch (Exception e) {
                        System.out.println("ERROR in sending POST.\n");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing Olympic Swimming Pool temperature.");
            }
        });
        
        // Observe the chlorine resource in the Olympic Swimming Pool
        expertManager.OSChlorineClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received Chlorine: " + tmpPayload);
                ChlorineSensor tmp = gson.fromJson(tmpPayload, ChlorineSensor.class);
                
                System.out.println("Received Observable Chlorine from Olympic Swimming Pool: " + tmp.getConcentration() + "\n");
                
                if (tmp.getConcentration() < chlorineThreshold) {
                    System.out.println("Received Chlorine from Olympic Swimming Pool is below threshold. EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    
                    tmp.setConcentration(idealChlorineThreshold);
                    Request request = new Request(CoAP.Code.POST);
                    request.setConfirmable(true);
                    String payload = gson.toJson(tmp);
                    request.setPayload(payload.getBytes());
                    
                    try {
                        expertManager.OSChlorineClient.advanced(request);
                        System.out.println("Post with new Chlorine from Olympic Swimming Pool: " + idealChlorineThreshold + " EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    } catch (Exception e) {
                        System.out.println("ERROR in sending POST.\n");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing Olympic Swimming Pool chlorine.");
            }
        });
        
        // Observe the temperature resource in the Children Pool
        expertManager.CPTempClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received Temperature: " + tmpPayload);
                ImmersionTempSensor tmp = gson.fromJson(tmpPayload, ImmersionTempSensor.class);
                
                System.out.println("Received Observable Temperature from Children Pool: " + tmp.getTemperature() + "\n");
                
                if (tmp.getTemperature() < tempThreshold) {
                    System.out.println("Received Temperature from Children Pool is below threshold. EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    
                    tmp.setTemperature(idealTempThreshold);
                    Request request = new Request(CoAP.Code.POST);
                    request.setConfirmable(true);
                    String payload = gson.toJson(tmp);
                    request.setPayload(payload.getBytes());
                    
                    try {
                        expertManager.CPTempClient.advanced(request);
                        System.out.println("Post with new Temperature from Children Pool: " + idealTempThreshold + " EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    } catch (Exception e) {
                        System.out.println("ERROR in sending POST.\n");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing Children Pool temperature.");
            }
        });
        
        // Observe the chlorine resource in the Children Pool
        expertManager.CPChlorineClient.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String tmpPayload = new String(response.getResponseText());
                System.out.println("Received Chlorine: " + tmpPayload);
                ChlorineSensor tmp = gson.fromJson(tmpPayload, ChlorineSensor.class);
                
                System.out.println("Received Observable Chlorine from Children Pool: " + tmp.getConcentration() + "\n");
                
                if (tmp.getConcentration() < chlorineThreshold) {
                    System.out.println("Received Chlorine from Children Pool is below threshold. EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    
                    tmp.setConcentration(idealChlorineThreshold);
                    Request request = new Request(CoAP.Code.POST);
                    request.setConfirmable(true);
                    String payload = gson.toJson(tmp);
                    request.setPayload(payload.getBytes());
                    
                    try {
                        expertManager.CPChlorineClient.advanced(request);
                        System.out.println("Post with new Chlorine from Children Pool: " + idealChlorineThreshold + " EMERGENCY OPERATION PERFORMED BY THE E.M.\n");
                    } catch (Exception e) {
                        System.out.println("ERROR in sending POST.\n");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                System.err.println("Error observing Children Pool chlorine.");
            }
        });
        
        // Keep the application running to continuously observe resources
        try {
            Thread.sleep(300000); // Observe for 5 minutes
        } catch (InterruptedException e) {
            e.printStackTrace(); // Handle interruption
        }
    }
}
