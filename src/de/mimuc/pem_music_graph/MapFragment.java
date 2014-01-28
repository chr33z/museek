package de.mimuc.pem_music_graph;

import com.google.android.gms.maps.GoogleMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment{

	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){

		return inflater.inflate(R.layout.map, container, false);
	}
}
