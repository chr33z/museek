package de.mimuc.pem_music_graph.favorite_list;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import de.mimuc.pem_music_graph.R;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class FavoriteListAdapter extends BaseExpandableListAdapter {

	private static final String TAG = FavoriteListAdapter.class.getName();

	/**
	 * Saves the context
	 */
	private Context context;

	private List<FavoriteLocation> favoriteList;

	/**
	 * The activity that handles all callbacks (parent Activity)
	 */
	private FavoriteListListener listener;


	/**
	 * constructor
	 * 
	 * @param context
	 * @param eventList
	 */
	public FavoriteListAdapter(Context context, List<FavoriteLocation> favoriteList, FavoriteListListener listener) {
		this.listener = listener;
		this.context = context;
		this.favoriteList = favoriteList;
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

		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.favorite_list_item_child, null);

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
		return favoriteList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final ExpandableListView listView = (ExpandableListView) parent;
		
		listView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				listener.onFavoriteClick(favoriteList.get(groupPosition));
				return true;
			}
		});

		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.favorite_list_item, null);

		final FavoriteLocation favoriteLocation = favoriteList.get(groupPosition);

		TextView favoriteTitle = (TextView) convertView.findViewById(R.id.favorite_name);
		TextView nextEvent = (TextView) convertView.findViewById(R.id.favorite_next_event);
		TextView nextEventDate = (TextView) convertView.findViewById(R.id.favorite_next_event_date);

		favoriteTitle.setText(favoriteLocation.locationName);
		
		if(favoriteLocation.nextEvent != null){
			Long startTime = Long.parseLong(favoriteLocation.nextEvent.startTime);
			
			nextEvent.setText(favoriteLocation.nextEvent.eventName);
			nextEventDate.setText(getHeaderTime(startTime));
		} else {
			nextEvent.setVisibility(View.GONE);
			nextEventDate.setVisibility(View.GONE);
		}
		

		convertView.findViewById(R.id.favorite_delete).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onFavoriteDelete(favoriteLocation.locationID);
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
	 * @return string
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

	public void openMap(double lat, double longi) {
		String uri = String.format(Locale.ENGLISH,
				"http://maps.google.com/maps?&daddr=%f,%f (%s)", lat, longi,
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

	/**
	 * formats the date
	 * 
	 * @param time
	 * @return
	 */
	private String formatTime(long time) {
		String[] weekdays = context.getResources().getStringArray(
				R.array.weekdays);
		String[] months = context.getResources().getStringArray(R.array.months);

		DateTime date = new DateTime(time);

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

	private String getHeaderTime(long startTime){
		DateTime eventTime = new DateTime(startTime);
		DateTime now = new DateTime();
		LocalDate today = now.toLocalDate();
		LocalDate tomorrow = today.plusDays(1);
		LocalDate afterTomorrow = today.plusDays(2);

		DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
		DateTime startOfTomorrow = tomorrow.toDateTimeAtStartOfDay(now.getZone()).plusHours(8);
		DateTime startOfDayAfterTomorrow = afterTomorrow.toDateTimeAtStartOfDay(now.getZone()).plusHours(8);

		String timeString;
		if(startOfToday.getMillis() <= eventTime.getMillis() &&
				eventTime.getMillis() < startOfTomorrow.getMillis()){
			timeString = context.getString(R.string.list_header_date_today) + " - "+
					context.getString(R.string.list_header_date_from) + " "+
					((eventTime.getHourOfDay() < 10) ? "0"+eventTime.getHourOfDay() : eventTime.getHourOfDay()) + ":"+
					((eventTime.getMinuteOfHour() < 10) ? "0"+eventTime.getMinuteOfHour() : eventTime.getMinuteOfHour()) + " "+
					context.getString(R.string.list_detail_clock);
		}
		else if(startOfTomorrow.getMillis() <= eventTime.getMillis() &&
				eventTime.getMillis() < startOfDayAfterTomorrow.getMillis()){
			timeString = context.getString(R.string.list_header_date_tomorrow) + " - "+
					context.getString(R.string.list_header_date_from) + " "+
					((eventTime.getHourOfDay() < 10) ? "0"+eventTime.getHourOfDay() : eventTime.getHourOfDay()) + ":"+
					((eventTime.getMinuteOfHour() < 10) ? "0"+eventTime.getMinuteOfHour() : eventTime.getMinuteOfHour()) + " "+
					context.getString(R.string.list_detail_clock);

		}
		else {
			timeString = formatTime(eventTime.getMillis());
		}

		return timeString;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private boolean stringNotEmpty(String string) {
		if (string.equals("") || string.equals("null") || string == null){
			return false;
		} else {
			return true;
		}
	}

}
