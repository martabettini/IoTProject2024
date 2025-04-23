package Simulation;

import it.unipr.iotlab.iot2024.cf.server.ChildrenPool;
import it.unipr.iotlab.iot2024.cf.server.EntranceTurnstile;
import it.unipr.iotlab.iot2024.cf.server.ExitTurnstile;
import it.unipr.iotlab.iot2024.cf.server.OlympicSwimmingPool;
import it.unipr.iotlab.iot2024.cf.server.RelaxingLagoon;

public class ServerStartSim {

	public static void main(String[] args) {
		try {
			RelaxingLagoon.main(args);
			OlympicSwimmingPool.main(args);
			ChildrenPool.main(args);
			EntranceTurnstile.main(args);
			ExitTurnstile.main(args);
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

}
