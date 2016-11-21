package sqdance.g2;

import sqdance.sim.Point;

public class ZigZagStrategyMedium implements Strategy {
	
	private int d;
	
	@Override
	public Point[] generate_starting_locations(int d) {
		this.d = d;
		return new ZigZagStrategySmall().generate_starting_locations(d);
	}

	@Override
	public Point[] play(Point[] dancers, int[] scores, 
			int[] partner_ids, int[] enjoyment_gained,
			int[] soulmate, int current_turn) {
		return null;
	}
}
