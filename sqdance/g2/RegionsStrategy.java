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

	int d;
	
	HashMap<Integer, Integer> local_ids, actual_ids;
	HashMap<Integer, Integer> region1Dancers, region2Dancers, region3Dancers;
	
	// Uses global IDs
	HashMap<Integer, Location> dancer_locations;

	int batch_size;
	int num_batches;
	int target_score;


	// YOGESH ZONE STARTS

    // Target position for each dancer, null if not in moving phase
    private Vector[] move_targets;
    private static double MOVE_DIST = 2.0;
    private static double MOVE_EPSILON = 0.1;


    // Uses dancer_locations and move_targets to come up with direction to move in each step
    private Vector[] generateMoveInstructions() {
        if (move_targets == null) {
            return null;
        }

        // Move to the target locations as fast as possible
        Vector[] instructions = new Vector[d];
        Vector difference;
        double norm;
        for (int i = 0; i < d; ++i) {
        	difference = move_targets[i].add( dancer_locations.get(i).getVector().multiply(-1) );
        	norm = difference.norm();
            if (norm - MOVE_DIST > MOVE_EPSILON) {
            	if( Math.abs(difference.x) > Math.abs(difference.y) ) {
            		difference.y = 0;
            	} else {
            		difference.x = 0;
            	}
            }
            // TODO
            difference.getLengthLimitedVector(MOVE_DIST - MOVE_EPSILON);
            instructions[i] = difference;
        }

        for (int i = 0; i < d; i+=100) {
        	System.out.print(move_targets[i]);
        	System.out.print("\t");
        }
        System.out.println();
        for (int i = 0; i < d; i+=100) {
        	System.out.print(dancer_locations.get(i).getVector());
        	System.out.print("\t");
        }
        System.out.println();
        System.out.println();
        for(int i=0; i<d; ++i) {
        	dancer_locations.get(i).getVector().x += instructions[i].x;
        	dancer_locations.get(i).getVector().y += instructions[i].y;
        }
        return instructions;
    }

    // If all dancers have reached target, return true Otherwise, return false
    private boolean isMovementComplete() {
    	// System.out.print("isMovementComplete:");
		if(move_targets!=null) {
	        for (int i = 0; i < d; ++i) {
	            if (	dancer_locations.get(i).getVector().x != move_targets[i].x
	            	||	dancer_locations.get(i).getVector().y != move_targets[i].y ) {
			        // System.out.println("false");
	                return false;
	            }
	        }
	        move_targets=null;
        }
        // System.out.println("true");
        return true;
    }

    // Set move target to the corresponding Point in the next tile
    private int region3size = 0;
    private void setMoveTargets() {
        move_targets = new Vector[d];
        int d1_id, d2_id, d3_id;
        for(int i=0; i<d; ++i) {
        	move_targets[i] = dancer_locations.get(i).getVector();
        }
        for(int i=0; i<batch_size; ++i) {
        	d1_id = region1Dancers.get(i);
        	d2_id = region2Dancers.get(i);
        	// d3_id = region3Dancers.get(i)
        	move_targets[d1_id] = new Vector( dancer_locations.get(d2_id).getVector()); // slightly left of this
        	move_targets[d2_id] = new Vector( region3[region3size++]);

        	region3Dancers.put(i, d2_id);
        	region2Dancers.remove(i);
        	region2Dancers.put(i, d1_id);
        	region1Dancers.remove(i);
        }
    }

	// YOGESH ZONE ENDS


	@Override
	public Point[] generate_starting_locations(int d) {

		this.d = d;
		
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
		region1 = new Vector[d];						// for all players
		region2 = new Vector[batch_size];				// for only so many players
		region3 = new Vector[d];						// for all players
		
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
			
			// Splitting last column if there are odd number of columns
			if (num_dancing_cols % 2 == 1 && next == batch_size - DANCERS_IN_A_LINE/2) {
				current.x += DANCER_LINE_GAP;
				current.y = DISTANCE_BETWEEN_DANCERS / 2;
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
		
		if (isMovementComplete()) {
			if (4 == current_turn%5) {
				setMoveTargets();
				return play(dancers, scores, partner_ids, enjoyment_gained, soulmate, current_turn, remainingEnjoyment);
			} else {
				Point[] instructions = new Point[d];
				for(int i=0;i<d;++i) {
					instructions[i] = new Point(0,0);
				}
				// System.out.println("x");
				return instructions;
				// TODO dance
			}
		} else {
			return Vector.getPoints(generateMoveInstructions());
		}
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
