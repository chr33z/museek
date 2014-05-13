package de.mimuc.pem_music_graph.list;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.utils.Utils;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EventListAdapter extends BaseExpandableListAdapter {

	private static final String TAG = EventListAdapter.class.getName();

	/** Saves the context */
	private Context context;

	// TODO store in event
	private boolean isDescriptionExpanded;

	/** Saves events in an arrayList */
	private List<Event> eventList;

	/** The activity that handles all callbacks (parent Activity) */
	private EventControllerListener callbackReceiver;

	/** is true if the currentNode has no events */
	private boolean noEvents = false;

	/**
	 * constructor
	 * 
	 * @param context
	 * @param eventList
	 */
	public EventListAdapter(Context context, List<Event> eventList) {
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
		final Event currentEvent = (Event) getGroup(groupPosition);
		
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.listitembig, null);
		}
		if (currentEvent != null) {
			TextView eventDescription = (TextView) convertView
					.findViewById(R.id.eventdescription);
			TextView admissionPrice = (TextView) convertView
					.findViewById(R.id.admissionprice);
			TextView addressStreet = (TextView) convertView
					.findViewById(R.id.street);
			TextView addressCity = (TextView) convertView
					.findViewById(R.id.city);
			ImageView loadWebsite = (ImageView) convertView
					.findViewById(R.id.website);
			ImageView share = (ImageView) convertView.findViewById(R.id.share);
			
			RelativeLayout map = (RelativeLayout) convertView.findViewById(R.id.map_layout);

			if (eventDescription != null && Utils.stringNotEmpty(currentEvent.eventDescription)){
				eventDescription.setVisibility(View.VISIBLE);
				eventDescription.setText(currentEvent.eventDescription);
			} else {
				eventDescription.setVisibility(View.GONE);
			}
			
			if(admissionPrice != null && Utils.stringNotEmpty(currentEvent.price)){
				admissionPrice.setVisibility(View.VISIBLE);
				admissionPrice.setText(currentEvent.price);
			} else {
				admissionPrice.setVisibility(View.GONE);
			}

			if (addressStreet != null)
				if (Utils.stringNotEmpty(currentEvent.addressStreet)
						&& Utils.stringNotEmpty(currentEvent.addressNumber))
					addressStreet.setText(currentEvent.addressStreet + " "
							+ currentEvent.addressNumber);
			if (addressCity != null)
				if (Utils.stringNotEmpty(currentEvent.addressPostcode)
						&& Utils.stringNotEmpty(currentEvent.addressCity))
					addressCity.setText(currentEvent.addressPostcode + " "
							+ currentEvent.addressCity);

			if (!Utils.stringNotEmpty(currentEvent.locationWebsite)) {
				loadWebsite.setVisibility(View.GONE);
			}

			loadWebsite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Utils.stringNotEmpty(currentEvent.locationWebsite)) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(currentEvent.locationWebsite));
						context.startActivity(browserIntent);
					}
				}
			});

			map.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
						openMap(Double.parseDouble(currentEvent.locationLatitude),Double.parseDouble(currentEvent.locationLongitude));
				}
			});

			share.setTag(groupPosition);
			share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Event event = eventList.get((Integer) v.getTag());
					callbackReceiver.onShareEvent(event);
				}
			});

			eventDescription.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isDescriptionExpanded) {
						((TextView)v).setMaxLines(10);
						setTextExpanded(true);
					} else {
						((TextView)v).setMaxLines(3);
						setTextExpanded(false);
					}
				}
			});
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return (eventList != null) ? eventList.get(groupPosition) : null;
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
		Event currentEvent = (Event) getGroup(groupPosition);

		// event list is not empty
		if (!noEvents) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.headlinelist, null);
			convertView.setTag(groupPosition);
			
			TextView locationName = (TextView) convertView
					.findViewById(R.id.eventlocationname);
			TextView eventName = (TextView) convertView
					.findViewById(R.id.eventname);
			TextView eventDate = (TextView) convertView
					.findViewById(R.id.eventdate);
			TextView currentDistance = (TextView) convertView
					.findViewById(R.id.currentdistance);
			ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
			arrow.setTag(groupPosition);
			
			CheckBox star = (CheckBox) convertView.findViewById(R.id.star);
			star.setTag(groupPosition);


			if (locationName != null) {
				if (Utils.stringNotEmpty(currentEvent.locationName))
					locationName
					.setText(currentEvent.locationName);
			}
			if (eventName != null) {
				if (Utils.stringNotEmpty(currentEvent.eventName))
					eventName.setText(currentEvent.eventName);
			}
			if(eventDate != null){
				eventDate.setText(Utils.getHeaderTime(Long.parseLong(currentEvent.startTime)));
			}
			
			if (currentDistance != null) {
				currentDistance
				.setText(Utils.roundDistance(currentEvent.currentDistance));
			}
			
			star.setChecked(currentEvent.isFavorite);

			if (currentEvent.isExpanded) {
				arrow.setImageResource(R.drawable.ic_action_collapse);
			} else {
				arrow.setImageResource(R.drawable.ic_action_expand);
			}
			
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int groupPosition = (Integer) v.getTag();
					ImageView arrow = (ImageView) v.findViewById(R.id.arrow);
					Event currentEvent = eventList.get(groupPosition);

					if (currentEvent.isExpanded) {
						arrow.setImageResource(R.drawable.ic_action_expand);
						currentEvent.isExpanded = false;
						callbackReceiver.onCollapseItem();
						listView.collapseGroup(groupPosition);
					} else {
						for (int i = 0; i < getGroupCount(); i++) {
							listView.collapseGroup(i);
							eventList.get(i).isExpanded = false;
						}
						arrow.setImageResource(R.drawable.ic_action_collapse);
						currentEvent.isExpanded = true;
						callbackReceiver.onExpandItem(currentEvent.ID);
						listView.expandGroup(groupPosition);
						callbackReceiver.scrollEventTop(v);
						callbackReceiver.attachMap(currentEvent);
					}
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
						callbackReceiver.onCollapseItem();
					} else {
						// all items are collapsed and the value of the previous
						// expanded listitem is set on false, after that the
						// expanded item is set on true and the current item is
						// expanded
						for (int i = 0; i < getGroupCount(); i++) {
							listView.collapseGroup(i);
							eventList.get(i).isExpanded = false;
						}
						listView.expandGroup(groupPosition);
						arrow.setImageResource(R.drawable.ic_action_collapse);
						eventList.get(groupPosition).isExpanded = true;
						callbackReceiver.onExpandItem(eventList
								.get(groupPosition).ID);
					}
				}
			});

			star.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int groupPosition = (Integer) v.getTag();
					CheckBox star = (CheckBox) v;

					if (eventList.get(groupPosition).isFavorite) {
						star.setChecked(false);
						callbackReceiver.onRemoveFavorites(eventList
								.get(groupPosition).locationID);
					} else {
						star.setChecked(true);
						callbackReceiver.onAddFavorites(eventList
								.get(groupPosition).locationID);
					}
				}

			});
		} else {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.noevents, null);
			
			TextView noEvents = (TextView) convertView
					.findViewById(R.id.noEvent);
			noEvents.setText(R.string.no_events);
			listView.setOnGroupClickListener(new OnGroupClickListener() {

				@Override
				public boolean onGroupClick(ExpandableListView parent, View v,
						int groupPosition, long id) {
					Toast.makeText(context, R.string.no_events,
							Toast.LENGTH_LONG).show();
					return true;
				}

			});
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
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

	public boolean isTextExpanded() {
		return isDescriptionExpanded;
	}

	public void setTextExpanded(boolean isTextExpanded) {
		this.isDescriptionExpanded = isTextExpanded;
	}
	
	/**
	 * sets the boolean on true if the eventList has no items
	 * 
	 * @param noEvents
	 */
	public void setNoEvents(boolean noEvents) {
		this.noEvents = noEvents;
	}
}
