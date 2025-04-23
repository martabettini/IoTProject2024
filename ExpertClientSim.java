package Simulation;

import it.unipr.iotlab.iot2024.cf.client.ExpertManager;

public class ExpertClientSim {

	public static void main(String[] args) {
		
		try {
			ExpertManager.main(args);
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

}
