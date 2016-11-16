package sqdance.g2;

import sqdance.sim.Point;

public class ZigZagStrategySmall implements Strategy {
	private static final double EPSILON = 0.00001;
	
	//ToDo: Set actual number (how many dancers are in each row)
	private static final int DANCERS_IN_A_LINE = 20;
	private int num_swaps;
	@Override
	public Point[] generate_starting_locations() {
		num_swaps = 0;
		return null;
	}
	
	//ToDo: data structure access and update
	private int getXfromLocation(int i) {
		return 0;
	}
	private int getYfromLocation(int i) {
		return 0;
	}
	private int getIDfromLocation(int i) {
		return 0;
	}
	private void updateLocations(int i, Point instruction) {
		;
	}
    public Point[] playSmallD(Point[] dancers,
    		int[] scores,
    		int[] partner_ids,
    		int[] enjoyment_gained,
    		int[] soulmate,
    		int current_turn) {
    	int d = scores.length;
    	Point[] instructions = new Point[d];
    	boolean complete = true;
    	//complete if soulmates are all found
    	for(int i = 0; i < d; ++ i) {
    		if(soulmate[i] == -1) {
    			complete = false;
    		}
    	}
    	if (complete) {
    		//move to final locations
    		int cur = 0;
    		for(int i = 0; i < d; ++ i) {
    			if(soulmate[i] < i) continue;
    			int j = soulmate[i];
    			int x1 = getXfromLocation(cur);
				int y1 = getYfromLocation(cur);
    			int x2 = getXfromLocation(cur + 1);
				int y2 = getYfromLocation(cur + 1);
				cur += 2;
				instructions[i] = new Vector(x1 - dancers[i].x,
    					y1 - dancers[i].y)
    					.getLengthLimitedVector(2)
    					.getPoint();
				instructions[i] = new Vector(x2 - dancers[j].x,
    					y2 - dancers[2].y)
    					.getLengthLimitedVector(2)
    					.getPoint();
    		}
    	} else {
    		//swap and dance
    		if(current_turn % 2 == 0) {
    			//dance
    			for(int i = 0; i < d; ++i) {
    				instructions[i] = new Point(0,0);
    			}
    		} else {
    			//swap
				for(int i = 0; i < d; i ++) {
					
					int x1 = getXfromLocation(i);
					int y1 = getYfromLocation(i);
					int id1 = getIDfromLocation(i);
					int next;
					if( num_swaps % 2 == 0) 
						next = (i % 2 == 0 ? i + 1 : i - 1);
					else {
						if(i == 0 || i == d-1) {
							instructions[i] = new Point(0, 0);
							continue;
						}
						next = (i % 2 == 0 ? i - 1 : i + 1);
					}
					
					int x2 = getXfromLocation(next);
					int y2 = getYfromLocation(next);
					int id2 = getIDfromLocation(next);
					Vector direction = new Vector(x2 - x1, y2 - y1);
					int i_mod = i % (2 * DANCERS_IN_A_LINE);
					if(num_swaps % 2 == 0) {
	    				//swap first and second, third with fourth, ...
						if(i_mod >= DANCERS_IN_A_LINE) {
    						i_mod = DANCERS_IN_A_LINE - 1 - i_mod;
    					}
						if(i_mod % 2 == 0 && i_mod != DANCERS_IN_A_LINE - 1) {
							direction = direction.add(2 * EPSILON, 0);
						}
					} else {
	    				//swap second with third, fourth with fifth, ...
						if(i_mod >= DANCERS_IN_A_LINE) {
    						i_mod = DANCERS_IN_A_LINE - 1 - i_mod;
    					}
						if((i_mod % 2 == 0 && i_mod != 0) 
								|| i_mod == DANCERS_IN_A_LINE - 1) {
							direction = direction.add(-2 * EPSILON, 0);
						} 
					}
					instructions[id1] = direction.getPoint();
					
    				
    			}
    			++ num_swaps;
    		}
    	}
    	for(int i = 0; i < d; ++i) {
    		updateLocations(i, instructions[i]);
    	}
    	return instructions;
    }
    
    @Override
	public Point[] play(Point[] dancers, int[] scores,
			int[] partner_ids, int[] enjoyment_gained) {
    	return null;
    }
    //increment current turn after everyone is done dancing with strangers/friends
    //for medium d
    //for small d just increment it every turn
	public Point[] play(Point[] dancers, int[] scores,
			int[] partner_ids, int[] enjoyment_gained,
			int[] soulmate, int current_turn) {
		return playSmallD(dancers,
	    		scores,
	    		partner_ids,
	    		enjoyment_gained,
	    		soulmate,
	    		current_turn);
	}
}
