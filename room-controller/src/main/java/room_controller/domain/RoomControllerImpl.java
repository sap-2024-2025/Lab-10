package room_controller.domain;

import java.util.ArrayList;
import java.util.List;

public class RoomControllerImpl implements RoomControllerInterface {

	private enum AirConditionerState {idle, heating, cooling}
	private AirConditionerState airConditionerState;
	private List<RoomListener> notificationServices;
	private double temperature;
	private boolean processStopped;
	
	public RoomControllerImpl(double initialTemperature) {
		notificationServices = new ArrayList<>();
		this.temperature = initialTemperature;
	}

	public void addNotificationService(RoomListener notificationService) {
		this.notificationServices.add(notificationService);
	}

	@Override
	public synchronized double getTemperature() {
		return temperature;
	}

	@Override
	public synchronized void setTemperature(double t) {
		temperature	= t;
	}

	@Override
	public synchronized String getHVACState() {
		return airConditionerState.name();
	}

	@Override
	public synchronized void startHeating() {
		airConditionerState = AirConditionerState.heating;
		for (var s: notificationServices) {
			s.notifyHVACStateChanged(airConditionerState.name());
		}		
		startProcess(1);
	}

	@Override
	public synchronized void startCooling() {
		airConditionerState = AirConditionerState.cooling;
		for (var s: notificationServices) {
			s.notifyHVACStateChanged(airConditionerState.name());
		}				
		startProcess(-1);
	}

	@Override
	public synchronized void stopWorking() {
		processStopped = true;
		airConditionerState = AirConditionerState.idle;		
		for (var s: notificationServices) {
			s.notifyHVACStateChanged(airConditionerState.name());
		}		
	}

	private synchronized boolean isProcessStopeed() {
		return processStopped;		
	}

	private synchronized void updateTemperature(double delta) {
		this.temperature += delta;
		for (var s: notificationServices) {
			s.notifyTemperatureChanged(temperature);
		}		

	}
	
	private synchronized void startProcess(double delta) {
		processStopped = false;
		new Thread(() -> {			
			while (!isProcessStopeed()) {
				updateTemperature(delta);
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {}
			}
			
		}).start();
	}
	
	
}
