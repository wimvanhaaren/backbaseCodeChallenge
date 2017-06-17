package it.jdev.kalah.domain.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Game {

	@JsonProperty(value = "gameId")
	private UUID id;

	private Player player1;
	private Player player2;
	
	private Player currentPlayer;
	
	private boolean gameOver = false;
	private Player winner;

	@JsonIgnore
	private Board board;
	
	public void start() {
		this.id = UUID.randomUUID();
		this.board = new Board();
		this.board.initialize();
		this.currentPlayer = player1;
	}

	public void move(int pit, Player player) {
		checkPlayersTurn(player);
		checkPlayersPit(pit, player);
		
		Pit lastPit = board.moveStones(pit);
		if (!(lastPit instanceof KalahPit)) {
			// switch to other player but not if last stone ended in his kalah pit...
			currentPlayer = (player.equals(player1) ? player2 : player1);
		}
		
		if (gameOver()) {
			this.gameOver = true;
			this.board.moveRemainingStonesToKalahPits();
			andTheWinnerIs();
		}
	}

	private void andTheWinnerIs() {
		int stonesPlayer1 = board.getPits()[6].getNumberOfStones();
		int stonesPlayer2 = board.getPits()[13].getNumberOfStones();
		if (stonesPlayer1 > stonesPlayer2) {
			winner = player1;
		} else {
			winner = player2;
		}
	}

	private void checkPlayersTurn(Player player) {
		if (! currentPlayer.equals(player)) {
			throw new IllegalMoveException("It is not this player's turn.");
		}
	}

	private void checkPlayersPit(int pit, Player player) {
		if ( (player.equals(player1) && pit > 6 ) || 
			 (player.equals(player2) && pit < 7)) {
			throw new IllegalMoveException("Player is not allowed to use other player's pit.");
		}
	}
	
	private boolean gameOver() {
		Pit[] pits = board.getPits();
		int count = 0;
		for (int i=0; i< 6; i++) {
			count += pits[i].getNumberOfStones();
		}
		if (count == 0) {
			return true;
		}
		count = 0;
		for (int i=7; i< 13; i++) {
			count += pits[i].getNumberOfStones();
		}
		if (count == 0) {
			return true;
		}
		return false;
	}

}
