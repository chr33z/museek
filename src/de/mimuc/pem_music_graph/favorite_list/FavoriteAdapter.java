package de.mimuc.pem_music_graph.favorite_list;

import java.util.List;

import org.joda.time.DateTime;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.list.FavoriteLocation;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FavoriteAdapter extends BaseAdapter {
	
	private List<FavoriteLocation> favoriteLocations;
	
	private Context context;
	
	private int layout;
	
	public FavoriteAdapter(Context context, List<FavoriteLocation> favoriteLocations){
		this.favoriteLocations = favoriteLocations;
		this.context = context;
		this.layout = R.layout.favorite_list_item;
	}

	@Override
	public int getCount() {
		return favoriteLocations.size();
	}

	@Override
	public Object getItem(int position) {
		return favoriteLocations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(layout, null);
		
		FavoriteLocation favoriteLocation = (FavoriteLocation) getItem(position);
		
		TextView favoriteTitle = (TextView) convertView.findViewById(R.id.favorite_name);
		TextView nextEvent = (TextView) convertView.findViewById(R.id.favorite_next_event);
		TextView nextEventDate = (TextView) convertView.findViewById(R.id.favorite_next_event_date);
		
		favoriteTitle.setText(favoriteLocation.locationName);
		nextEvent.setText("Nächstes Event!");
		nextEventDate.setText("Heute - 16:30 Uhr");
		
		return convertView;
	}

}
