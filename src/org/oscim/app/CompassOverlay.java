package org.oscim.app;

import org.oscim.overlay.Overlay;
import org.oscim.view.MapView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class CompassOverlay extends Overlay  implements SensorEventListener  {

	public CompassOverlay(MapView mapView) {
		super(mapView);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
