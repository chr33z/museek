package de.mimuc.pem_music_graph.list;

public interface EventControllerListener {

	public void onEventControllerUpdate();

	public void onAddFavorites(String locationID);

	public void onRemoveFavorites(String locationID);

	public void onExpandedItemTrue(String locationID);
	
	public void onExpandedItemFalse();
	
	public void onShareEvent(Event event);
}
