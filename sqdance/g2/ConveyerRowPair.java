package sqdance.g2;

import java.util.HashMap;

import sqdance.sim.Point;

/*
 * This class contains a combination of a dancing area (2 parallel rows of dancers)
 * and a dense audience area (variable number of dancers)
 */
public class ConveyerRowPair {
	
	// Fixed locations for dancers to be at
	// We can also fix the set of indexes in the idle_locations array that will be transferred
	// to the dancing area in the next swap
	Point[] dancing_locations;
	Point[] idle_locations;
	
	// Dancers in this data structure and their locations (dancers never move across 2 instances)
	// map.get(i) gives the index of the point the ith dancer is at in the corresponding array
	HashMap<Integer, Integer> dancing_person_position;
	HashMap<Integer, Integer> idle_person_position;
	
	int turns_completed;
	int turns_between_swaps;
	
	public ConveyerRowPair(int[] dancer_ids, double distance_from_top, int turns_between_swaps) {
		// TODO: Initialize the row pair
		
		this.turns_completed = 0;
		this.turns_between_swaps = turns_between_swaps;
	}
	
	public HashMap<Integer, Point> make_move() {
		// TODO: Make the move and return it
		
		if (turns_completed > 0 && turns_completed % turns_between_swaps == 0) {
			// TODO swapping time - move new set to the dancing area
			// 1 turn should be enough to do this (I hope)
		} else {
			// TODO just dance (and maybe rearrange the dancers in the idle tile
			// based on their scores)
		}
		
		turns_completed++;
		
		return null;
	}
	
	/*
	 * Gets the location of the input dancer if it is in this object,
	 * otherwise returns null
	 */
	public Point getLocationOf(int dancer_id) {
		if (dancing_person_position.containsKey(dancer_id)) {
			return dancing_locations[dancing_person_position.get(dancer_id)];
		} else if (idle_person_position.containsKey(dancer_id)) {
			return idle_locations[idle_person_position.get(dancer_id)];
		} else {
			return null;
		}
	}
}
