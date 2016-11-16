package sqdance.g2;

import sqdance.sim.Point;

public class ZigZagStrategySmall implements Strategy {
	private static final int EPSILON = 0.00001;
	private static final int DANCERS_IN_A_LINE = 20;
	@Override
	public Point[] generate_starting_locations() {
		return null;
	}
	private int getXfromLocation(int i) {
		
	}
	private int getYfromLocation(int i) {
		
	}
	private int getIDfromLocation(int i) {
		
	}
    public void play_small_d(Point[] dancers,
    		int[] scores,
    		int[] partner_ids,
    		int[] enjoyment_gained) {
    	Point[] instructions = new Point[d];
    	boolean complete = true;
    	//complete if soulmates are all found
    	for(int i = 0; i < d; ++ i) {
    		if(souldmate[i] == -1) {
    			complete = false;
    		}
    	}
    	if (complete) {
    		//move to final locations
    	} else {
    		//swap and dance
    		if(current_turn % 2 == 0) {
    			//dance
    			for(int i = 0; i < d; ++i) {
    				instructions[i] = new Point(0,0);
    			}
    		} else {
    			//swap
    			if(num_swaps % 2 == 0) {
    				//swap first and second, third with fourth, ...
    				for(int i = 0; i < d; i += 2) {
    					int x1 = getXfromLocation(i);
    					int y1 = getYfromLocation(i);
    					int x2 = getXfromLocation(i + 1);
    					int y2 = getYfromLocation(i + 1);
    					int id1 = getIDfromLocation(i);
    					int id2 = getIDfromLocation(i + 1);
    					
    				}
    				
    			} else if (num_swaps % 2 == 1) {
    				//swap second with third, fourth with fifth, ...
    			}
    			++ num_swaps;
    		}
    	}
    }
    
	@Override
	public Point[] play(Point[] dancers, int[] scores, int[] partner_ids, int[] enjoyment_gained) {
		return null;
	}
}
