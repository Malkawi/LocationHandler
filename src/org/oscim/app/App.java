/*
 * Copyright 2012 Hannes Janetzek
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.app;

import org.oscim.view.MapView;

import android.app.Application;
import android.content.res.Resources;

// see http://androidcookbook.com : Android's Application Object as a "Singleton"
public class App extends Application {

	public static final String TAG = TileMap.class.getName();

	static MapView map;
	static POISearch poiSearch;
	static Resources res;
	static TileMap activity;

	@Override
	public void onCreate() {
		super.onCreate();

		
		res = getResources();
	}
}
