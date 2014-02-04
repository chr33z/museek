package de.mimuc.pem_music_graph.list;

import java.util.Comparator;

import org.joda.time.DateTime;

public class DateDistanceComparator implements Comparator {

	@Override
	public int compare(Object lhs, Object rhs) {
		Event event1 = (Event) lhs;
		Event event2 = (Event) rhs;
		DateTime date1 = new DateTime((Long.parseLong(event1.startTime)));
		DateTime date2 = new DateTime((Long.parseLong(event2.startTime)));
		int compare = date1.compareTo(date2);
		date1 = date1.withTimeAtStartOfDay().plusHours(8);
		date2 = date2.withTimeAtStartOfDay().plusHours(8);
		int compare2 = date1.compareTo(date2);
		if (compare2 == 0) {
			float distance1 = event1.currentDistance;
			float distance2 = event2.currentDistance;
			return calculate(distance1, distance2);
		} else
			return compare;
	}

	private int calculate(float distance1, float distance2) {
		if (distance1 < distance2)
			return -1;
		if (distance1 > distance2)
			return 1;
		return 0;
	}
}
