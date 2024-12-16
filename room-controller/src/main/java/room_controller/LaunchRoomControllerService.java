package room_controller;

import room_controller.domain.*;
import room_controller.infrastructure.*;

public class LaunchRoomControllerService {

	public static void main(String[] args) {
		
		/* domain layer */
		
		RoomControllerImpl domain = new RoomControllerImpl(15);
	
		/* infrastructure layer */
		
	   	/* web controller */
	   	
	   	RoomControllerService ws = new RoomControllerService(domain);	   	
	   	domain.addNotificationService(ws);
	   	ws.launch();

	}   
}
