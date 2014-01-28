package de.mimuc.pem_music_graph.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.graph.GenreNode;
import android.content.ActivityNotFoundException;
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
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter2 extends BaseExpandableListAdapter {

	private static final String TAG = ExpandableListAdapter2.class.getName();

	private Context context;
	private boolean isDescriptionExpanded;
	private List<Event> eventList;
	private EventControllerListener callbackReceiver;
	private String genreNode;

	public ExpandableListAdapter2(Context context, List<Event> eventList) {
		this.eventList = new ArrayList<Event>();
		this.eventList = eventList;
		this.callbackReceiver = (EventControllerListener) context;
		this.context = context;
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
				if (currentEvent.endTime.equals("0"))
					currentEvent.endTime = "";
				if (stringNotEmpty(currentEvent.endTime))
					openingHours
							.setText("Ge"
									+ context.getString(R.string.oe)
									+ "ffnet: "
									+ formatTime(Long
											.parseLong(currentEvent.startTime))
									+ " - " + currentEvent.endTime + " Uhr");
				else if (stringNotEmpty(currentEvent.startTime))
					openingHours
							.setText("Ge"
									+ context.getString(R.string.oe)
									+ "ffnet: "
									+ " ab "
									+ formatTime(Long
											.parseLong(currentEvent.startTime))
									+ " Uhr");
			}
			if (eventDescription != null)
				if (stringNotEmpty(currentEvent.endTime))
					eventDescription.setText(currentEvent.eventDescription);
			if (stringNotEmpty(currentEvent.eventDescription))
				eventDescription.setText(currentEvent.eventDescription);
			else {
				eventDescription.setVisibility(View.GONE);
			}
			if (admissionPriceGirls != null)
				admissionPriceGirls.setText(context
						.getString(R.string.list_detail_girls));
			if (admissionPriceBoys != null)
				admissionPriceBoys.setText(context
						.getString(R.string.list_detail_boys));
			// FIXME Abfrage, ob Preise vorhanden
			admissionPriceGirls.setVisibility(View.GONE);
			admissionPriceBoys.setVisibility(View.GONE);
			// if (admissionPriceGirls != null)
			// admissionPriceGirls.setText("M"
			// + context.getString(R.string.ae) + "dels: " + ",-");
			// if (admissionPriceBoys != null)
			// admissionPriceBoys.setText("Jungs: " + ",-");

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
		Event currentEvent = eventList.get(groupPosition);

		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.headlinelist, null);
		}

		// compute distance
//		Location destination = new Location("destination");
//		destination
//				.setLatitude(Double.parseDouble(eventList.get(groupPosition).locationLatitude));
//		destination.setLongitude(Double.parseDouble(eventList
//				.get(groupPosition).locationLongitude));
//		Location currentLocation = eventList.get(groupPosition).currentLocation;
//		float distance = currentLocation.distanceTo(destination);

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
		if (getGenreNode().equals("Music")) {
			return eventList;
		}
		for (int i = 0; i < eventList.size(); i++) {
			switch (getGenreEnum(eventList.get(i).eventGenre)) {
			case POP:
				genreList.add(eventList.get(i));
				break;
			case ALTERNATIVE:
				genreList.add(eventList.get(i));
				break;
			case PUNK:
				genreList.add(eventList.get(i));
				break;
			case INDUSTRIAL:
				genreList.add(eventList.get(i));
				break;
			case BRITPOP:
				genreList.add(eventList.get(i));
				break;
			case DANCEELECTRO:
				genreList.add(eventList.get(i));
				break;
			case EXTREM:
				genreList.add(eventList.get(i));
				break;
			case TECHNO:
				genreList.add(eventList.get(i));
				break;
			case DUBSTEP:
				genreList.add(eventList.get(i));
				break;
			case HIPHOPRAP:
				genreList.add(eventList.get(i));
				break;
			case BLACK:
				genreList.add(eventList.get(i));
				break;
			case ROCKMETAL:
				genreList.add(eventList.get(i));
				break;
			case METAL:
				genreList.add(eventList.get(i));
				break;
			case PROGRESSIVE:
				genreList.add(eventList.get(i));
				break;
			case HARDROCK:
				genreList.add(eventList.get(i));
				break;
			default:
				break;
			}
		}
		return genreList;
	}

	public Genre getGenreEnum(String genre) {
		if (genre.equals("Music")) {
			return Genre.MUSIC;
		} else if (genre.equals("Pop")) {
			return Genre.POP;
		} else if (genre.equals("Alternative")) {
			return Genre.ALTERNATIVE;
		} else if (genre.equals("Punk")) {
			return Genre.PUNK;
		} else if (genre.equals("Industrial")) {
			return Genre.INDUSTRIAL;
		} else if (genre.equals("Britpop")) {
			return Genre.BRITPOP;
		} else if (genre.equals("Danceelectro")) {
			return Genre.DANCEELECTRO;
		} else if (genre.equals("Extrem")) {
			return Genre.EXTREM;
		} else if (genre.equals("Techno")) {
			return Genre.TECHNO;
		} else if (genre.equals("Dubstep")) {
			return Genre.DUBSTEP;
		} else if (genre.equals("HipHopRap")) {
			return Genre.HIPHOPRAP;
		} else if (genre.equals("Black")) {
			return Genre.BLACK;
		} else if (genre.equals("RockMetal")) {
			return Genre.ROCKMETAL;
		} else if (genre.equals("Metal")) {
			return Genre.METAL;
		} else if (genre.equals("Progressive")) {
			return Genre.PROGRESSIVE;
		} else if (genre.equals("Hardrock")) {
			return Genre.HARDROCK;
		}
		return null;

	}

	public void openMap() {
		String uri = String.format(Locale.ENGLISH,
				"http://maps.google.com/maps?&daddr=%f,%f (%s)", 12f, 2f,
				"Where the party is at");
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			try {
				Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(uri));
				context.startActivity(unrestrictedIntent);
			} catch (ActivityNotFoundException innerEx) {
				Toast.makeText(context, "Please install a maps application",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public boolean isTextExpanded() {
		return isDescriptionExpanded;
	}

	public void setTextExpanded(boolean isTextExpanded) {
		this.isDescriptionExpanded = isTextExpanded;
	}

	private String formatTime(long time) {
		String[] weekdays = context.getResources().getStringArray(
				R.array.weekdays);
		String[] months = context.getResources().getStringArray(R.array.months);

		DateTime date = new DateTime(time);
		Log.d(TAG, date.getDayOfWeek() + "");
		Log.d(TAG, date.getDayOfMonth() + "");
		Log.d(TAG, date.getMonthOfYear() + "");

		String dayWeek = weekdays[date.getDayOfWeek() - 1];
		String dayMonth = date.getDayOfMonth() + "";
		String month = months[date.getMonthOfYear() - 1];
		String hours = (date.getHourOfDay() < 10) ? "0" + date.getHourOfDay()
				: date.getHourOfDay() + "";
		String minutes = (date.getMinuteOfHour() < 10) ? "0"
				+ date.getMinuteOfHour() : date.getMinuteOfHour() + "";

		return dayWeek + ", " + dayMonth + ". " + month + ". " + hours + ":"
				+ minutes;

	}

	public String getGenreNode() {
		return genreNode;
	}

	public void setGenreNode(String genreNode) {
		this.genreNode = genreNode;
	}
}
