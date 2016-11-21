package sqdance.g2;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import sqdance.sim.Point;

public class TilesZigZagStrategy implements Strategy {
	
	private int d;
	
	// tiles[0] will the main dance floor tile
	private List<Tile> tiles;
	
	// Target position for each dancer, null if not in moving phase
	private Point[] move_targets;
	
	// Number of turns for which we have been dancing
	private static int DANCE_TURN_LIMIT = 5;
	private int dancing_turns;
	
	public TilesZigZagStrategy() {
		this.tiles = new LinkedList<>();
	}

	@Override
	public Point[] generate_starting_locations(int d) {
		this.d = d;
		
		int num_tiles = 1;
		int dancers_per_tile = d;
		
		// Creating fist dancing tile
		int[] dancer_ids = new int[dancers_per_tile];
		for (int i = 0; i < dancers_per_tile; ++i) {
			dancer_ids[i] = i;
		}
		Tile dancing_tile = new Tile(TileType.DANCING, dancer_ids, new Point(0,0));
		tiles.add(dancing_tile);
		
		// Creating the rest of the tiles
		for (int tile_num = 1; tile_num < num_tiles; ++num_tiles) {
			
		}
		
		return combineTilePositions();
	}

	@Override
	public Point[] play(Point[] dancers, int[] scores,
			int[] partner_ids, int[] enjoyment_gained,
			int[] soulmate, int current_turn) {
		if (move_targets == null) {
			// Not moving, dancing
			
			// TODO: Dance using the zig-zag strategy
			Point[] instructions = new Point[d];
			dancing_turns++;
			
			// Set move targets if dance turns are up
			if (dancing_turns == DANCE_TURN_LIMIT) {
				setMoveTargets();
			}
			
			return instructions;
		} else {
			// If movement is complete, go to the dance part, otherwise just move
			if (movementComplete(dancers)) {
				move_targets = null;
				play(dancers, scores, partner_ids, enjoyment_gained, soulmate, current_turn);
			} else {
				return generateMoveInstructions(dancers);
			}
		}
		return null;
	}

	/*
	 * Get locations of all dancers in all tiles and combine them into an array
	 */
	private Point[] combineTilePositions() {
		Point[] final_positions = new Point[d];
		for (Tile tile : tiles) {
			for (int point : tile.getPointKeys()) {
				final_positions[tile.getDancerAt(point)] = tile.getPoint(point);
			}
		}
		
		return final_positions;
	}

	private Point[] generateMoveInstructions(Point[] dancerLocations) {
		if (move_targets == null) {
			return null;
		}
		
		// Move to the target locations as fast as possible
		Point[] instructions = new Point[d];
		for (int i = 0; i < d; ++i) {
			Point difference = new Point(move_targets[i].x - dancerLocations[i].x,
										 move_targets[i].y - dancerLocations[i].y);
			double abs = Math.sqrt(difference.x * difference.x) + (difference.y * difference.y);
			if (abs > 2) {
				difference = new Point(difference.x * 2 / abs, difference.y * 2 / abs);
			}
			instructions[i] = difference;
		}
		
		return instructions;
	}

	/*
	 * If all dancers have reached target, return true
	 * Otherwise, return false
	 */
	private boolean movementComplete(Point[] dancerLocations) {
		for (int i = 0; i < d; ++i) {
			if (dancerLocations[i].x != move_targets[i].x
				|| dancerLocations[i].y != move_targets[i].y) {
				return false;
			}
		}
		
		return true;
	}

	/*
	 * Set move target to the corresponding Point in the next tile
	 */
	private void setMoveTargets() {
		move_targets = new Point[d];
		for (int i = 0; i < tiles.size(); ++i) {
			Tile tile = tiles.get(i);
			Tile nextTile = tiles.get((i+1)%tiles.size());
			for (int pointIdx : tile.getPointKeys()) {
				// Dancer at a point index on this tile moves to point at the same index on next tile
				move_targets[tile.getDancerAt(pointIdx)] = nextTile.getPoint(pointIdx);
			}
		}
	}
}
