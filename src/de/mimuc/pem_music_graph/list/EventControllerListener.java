package de.mimuc.pem_music_graph.list;

import android.view.View;

public interface EventControllerListener {

	public void onEventControllerUpdate();
	
	public void onEventControllerFinished();

	public void onAddFavorites(String locationID);

	public void onRemoveFavorites(String locationID);

	public void onExpandItem(String locationID);
	
	public void onCollapseItem();
	
	public void onShareEvent(Event event);
	
	public void scrollEventTop(View listItem);
	
	public void attachMap(Event event);
}
