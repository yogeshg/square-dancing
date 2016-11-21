package sqdance.g2;

import java.util.HashMap;
import java.util.Set;

import sqdance.sim.Point;

public class Tile {
	private int dancers;
	private Point[] locations;
	private HashMap<Integer, Integer> dancer_at_point;
	private TileType type;
	
	public Tile(TileType type, int[] dancer_ids, Point left_top_corner) {
		this.dancers = dancer_ids.length;
		this.type = type;
		// TODO generate points to dance at and the map of who is dancing where
	}

	public Point getPoint(int point) {
		return locations[point];
	}

	public Set<Integer> getPointKeys() {
		return dancer_at_point.keySet();
	}
	
	public int getDancerAt(int point) {
		return dancer_at_point.get(point);
	}
}

enum TileType {
	DANCING, STANDBY
}