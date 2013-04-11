package org.osmdroid.utils;

import org.oscim.core.GeoPoint;

public class mathOp {

	
	public static  double newLat ( double lat , float offest  ){
		
		
		
		return  lat + (offest/6378137) * 180/(22/7);
		//return 0;
	}
}
