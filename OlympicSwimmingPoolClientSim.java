package Simulation;

import it.unipr.iotlab.iot2024.cf.client.ChlorineMixer;
import it.unipr.iotlab.iot2024.cf.client.HeatingPump;

public class OlympicSwimmingPoolClientSim {

	private static final int POOL_PORT = 5684;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			HeatingPump.main(args, POOL_PORT);
			ChlorineMixer.main(args, POOL_PORT);
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

}
