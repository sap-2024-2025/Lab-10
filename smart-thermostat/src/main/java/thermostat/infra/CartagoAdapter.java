package thermostat.infra;

import cartago.Artifact;
import cartago.CartagoEnvironmentStandalone;

public abstract class CartagoAdapter {

	protected Artifact discover(String artifactName) {
		while (true) {
			var w = CartagoEnvironmentStandalone
					.getInstance()
					.getWorkspace();
			if (w != null) {
				Artifact art = w.getRawArtifactReference(artifactName);
				if (art != null) {
					return art;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception ex) {}	
				}
			} else {
				try {
					Thread.sleep(200);
				} catch (Exception ex) {}	
			}
		}
	}

}
