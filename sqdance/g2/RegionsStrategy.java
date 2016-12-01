package sqdance.g2;

import java.util.HashMap;
import java.util.List;

import sqdance.sim.Point;

public class RegionsStrategy implements Strategy {
	
	Vector[] region1;
	Vector[] region2;
	Vector[] region3;
	
	HashMap<Integer, Integer> local_ids, actual_ids;
	
	// Uses global IDs
	HashMap<Integer, Location> dancer_locations;

	int batch_size;
	int target_score;
	
	@Override
	public Point[] generate_starting_locations(int d) {
		return null;
	}

	@Override
	public Point[] play(Point[] dancers, int[] scores, int[] partner_ids, int[] enjoyment_gained, int[] soulmate,
			int current_turn, int[][] remainingEnjoyment) {
		return null;
	}

}

// Has an array reference and the index of a point into it
class Location {
	Vector[] region;
	int index;
	
	public Location(Vector[] region, int index) {
		this.region = region;
		this.index = index;
	}
	
	public Vector getVector() {
		return this.region[this.index];
	}
}
