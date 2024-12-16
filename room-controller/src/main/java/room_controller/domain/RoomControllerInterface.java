package room_controller.domain;

public interface RoomControllerInterface {
	
	double getTemperature();
	
	String getHVACState();

	void startHeating();
	
	void startCooling();
	
	void stopWorking();

	void setTemperature(double t);
	
}
	
