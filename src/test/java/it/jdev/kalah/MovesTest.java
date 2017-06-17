package it.jdev.kalah;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.jdev.kalah.api.GameController;
import it.jdev.kalah.domain.model.Board;
import it.jdev.kalah.domain.model.Game;
import it.jdev.kalah.domain.model.Player;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class MovesTest {
	
	@Autowired
	private MockMvc mockMvc;

	private Game game;
	private Player player1 = new Player(1, "David");
	private Player player2 = new Player(2, "Neal");
	
	@Before
	public void setUp() throws Exception {
		this.game = startNewGame();
	}

	private Game startNewGame() throws JsonProcessingException, Exception, UnsupportedEncodingException, IOException,
			JsonParseException, JsonMappingException {
		Game game = new Game();
		game.setPlayer1(player1);
		game.setPlayer2(player2);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String gameJson = objectMapper.writeValueAsString(game);
		MvcResult mvcResult = this.mockMvc
				.perform(post("/kalah").contentType(MediaType.APPLICATION_JSON_UTF8).content(gameJson))
				.andDo(print())
				.andExpect(status().isCreated())
				.andReturn();

		String response = mvcResult.getResponse().getContentAsString();
		return objectMapper.readValue(response, Game.class);
	}

	private Board getBoard(UUID gameId) throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/kalah/" + gameId + "/board")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn();
		String response = mvcResult.getResponse().getContentAsString();
		ObjectMapper objectMapper = new ObjectMapper();
		Board board = objectMapper.readValue(response, Board.class);
		return board;
	}


	@Test
	public void shouldDistributeStoneCorrectlyAndTurnOverToTheOtherPlayer() throws Exception {
		int pit = 1;
		Game game = makeMove(pit, player1);
		
		Board board = getBoard(game.getId());

		assertEquals(6, board.getPits()[0].getNumberOfStones());
		assertEquals(0, board.getPits()[1].getNumberOfStones());
		assertEquals(7, board.getPits()[2].getNumberOfStones());
		assertEquals(7, board.getPits()[3].getNumberOfStones());
		assertEquals(7, board.getPits()[4].getNumberOfStones());
		assertEquals(7, board.getPits()[5].getNumberOfStones());
		assertEquals(1, board.getPits()[6].getNumberOfStones());
		assertEquals(7, board.getPits()[7].getNumberOfStones());
		assertEquals(6, board.getPits()[8].getNumberOfStones());
		assertEquals(player2, game.getCurrentPlayer()); // Other player's turn
	}
	
	
	@Test
	public void playerShouldGetAnotherTurnIfTheLastStoneLandsInHisOwnInKalahPit() throws Exception {
		int pit = 0;
		Game game = makeMove(pit, player1);
		
		Board board = getBoard(game.getId());

		assertEquals(0, board.getPits()[0].getNumberOfStones());
		assertEquals(7, board.getPits()[1].getNumberOfStones());
		assertEquals(7, board.getPits()[2].getNumberOfStones());
		assertEquals(7, board.getPits()[3].getNumberOfStones());
		assertEquals(7, board.getPits()[4].getNumberOfStones());
		assertEquals(7, board.getPits()[5].getNumberOfStones());
		assertEquals(1, board.getPits()[6].getNumberOfStones());
		assertEquals(6, board.getPits()[7].getNumberOfStones());
		assertEquals(player1, game.getCurrentPlayer()); // Last stone in own kalah pit so player is still on...
	}

	@Test
	public void shouldNotBeAbleToPutAStoneInOtherPlayersKalahPit() throws Exception {
		Game game = makeMove(0, player1);
		game = makeMove(2, player1);
		game = makeMove(7, player2);
		game = makeMove(5, player1);
		
		Board board = getBoard(game.getId());

		assertEquals(2, board.getPits()[0].getNumberOfStones());
		assertEquals(1, board.getPits()[13].getNumberOfStones());

	}
	
	private Game makeMove(int pit, Player player) throws Exception {
		MvcResult mvcResult = this.mockMvc
				.perform(post("/kalah/" + this.game.getId() + "/board/" + pit)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(toJson(player)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andReturn();
		
		String response = mvcResult.getResponse().getContentAsString();
		ObjectMapper objectMapper = new ObjectMapper();
		Game game = objectMapper.readValue(response, Game.class);
				return game;
	}

	private String toJson(Player player) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(player);
	}

	@Test
	public void shouldNotBeAbleToStartFromOtherPlayersPit() throws Exception {
		this.mockMvc
				.perform(post("/kalah/" + this.game.getId() + "/board/9")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(toJson(player1)))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

	@Test
	public void playerShouldNotBeAbleToMakeAMoveWhenItsNotHisTurn() throws Exception {
		this.mockMvc
				.perform(post("/kalah/" + this.game.getId() + "/board/9")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(toJson(player2)))
				.andDo(print())
				.andExpect(status().isForbidden());
	}

}
