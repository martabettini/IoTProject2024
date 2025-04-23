package Simulation;

/**
 * This class contains the simulation of the Smart Wellness Village
 */
public class GeneralSim {

	public static void main(String[] args) {
		
		try {
			ServerStartSim.main(args);
			MemberClientSim.main(args);
			ExpertClientSim.main(args);
	
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

}
