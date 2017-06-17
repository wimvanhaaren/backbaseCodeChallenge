package it.jdev.kalah.domain.model;

import lombok.Data;

@Data
public class Board {
	
	private static final int NUMBER_OF_PITS = 14;

	private Pit[] pits = new Pit[NUMBER_OF_PITS];
	
	public void initialize() {
		for (int i = 0; i < NUMBER_OF_PITS; i++) {
			Pit pit;
			if (i == 6 || i == 13) {
				pit = new KalahPit();
			} else {
				pit = new RegularPit(6);
			}
			pits[i] = pit;
		}
	}

	public Pit moveStones(final int pitNumber) {
		Pit pit = pits[pitNumber];
		int stones = pit.takeAllStones();
		return distributeStones(stones, pitNumber);
		
	}

	private Pit distributeStones(final int stones, final int pitNumber) {
		int currentPitNumber = pitNumber + 1;
		Pit currentPit = null;
		for (int i=0; i<stones; i++) {
			if (currentPitNumber == 14) { // end of board reached
				currentPitNumber = 0; // start from first pit again
			}
			currentPit = pits[currentPitNumber];
			
			if (isOtherPlayersKalahPit(currentPit, pitNumber)) {
				// Skip this kala pit
				currentPitNumber++;
				if (currentPitNumber == 14) {
					currentPitNumber = 0;
				}
				currentPit = pits[currentPitNumber];
			}
			
			currentPit.addStone();
			currentPitNumber++;
		}
		
		// when the last stone lands in an own empty pit, the player captures this stone and all stones in the opposite pit (the other players' pit) and puts them in his own Kalah.
		if (didLastStoneLandInEmptyOwnPit(currentPitNumber - 1, pitNumber)) {
			Pit oppositePit = findOppositePit(currentPitNumber - 1);
			int allStones = oppositePit.takeAllStones();
			allStones = allStones + currentPit.takeAllStones();
			putStonesInPlayersKalahPit(allStones, pitNumber);
		}

		
		return currentPit; // return the last pit		
	}

	private void putStonesInPlayersKalahPit(int allStones, int startPitNumber) {
		Pit kalahPit = null;
		if (startPitNumber < 6) {
			kalahPit = pits[6];
		} else {
			kalahPit = pits[13];
		}
		kalahPit.addStones(allStones);
	}

	private Pit findOppositePit(int currentPitNumber) {
		int oppositePitNumber = 12 - currentPitNumber;
		return pits[oppositePitNumber];
	}

	private boolean didLastStoneLandInEmptyOwnPit(int lastPitNumber, int startPitNumber) {
		if (pits[lastPitNumber].getNumberOfStones() > 1) {
			return false;
		}
		if (pits[lastPitNumber] instanceof KalahPit) {
			return false;
		}
		if (lastPitNumber < 6 && startPitNumber < 6) {
			return true;
		}
		if (lastPitNumber > 6 && startPitNumber > 6) {
			return true;
		}
		return false;
	}

	private boolean isOtherPlayersKalahPit(Pit currentPit, int pitNumber) {
		if (! (currentPit instanceof KalahPit)) {
			return false;
		}
		
		Pit othersKalaPit;
		if (pitNumber < 6) {
			othersKalaPit = pits[13];
		} else {
			othersKalaPit = pits[6];
		}
		if (currentPit.equals(othersKalaPit)) {
			return true;
		}

		return false;
	}

	public void moveRemainingStonesToKalahPits() {
		// player1
		Pit kalaPitPlayer1 = pits[6];
		for (int i=0; i<6; i++) {
			kalaPitPlayer1.addStones(pits[i].takeAllStones());
		}
		// player2
		Pit kalaPitPlayer2 = pits[13];
		for (int i=7; i< 13; i++) {
			kalaPitPlayer2.addStones(pits[i].takeAllStones());
		}		
	}

}
