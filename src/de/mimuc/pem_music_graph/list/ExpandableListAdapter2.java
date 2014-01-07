package de.mimuc.pem_music_graph.list;

import java.util.ArrayList;
import java.util.List;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.R.id;
import de.mimuc.pem_music_graph.R.layout;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

	private static final String TAG = ExpandableListAdapter2.class.getName();

	private Context context;

	// TODO where comes the List with the Information from?
	private List<EventLocation> eventLocationList;

	public ExpandableListAdapter2(Context context, List<EventLocation> eventLocationList) {
		this.context = context;
		
		this.eventLocationList = eventLocationList;
	}

	public void updateEventLocationList(List<EventLocation> eventLocationList) {
	    this.eventLocationList = eventLocationList;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.listitem, null);
		}
		EventLocation eventLocation = eventLocationList.get(groupPosition);
		if (eventLocation != null) {
			TextView address = (TextView) convertView
					.findViewById(R.id.address);
			TextView phonenumber = (TextView) convertView
					.findViewById(R.id.phonenumber);
			TextView emailAddress = (TextView) convertView
					.findViewById(R.id.emailAddress);
			TextView openingHours = (TextView) convertView
					.findViewById(R.id.openingHours);
			TextView ageRestriction = (TextView) convertView
					.findViewById(R.id.ageRestriction);
			TextView furtherInformation = (TextView) convertView
					.findViewById(R.id.furtherInformation);

			// making Textfields not editable
			address.setKeyListener(null);
			phonenumber.setKeyListener(null);
			emailAddress.setKeyListener(null);
			openingHours.setKeyListener(null);
			ageRestriction.setKeyListener(null);
			furtherInformation.setKeyListener(null);

			// setting the Informations of the EventLocation
			if (address != null)
				address.setText(eventLocation.street + " "
						+ eventLocation.housenumber + ", "
						+ eventLocation.postcode + " " + eventLocation.city);
			if (phonenumber != null)
				phonenumber.setText(eventLocation.phonenumber);
			if (emailAddress != null)
				emailAddress.setText(eventLocation.emailAddress);
			if (openingHours != null)
				openingHours.setText("�ffnungszeiten: "
						+ eventLocation.openingHours);
			if (ageRestriction != null)
				ageRestriction.setText("Altersbeschr�nkung: "
						+ eventLocation.ageRestriction);
			if (furtherInformation != null)
				furtherInformation.setText(eventLocation.furtherInformation);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// always only one child in the expandable ListView
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getGroupCount() {
		return eventLocationList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		Location destination = new Location("destination");
		destination.setLatitude(Double.parseDouble(eventLocationList.get(groupPosition).latitude));
		destination.setLongitude(Double.parseDouble(eventLocationList.get(groupPosition).longitude));
		Location currentLocation = eventLocationList.get(groupPosition).currentLocation;
		float distance = currentLocation.distanceTo(destination);
		
		Log.v(TAG, ""+distance);
		
		TextView textView = new TextView(context);
		
		
		textView.setText(eventLocationList.get(groupPosition).name + " " + roundDistance(distance));
		textView.setPadding(50, 10, 10, 10);
		textView.setTextSize(25);
		return textView;
	}

	/**
	 * if distance >=1000m, information in km, else in m
	 * @param distance
	 * @return
	 */
	private String roundDistance(float distance) {
		String distanceUnity = "m";
		if(distance >= 1000){
			float dist = distance;
			dist = distance/1000;
			dist = Math.round(dist*10);
			dist = dist/10;
			distance = dist;
			distanceUnity = "km";
		}else
			distance = Math.round(distance);
		return distance + " " + distanceUnity;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
