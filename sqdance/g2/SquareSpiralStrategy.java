package sqdance.g2;

import java.util.List;

import sqdance.sim.Point;

public class SquareSpiralStrategy {
    public static Point[] positionsSquare = null;

    static double DIST_PAIRS = 1.5;
    static double DIST_DANCERS = 1.001;

	public static Point[] init() {
		int d = Player.d;
        positionsSquare = new Point[d];
        int danceSquareSide = (int) Math.ceil( Math.sqrt( d ) );
        List<Vector> spiral = Looper2D.getCentersBetweenDancers(danceSquareSide, danceSquareSide);
        Vector p = null;
        for (int i=0; i<d/2; i++) {
            p = spiral.get(i);
            positionsSquare[d-i-1] = positionsSquare[i] = p.multiply(DIST_PAIRS).getPoint();
            if( p.state==1 || p.state==3 ) {
                positionsSquare[i] = new Point(positionsSquare[i].x,positionsSquare[i].y+DIST_DANCERS/2);
                positionsSquare[d-i-1] = new Point(positionsSquare[d-i-1].x,positionsSquare[i].y-DIST_DANCERS/2);
            } else {
                positionsSquare[i] = new Point(positionsSquare[i].x+DIST_DANCERS/2, positionsSquare[i].y);
                positionsSquare[d-i-1] = new Point(positionsSquare[i].x-DIST_DANCERS/2, positionsSquare[d-i-1].y);
            }
        }
        // Point[] instructions = new Point[d];
        return positionsSquare;
    }

    public static Point[] move(Point[] dancers,
    		int[] scores, 
    		int[] partner_ids, 
    		int[] enjoyment_gained,
    		int[][] remainingEnjoyment) {
    	int d = Player.d;
        Point[] instructions = new Point[d];
        
        //only move when i and j!=i cannot dance any more
        int sad_dancers = 0;
        for(int i = 0 ; i < d ; ++i) {
        	int j = partner_ids[i];
        	if(i!=j && remainingEnjoyment[i][j] == 0)
        		sad_dancers ++ ;
        }
        for(int i = 0 ; i < d ; ++i) {
        	int next_i = i;
        	if(sad_dancers == 0)
        		next_i = i;
        	else 
        		next_i = (i==d-1?0:i+1);
        	instructions[i] = 
        			new Vector(dancers[next_i].x - dancers[i].x,
        					dancers[next_i].y - dancers[i].y)
        			.getLengthLimitedVector(2)
        			.getPoint();
        }
        return instructions;
    }
}
