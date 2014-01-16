package de.mimuc.pem_music_graph.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.R.id;
import de.mimuc.pem_music_graph.R.layout;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

	private static final String TAG = ExpandableListAdapter2.class.getName();

	private Context context;
	// TODO muss gespeichert werden
	private boolean isStarFilled;
	private boolean isTextExpanded;

	// TODO where comes the List with the Information from?
	private List<EventLocation> eventLocationList;

	public ExpandableListAdapter2(Context context,
			List<EventLocation> eventLocationList) {
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
			// TODO Abfrage, wie gross der Bildschirm ist
			convertView = layoutInflater.inflate(R.layout.listitembig, null);
		}
		final EventLocation eventLocation = eventLocationList
				.get(groupPosition);
		if (eventLocation != null) {
			TextView openingHours = (TextView) convertView
					.findViewById(R.id.openinghours);
			final TextView furtherInformation = (TextView) convertView
					.findViewById(R.id.furtherinformation);
			TextView admissionPriceGirls = (TextView) convertView
					.findViewById(R.id.admissionpricegirls);
			TextView admissionPriceBoys = (TextView) convertView
					.findViewById(R.id.admissionpriceboys);
			TextView street = (TextView) convertView.findViewById(R.id.street);
			TextView city = (TextView) convertView.findViewById(R.id.city);
			ImageView loadWebsite = (ImageView) convertView
					.findViewById(R.id.website);
			ImageView pig = (ImageView) convertView.findViewById(R.id.pig);
			ImageView direction = (ImageView) convertView
					.findViewById(R.id.direction);

			// setting the Informations of the EventLocation
			if (openingHours != null)
				openingHours.setText("Ge\u00F6ffnet: "
						+ eventLocation.openingHours);
			if (furtherInformation != null)
				furtherInformation
						.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
			if (admissionPriceGirls != null)
				admissionPriceGirls.setText("Mädels: ");
			if (admissionPriceBoys != null)
				admissionPriceBoys.setText("Jungs: ");
			if (street != null)
				street.setText(eventLocation.street + " "
						+ eventLocation.housenumber);
			if (city != null)
				city.setText(eventLocation.postcode + " " + eventLocation.city);

			loadWebsite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
					// .parse(eventLocation.locationUri));
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://www.google.com"));
					context.startActivity(browserIntent);
				}
			});

			pig.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});

			direction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});

			furtherInformation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isTextExpanded) {
						furtherInformation.setMaxLines(10);
						setTextExpanded(true);
					} else {
						furtherInformation.setMaxLines(3);
						setTextExpanded(false);
					}
				}
			});
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
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.headlinelist, null);
		}

		// compute distance
		Location destination = new Location("destination");
		destination.setLatitude(Double.parseDouble(eventLocationList
				.get(groupPosition).latitude));
		destination.setLongitude(Double.parseDouble(eventLocationList
				.get(groupPosition).longitude));
		Location currentLocation = eventLocationList.get(groupPosition).currentLocation;
		float distance = currentLocation.distanceTo(destination);

		TextView eventLocationName = (TextView) convertView
				.findViewById(R.id.eventlocationname);
		TextView eventName = (TextView) convertView
				.findViewById(R.id.eventname);
		TextView currentDistance = (TextView) convertView
				.findViewById(R.id.currentdistance);
		final ImageView arrow = (ImageView) convertView
				.findViewById(R.id.arrow);
		final ImageView star = (ImageView) convertView.findViewById(R.id.star);

		if (eventLocationName != null) {
			eventLocationName
					.setText(eventLocationList.get(groupPosition).name);
		}
		if (eventName != null) {
			eventName.setText("Test");
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
					listView.collapseGroup(gP);
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
				if (!isStarFilled) {
					star.setImageResource(R.drawable.ic_action_not_important);
					setStarFilled(true);
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
		return "ca. " + distance + " " + distanceUnity;
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
}
