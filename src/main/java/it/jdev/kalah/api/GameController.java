package it.jdev.kalah.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.jdev.kalah.domain.model.Board;
import it.jdev.kalah.domain.model.Game;
import it.jdev.kalah.domain.model.Player;

@RestController
@RequestMapping("/kalah")
public class GameController {
	
	// Poor man's repository. Would never do this in real production code ;)
	private static final Map<UUID, Game> GAME_CACHE = new HashMap<>();
	
	@PostMapping
	@ResponseStatus(org.springframework.http.HttpStatus.CREATED)
	public Game newGame(@RequestBody Game newGame) {
		newGame.start();
		GAME_CACHE.put(newGame.getId(), newGame);
		return newGame;
	}
	
	@GetMapping("/{gameId}/board")
	public Board getBoard(@PathVariable UUID gameId) {
		Game game = GAME_CACHE.get(gameId);
		Board board = game.getBoard();
		return board;
	}

	@PostMapping("/{gameId}/board/{pit}")
	@ResponseStatus(org.springframework.http.HttpStatus.CREATED)
	public ResponseEntity<Game> makeMove(@PathVariable("gameId") UUID gameId, @PathVariable("pit") int pit, @RequestBody Player player) {
		Game game = GAME_CACHE.get(gameId);
		if (game.isGameOver()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		game.move(pit, player);
		return ResponseEntity.status(HttpStatus.CREATED).body(game);
	}

}
