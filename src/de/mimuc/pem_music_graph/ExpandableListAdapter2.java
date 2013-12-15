package de.mimuc.pem_music_graph;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter2 extends BaseExpandableListAdapter implements
		ListAdapter {

	private static final String TAG = ExpandableListAdapter2.class.getName();

	private Context context;

	// TODO where comes the List with the Information from?
	private LocationList loList = new LocationList();
	private List<Location> locations = loList.getLocations();

	public ExpandableListAdapter2(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 0;
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
		Location location = locations.get(groupPosition);
		if (location != null) {
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

			// setting the Informations of the Location
			if (address != null)
				address.setText(location.street + " " + location.housenumber
						+ ", " + location.postcode + " " + location.city);
			if (phonenumber != null)
				phonenumber.setText(location.phonenumber);
			if (emailAddress != null)
				emailAddress.setText(location.emailAddress);
			if (openingHours != null)
				openingHours
						.setText("Öffnungszeiten: " + location.openingHours);
			if (ageRestriction != null)
				ageRestriction.setText("Altersbeschränkung: "
						+ location.ageRestriction);
			if (furtherInformation != null)
				furtherInformation.setText(location.furtherInformation);
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
		return locations.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = new TextView(context);
		textView.setText(locations.get(groupPosition).name);
		textView.setPadding(50, 10, 10, 10);
		textView.setTextSize(25);
		return textView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public boolean isEnabled(int arg0) {
		return true;
	}

}
