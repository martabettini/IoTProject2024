package Simulation;

import it.unipr.iotlab.iot2024.cf.client.MemberClient;

public class MemberClientSim {
	
	private static final int ENTRANCE_PORT = 5686;
	private static final int EXIT_PORT = 5687;

	public static void main(String[] args) {

		try {
			
			MemberClient.main(args, 5686);
			MemberClient.main(args, 5687);
			
			Thread.sleep(1000);
			MemberClient.enteredMemberPOST(5, ENTRANCE_PORT);
			Thread.sleep(1000);
			MemberClient.enteredMemberPOST(6, ENTRANCE_PORT);
			Thread.sleep(1000);
			MemberClient.enteredMemberPOST(7, ENTRANCE_PORT);
			Thread.sleep(1000);
			
			MemberClient.enteredMemberPOST(5, EXIT_PORT);
			Thread.sleep(1000);
			
			MemberClient.enteredMemberPOST(8, ENTRANCE_PORT);
			Thread.sleep(1000);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
