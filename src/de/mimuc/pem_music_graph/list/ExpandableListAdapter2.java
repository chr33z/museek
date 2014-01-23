package de.mimuc.pem_music_graph.list;

import java.util.ArrayList;
import java.util.List;

import de.mimuc.pem_music_graph.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

	private static final String TAG = ExpandableListAdapter2.class.getName();

	private Context context;
	private boolean isDescriptionExpanded;
	private List<Event> eventList;
	private EventControllerListener callbackReceiver;

	public ExpandableListAdapter2(Context context, List<Event> eventList) {
		this.context = context;
		this.eventList = new ArrayList<Event>();
		this.eventList = eventList;
		this.callbackReceiver = (EventControllerListener) context;
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
		final Event currentEvent = eventList.get(groupPosition);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// TODO Abfrage, wie gross der Bildschirm ist
			convertView = layoutInflater.inflate(R.layout.listitembig, null);
		}
		if (currentEvent != null) {
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
				if (currentEvent.endTime.equals("0"))
					currentEvent.endTime = "";
				if (stringNotEmpty(currentEvent.endTime))
					openingHours.setText(context
							.getString(R.string.list_detail_open)
							+ " "
							+ currentEvent.startTime
							+ " - "
							+ currentEvent.endTime
							+ " "
							+ context.getString(R.string.list_detail_clock));
				else if (stringNotEmpty(currentEvent.startTime))
					openingHours.setText(context
							.getString(R.string.list_detail_open_from)
							+ " "
							+ currentEvent.startTime
							+ " "
							+ context.getString(R.string.list_detail_clock));
			}
			if (eventDescription != null)
				if (stringNotEmpty(currentEvent.endTime))
					eventDescription.setText(currentEvent.eventDescription);
				else {
					eventDescription.setMaxLines(0);
					eventDescription.setPadding(0, 0, 0, 0);
				}
			if (admissionPriceGirls != null)
				admissionPriceGirls.setText(context
						.getString(R.string.list_detail_girls));
			if (admissionPriceBoys != null)
				admissionPriceBoys.setText(context
						.getString(R.string.list_detail_boys));
			if (addressStreet != null)
				if (stringNotEmpty(currentEvent.addressStreet)
						&& stringNotEmpty(currentEvent.addressNumber))
					addressStreet.setText(currentEvent.addressStreet + " "
							+ currentEvent.addressNumber);
			if (addressCity != null)
				if (stringNotEmpty(currentEvent.addressPostcode)
						&& stringNotEmpty(currentEvent.addressCity))
					addressCity.setText(currentEvent.addressPostcode + " "
							+ currentEvent.addressCity);

			loadWebsite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (currentEvent.locationWebsite != null) {
						if (stringNotEmpty(currentEvent.locationWebsite)) {
							Intent browserIntent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse(currentEvent.locationWebsite));
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
					if (!isDescriptionExpanded) {
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
		final ExpandableListView listView = (ExpandableListView) parent;
//		Event currentEvent = (sortGenre()).get(groupPosition);
		Event currentEvent = eventList.get(groupPosition);

		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.headlinelist, null);
		}

		TextView locationName = (TextView) convertView
				.findViewById(R.id.eventlocationname);
		TextView eventName = (TextView) convertView
				.findViewById(R.id.eventname);
		TextView currentDistance = (TextView) convertView
				.findViewById(R.id.currentdistance);
		ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
		arrow.setTag(groupPosition);
		ImageView star = (ImageView) convertView.findViewById(R.id.star);
		star.setTag(groupPosition);

		if (locationName != null) {
			if (stringNotEmpty(currentEvent.locationName))
				locationName.setText(eventList.get(groupPosition).locationName);
		}
		if (eventName != null) {
			if (stringNotEmpty(currentEvent.eventName))
				eventName.setText(currentEvent.eventName);
		}
		if (currentDistance != null) {
			currentDistance
					.setText(roundDistance(currentEvent.currentDistance));
		}
		if (currentEvent.isFavorite) {
			star.setImageResource(R.drawable.ic_action_important);
		} else {
			star.setImageResource(R.drawable.ic_action_not_important);
		}

		if (currentEvent.isExpanded) {
			listView.expandGroup(groupPosition);
			arrow.setImageResource(R.drawable.ic_action_collapse);
		} else {
			arrow.setImageResource(R.drawable.ic_action_expand);
		}

		listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				ImageView arrow = (ImageView) v.findViewById(R.id.arrow);
				Event currentEvent = eventList.get(groupPosition);

				if (currentEvent.isExpanded) {
					arrow.setImageResource(R.drawable.ic_action_expand);
					currentEvent.isExpanded = false;
					callbackReceiver.onExpandedItem(currentEvent.locationID,
							false);
				} else {
					arrow.setImageResource(R.drawable.ic_action_collapse);
					currentEvent.isExpanded = true;
					callbackReceiver.onExpandedItem(currentEvent.locationID,
							true);
				}
				return false;
			}
		});
		arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int groupPosition = (Integer) v.getTag();
				ImageView arrow = (ImageView) v;

				if (eventList.get(groupPosition).isExpanded) {
					listView.collapseGroup(groupPosition);
					arrow.setImageResource(R.drawable.ic_action_expand);
					eventList.get(groupPosition).isExpanded = false;
					callbackReceiver.onExpandedItem(
							eventList.get(groupPosition).locationID, false);
				} else {
					listView.expandGroup(groupPosition);
					arrow.setImageResource(R.drawable.ic_action_collapse);
					eventList.get(groupPosition).isExpanded = true;
					callbackReceiver.onExpandedItem(
							eventList.get(groupPosition).locationID, true);
				}
			}
		});

		star.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int groupPosition = (Integer) v.getTag();
				ImageView star = (ImageView) v;

				if (eventList.get(groupPosition).isFavorite) {
					star.setImageResource(R.drawable.ic_action_not_important);
					callbackReceiver.onRemoveFavorites(eventList
							.get(groupPosition).locationID);
				} else {
					star.setImageResource(R.drawable.ic_action_important);
					callbackReceiver.onAddFavorites(eventList
							.get(groupPosition).locationID);
				}
			}

		});

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
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

	private List<Event> sortGenre() {
		List<Event> genreList = new ArrayList<Event>();
		// TODO
		return genreList;
	}

	public boolean isTextExpanded() {
		return isDescriptionExpanded;
	}

	public void setTextExpanded(boolean isTextExpanded) {
		this.isDescriptionExpanded = isTextExpanded;
	}

}
