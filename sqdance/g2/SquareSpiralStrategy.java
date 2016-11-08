package sqdance.g2;

import java.util.List;

import sqdance.sim.Point;

public class SquareSpiralStrategy {
    public static Point[] positionsSquare = null;

    static double DIST_PAIRS = 2.0;
    static double DIST_DANCERS = 1.2;

	public static Point[] init() {
		int d = Player.d;
        positionsSquare = new Point[d];
        int danceSquareSide = (int) Math.ceil( Math.sqrt( d ) );
        List<Pair> spiral = Looper2D.getSpiral(danceSquareSide, danceSquareSide, true);
        Pair p = null;
        for (int i=0; i<d/2; i++) {
            p = spiral.get(i);
            positionsSquare[d-i-1] = positionsSquare[i] = new Point(p.i * DIST_PAIRS, p.j * DIST_PAIRS);
            if( p.state==1 || p.state==3 ) {
                positionsSquare[i] = new Point(positionsSquare[i].x+DIST_DANCERS/2, positionsSquare[i].y);
                positionsSquare[d-i-1] = new Point(positionsSquare[i].x-DIST_DANCERS/2, positionsSquare[d-i-1].y);
            } else {
                positionsSquare[i] = new Point(positionsSquare[i].x,positionsSquare[i].y+DIST_DANCERS/2);
                positionsSquare[d-i-1] = new Point(positionsSquare[d-i-1].x,positionsSquare[i].y-DIST_DANCERS/2);
            }
        }
        // Point[] instructions = new Point[d];
        return positionsSquare;
    }

    public static Point[] move(Point[] dancers, int[] scores, int[] partner_ids, int[] enjoyment_gained) {
    	int d = Player.d;
        Point[] instructions = new Point[d];
        for(int i = 0 ; i < d ; ++i) {
        	instructions[i]  = new Point(0,0);
        }
        return instructions;
    }
}
