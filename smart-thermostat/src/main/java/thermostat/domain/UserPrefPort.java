package thermostat.domain;

public interface UserPrefPort  {
	
	void connect(double preferredTemp);

	void notifyNewPreferredTemp(double preferredTemp);

}
