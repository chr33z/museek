package de.mimuc.pem_music_graph.list;

public interface EventControllerListener {

	public void onEventControllerUpdate();

	public void onAddFavorites(String locationID);

	public void onRemoveFavorites(String locationID);

	public void onExpandedItem(String locationID, boolean b);
}
