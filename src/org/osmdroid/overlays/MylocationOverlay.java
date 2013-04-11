package org.osmdroid.overlays;


import org.oscim.view.MapView;

import android.graphics.Canvas;

import com.google.android.maps.Overlay;

public class MylocationOverlay extends Overlay  {

	MapView  mapV ;
	public MylocationOverlay(MapView mapView) {
		//super(mapView);
		// TODO Auto-generated constructor stub
		
		
		mapV= mapView;
	}
	@Override
	public void draw(Canvas arg0, com.google.android.maps.MapView arg1, boolean arg2) {
		// TODO Auto-generated method stub
		super.draw(arg0, arg1, arg2);
	}

	
	
	
}
