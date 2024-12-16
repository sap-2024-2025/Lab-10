package thermostat.infra;

import thermostat.domain.UserPrefPort;

public class UserPrefAdapter extends CartagoAdapter {

	private String artifactName;
	private UserPrefPort userPrefArtifact;
	private int port;
	
	private double preferredTemperatue;
	private UserPrefWebService ws;
	
	public UserPrefAdapter(double initialPrefTemp, String artifactName, int port) {
		this.preferredTemperatue = initialPrefTemp;
		this.artifactName = artifactName;	
		this.port = port;
	}
	
	public void init() {
		try {
			userPrefArtifact = (UserPrefPort) this.discover(artifactName);
			userPrefArtifact.connect(preferredTemperatue);
			
			ws = new UserPrefWebService(this, port);
			ws.launch();
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public double getCurrentPreferredTemp() {
		return preferredTemperatue;
	}
	
	public void notifyNewPreferredTemp(double newTemp) {
		this.preferredTemperatue = newTemp;
		userPrefArtifact.notifyNewPreferredTemp(newTemp);
	}

}
