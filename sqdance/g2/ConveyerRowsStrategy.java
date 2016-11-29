package sqdance.g2;

import java.util.HashMap;

import sqdance.sim.Point;

public class ConveyerRowsStrategy implements Strategy {
	
	int d;
	ConveyerRowPair[] conveyer_rows;

	@Override
	public Point[] generate_starting_locations(int d) {
		this.d = d;
		
		// TODO: Initialize rows and their positions based on number of dancers
		conveyer_rows = new ConveyerRowPair[2 /*TODO*/];
		
		return null;
	}

	@Override
	public Point[] play(Point[] dancers, int[] scores, int[] partner_ids, int[] enjoyment_gained, int[] soulmate,
			int current_turn, int[][] remainingEnjoyment) {
		
		// Array to aggregate all row moves
		Point[] instructions = new Point[d];
		
		for (ConveyerRowPair row : conveyer_rows) {
			HashMap<Integer, Point> row_moves = row.make_move();
			addMovesToInstructions(instructions, row_moves);
		}
		
		return instructions;
	}

	private void addMovesToInstructions(Point[] instructions, HashMap<Integer, Point> row_moves) {
		// TODO: Add the hashmap contents into the aggregate instruction array
		
	}

}
