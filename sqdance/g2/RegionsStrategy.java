package sqdance.g2;

import java.util.HashMap;
import java.util.List;

import sqdance.sim.Point;

public class RegionsStrategy implements Strategy {
	
	private static final double EPSILON = 0.0000001;
	
	private static final int DANCERS_IN_A_LINE = 40;
	private static final int IDLERS_IN_A_LINE = 200;
	
	private static final double root3 = Math.sqrt(3);
	
	private static final double DISTANCE_BETWEEN_DANCERS = 0.500001;
	private static final double DANCER_LINE_GAP = root3 / 2 * DISTANCE_BETWEEN_DANCERS;
	private static final double DISTANCE_BETWEEN_IDLERS = 0.1;
	private static final double IDLER_LINE_GAP = root3 / 2 * DISTANCE_BETWEEN_IDLERS;
	
	Vector[] region1;
	Vector[] region2;
	Vector[] region3;
	
	HashMap<Integer, Integer> local_ids, actual_ids;
	HashMap<Integer, Integer> region1Dancers, region2Dancers, region3Dancers;
	
	// Uses global IDs
	HashMap<Integer, Location> dancer_locations;

	int batch_size;
	int num_batches;
	int target_score;
	
	@Override
	public Point[] generate_starting_locations(int d) {
		
		Vector[] locations = new Vector[d];
		dancer_locations = new HashMap<>();
		region1Dancers = new HashMap<>();
		region2Dancers = new HashMap<>();
		region3Dancers = new HashMap<>();
		
		/*
		 *  Calculate batch size and the target score
		 */
		
		int num_dancing_cols = (int) Math.ceil((18.8 - root3*DISTANCE_BETWEEN_IDLERS*d/400)
											   / (root3*DISTANCE_BETWEEN_DANCERS/2 - root3*DISTANCE_BETWEEN_IDLERS/10))
								 - 1;
		batch_size = 40 * num_dancing_cols;
		num_batches = (int)Math.ceil(d * 1.0 / batch_size);
		target_score = 500;
		
		int dancers_in_round_1 = batch_size;
		if (num_batches == 2) {
			dancers_in_round_1 = d % 4 == 0 ? d/2 : d/2 + 1;
		}
		
		/*
		 * Make regions
		 */
		region1 = new Vector[d];
		region2 = new Vector[batch_size];
		region3 = new Vector[d];
		
		Vector current;
		
		// Region 1
		current = new Vector(0, 0);
		for (int i = 0; i < d; ++i) {
			region1[i] = new Vector(current);
			
			int next = i + 1;
			if (next % IDLERS_IN_A_LINE == 0) {
				current.x += IDLER_LINE_GAP + EPSILON;
				current.y = next % (2*IDLERS_IN_A_LINE) == 0 ? 0 : DISTANCE_BETWEEN_IDLERS / 2;
			} else {
				current.y += DISTANCE_BETWEEN_IDLERS + EPSILON;
			}
		}
		
		double maxX = 0;
		for (int i = dancers_in_round_1; i < d; ++i) {
			locations[i] = region1[i - dancers_in_round_1];
			dancer_locations.put(i, new Location(region1, i - dancers_in_round_1));
			if (maxX < locations[i].x) {
				maxX = locations[i].x;
			}
			region1Dancers.put(i - dancers_in_round_1, i);
		}
		
		// Region 2
		current = new Vector(maxX + 0.6, EPSILON);
		for (int i = 0; i < batch_size; ++i) {
			region2[i] = new Vector(current);
			
			int next = i + 1;
			if (next % DANCERS_IN_A_LINE == 0) {
				current.x += DANCER_LINE_GAP;
				if (next % (2*DANCERS_IN_A_LINE) == 0) current.x += EPSILON;
				current.y = next % (2*DANCERS_IN_A_LINE) == 0 ? EPSILON : DISTANCE_BETWEEN_DANCERS / 2;
			} else {
				current.y += DISTANCE_BETWEEN_DANCERS + EPSILON;
			}
		}
		
		for (int i = 0; i < dancers_in_round_1; ++i) {
			locations[i] = region2[i];
			dancer_locations.put(i, new Location(region2, i));
			region2Dancers.put(i, i);
		}
		
		// Region 3
		current = new Vector(20, 0);
		for (int i = 0; i < d; ++i) {
			region3[i] = new Vector(current);
			
			int next = i + 1;
			if (next % IDLERS_IN_A_LINE == 0) {
				current.x -= IDLER_LINE_GAP + EPSILON;
				current.y = next % (2*IDLERS_IN_A_LINE) == 0 ? EPSILON : DISTANCE_BETWEEN_IDLERS / 2;
			} else {
				current.y += DISTANCE_BETWEEN_IDLERS + EPSILON;
			}
		}
		
		return Vector.getPoints(locations);
	}

	@Override
	public Point[] play(Point[] dancers, int[] scores, int[] partner_ids, int[] enjoyment_gained, int[] soulmate,
			int current_turn, int[][] remainingEnjoyment) {
		
		if (current_turn == 8) {
			this.target_score = (1800/num_batches - 9) * (20*(3+Player.f_estimate)/21);
		}
		
		/*if (isMovementComplete()) {
			if (target_score_reached(scores)) {
				setMoveTargets();
				play(dancers, scores, partner_ids, enjoyment_gained, soulmate, current_turn, remainingEnjoyment);
			} else {
				// TODO dance
			}
		} else {
			return Vector.getPoints(generateMoveInstructions());
		}
		*/
		Point[] ret = new Point[dancers.length];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = new Point(0,0);
		}
		return ret;
	}

	private boolean target_score_reached(int[] scores) {
		for (int i = 0; i < scores.length; ++i) {
			if (dancer_locations.get(i).region.equals(region2) && scores[i] < target_score) {
				return false;
			}
		}
		return true;
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
