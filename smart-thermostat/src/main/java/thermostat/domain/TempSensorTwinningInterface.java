package thermostat.domain;


public interface TempSensorTwinningInterface {

	
	void connect(double initialValue);

	void syncState(double newValue);
	
}
