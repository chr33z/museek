package de.mimuc.pem_music_graph.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.R.id;
import de.mimuc.pem_music_graph.R.layout;
import de.mimuc.pem_music_graph.utils.LocationControllerListener;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodSession.EventCallback;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

	private static final String TAG = ExpandableListAdapter2.class.getName();

	private Context context;
	private boolean isStarFilled;
	// TODO in LocationController speichern
	private List<String> favorites;
	private boolean isTextExpanded;

	private List<Event> eventList;
	
	private LocationControllerListener callbackReceiver;

	public ExpandableListAdapter2(Context context, List<Event> eventList) {
		this.context = context;
		this.eventList = eventList;
		this.callbackReceiver = (LocationControllerListener) context;
		favorites = new ArrayList<String>();
	}

	public void updateEventList(List<Event> eL) {
		this.eventList = sortEventsDistance(eL);
	}

	private List<Event> sortEventsDistance(List<Event> eL) {
		Map<Float, Event> unsortedList = new HashMap<Float, Event>();
		for (int i = 0; i < eL.size(); i++) {
			Location destination = new Location("destination");
			destination
					.setLatitude(Double.parseDouble(eL.get(i).locationLatitude));
			destination
					.setLongitude(Double.parseDouble(eL.get(i).locationLongitude));
			Location currentLocation = eL.get(i).currentLocation;
			float distance = currentLocation.distanceTo(destination);
			unsortedList.put(distance, eL.get(i));
		}
		Map<Float, Event> sortedList = new TreeMap<Float, Event>(unsortedList);
		eL.addAll(sortedList.values());
		return eL;
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
			// TODO Abfrage, wie gross der Bildschirm ist
			convertView = layoutInflater.inflate(R.layout.listitembig, null);
		}
		final Event event = eventList.get(groupPosition);
		if (event != null) {
			TextView openingHours = (TextView) convertView
					.findViewById(R.id.openinghours);
			final TextView eventDescription = (TextView) convertView
					.findViewById(R.id.eventdescription);
			TextView admissionPriceGirls = (TextView) convertView
					.findViewById(R.id.admissionpricegirls);
			TextView admissionPriceBoys = (TextView) convertView
					.findViewById(R.id.admissionpriceboys);
			TextView addressStreet = (TextView) convertView
					.findViewById(R.id.street);
			TextView addressCity = (TextView) convertView
					.findViewById(R.id.city);
			ImageView loadWebsite = (ImageView) convertView
					.findViewById(R.id.website);
			ImageView pig = (ImageView) convertView.findViewById(R.id.pig);
			ImageView direction = (ImageView) convertView
					.findViewById(R.id.direction);

			// setting the Informations of the EventLocation
			if (openingHours != null) {
				if (event.endTime.equals("0"))
					event.endTime = "";
				if (stringNotEmpty(event.endTime))
					openingHours.setText("Ge" + context.getString(R.string.oe)
							+ "ffnet: " + event.startTime + " - "
							+ event.endTime + " Uhr");
				else if (stringNotEmpty(event.startTime))
					openingHours.setText("Ge" + context.getString(R.string.oe)
							+ "ffnet: " + " ab " + event.startTime + " Uhr");
			}
			if (eventDescription != null)
				if (stringNotEmpty(event.endTime))
					eventDescription.setText(event.eventDescription);
				else {
					eventDescription.setMaxLines(0);
					eventDescription.setPadding(0, 0, 0, 0);
				}
			if (admissionPriceGirls != null)
				admissionPriceGirls.setText("M"
						+ context.getString(R.string.ae) + "dels: " + ",-");
			if (admissionPriceBoys != null)
				admissionPriceBoys.setText("Jungs: " + ",-");
			if (addressStreet != null)
				if (stringNotEmpty(event.addressStreet)
						&& stringNotEmpty(event.addressNumber))
					addressStreet.setText(event.addressStreet + " "
							+ event.addressNumber);
			if (addressCity != null)
				if (stringNotEmpty(event.addressPostcode)
						&& stringNotEmpty(event.addressCity))
					addressCity.setText(event.addressPostcode + " "
							+ event.addressCity);

			loadWebsite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (event.locationWebsite != null) {
						if (stringNotEmpty(event.locationWebsite)) {
							Intent browserIntent = new Intent(
									Intent.ACTION_VIEW, Uri
											.parse(event.locationWebsite));
							context.startActivity(browserIntent);
						}
					}
				}
			});

			direction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});

			eventDescription.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isTextExpanded) {
						eventDescription.setMaxLines(10);
						setTextExpanded(true);
					} else {
						eventDescription.setMaxLines(3);
						setTextExpanded(false);
					}
				}
			});
		}

		return convertView;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private boolean stringNotEmpty(String string) {
		if (string.equals(""))
			return false;
		return true;
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
		return eventList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.headlinelist, null);
		}

		// compute distance
		Location destination = new Location("destination");
		destination
				.setLatitude(Double.parseDouble(eventList.get(groupPosition).locationLatitude));
		destination.setLongitude(Double.parseDouble(eventList
				.get(groupPosition).locationLongitude));
		Location currentLocation = eventList.get(groupPosition).currentLocation;
		float distance = currentLocation.distanceTo(destination);

		TextView locationName = (TextView) convertView
				.findViewById(R.id.eventlocationname);
		TextView eventName = (TextView) convertView
				.findViewById(R.id.eventname);
		TextView currentDistance = (TextView) convertView
				.findViewById(R.id.currentdistance);
		final ImageView arrow = (ImageView) convertView
				.findViewById(R.id.arrow);
		final ImageView star = (ImageView) convertView.findViewById(R.id.star);

		if (locationName != null) {
			if (stringNotEmpty(eventList.get(groupPosition).locationName))
				locationName.setText(eventList.get(groupPosition).locationName);
		}
		if (eventName != null) {
			if (stringNotEmpty(eventList.get(groupPosition).eventName))
				eventName.setText(eventList.get(groupPosition).eventName);
		}
		if (currentDistance != null) {
			currentDistance.setText(roundDistance(distance));
		}

		final int gP = groupPosition;
		final boolean iE = isExpanded;
		final ExpandableListView listView = (ExpandableListView) parent;

		arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (iE) {
					listView.collapseGroup(groupPosition);
					arrow.setImageResource(R.drawable.ic_action_expand);
				} else {
					listView.expandGroup(gP);
					arrow.setImageResource(R.drawable.ic_action_collapse);
				}
			}
		});

		star.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!eventList.get(groupPosition).isFavorite) {
					star.setImageResource(R.drawable.ic_action_not_important);
					callbackReceiver.onAddFavorites(eventList.get(groupPosition).locationID);
				} else {
					star.setImageResource(R.drawable.ic_action_important);
					setStarFilled(false);
				}
			}

		});

		return convertView;
	}

	/**
	 * if distance >=1000m, information in km, else in m
	 * 
	 * @param distance
	 * @return
	 */
	private String roundDistance(float distance) {
		String distanceUnity = "m";
		if (distance >= 1000) {
			float dist = distance;
			dist = distance / 1000;
			dist = Math.round(dist * 10);
			dist = dist / 10;
			distance = dist;
			distanceUnity = "km";
		} else
			distance = Math.round(distance);
		return "ca. " + (int) distance + " " + distanceUnity;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public boolean isStarFilled() {
		return isStarFilled;
	}

	public void setStarFilled(boolean isStarFilled) {
		this.isStarFilled = isStarFilled;
	}

	public boolean isTextExpanded() {
		return isTextExpanded;
	}

	public void setTextExpanded(boolean isTextExpanded) {
		this.isTextExpanded = isTextExpanded;
	}

	public List<String> getFavorites() {
		return favorites;
	}

}
