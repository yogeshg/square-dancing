package sqdance.g2;

import sqdance.sim.Point;

public class DummyStrategy implements Strategy {
    static double MIN_DIST = 0.1;
	public Point[] generate_starting_locations(int d) {
        Point[] start = new Point[d];
        for(int i=0; i<d; ++i) {
            start[i] = new Point(i*MIN_DIST,0);
        }
        return start;
    }
	public Point[] play(Point[] dancers,
			int[] scores,
			int[] partner_ids,
			int[] enjoyment_gained,
			int[] soulmate,
			int current_turn) {
        Point[] play = new Point[dancers.length];
        return play;
    }
}
