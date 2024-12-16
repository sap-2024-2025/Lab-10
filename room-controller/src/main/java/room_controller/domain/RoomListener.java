package room_controller.domain;

public interface RoomListener {

	void notifyTemperatureChanged(double newTemp);

	void notifyHVACStateChanged(String newState);
	
}
