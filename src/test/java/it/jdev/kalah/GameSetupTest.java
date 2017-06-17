package it.jdev.kalah;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

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
import it.jdev.kalah.domain.model.KalahPit;
import it.jdev.kalah.domain.model.Pit;
import it.jdev.kalah.domain.model.Player;
import it.jdev.kalah.domain.model.RegularPit;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameSetupTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void newGame() throws Exception {
		Game game = startNewGame();
		assertNotNull(game.getId());
	}

	private Game startNewGame() throws JsonProcessingException, Exception, UnsupportedEncodingException, IOException,
			JsonParseException, JsonMappingException {
		Game game = new Game();
		game.setPlayer1(new Player(1, "David"));
		game.setPlayer2(new Player(2, "Neal"));
		
		ObjectMapper objectMapper = new ObjectMapper();
		String gameJson = objectMapper.writeValueAsString(game);
		MvcResult mvcResult = this.mockMvc
				.perform(post("/kalah").contentType(MediaType.APPLICATION_JSON_UTF8).content(gameJson))
				.andDo(print())
				.andExpect(status().isCreated())
				.andReturn();

		String response = mvcResult.getResponse().getContentAsString();
		Game result = objectMapper.readValue(response, Game.class);
		return result;
	}
	
	@Test
	public void getBoard() throws Exception {
		Game game = startNewGame();
		Board board = getBoard(game.getId());
		assertNotNull(board);
		Pit[] pits = board.getPits();
		assertEquals(14, pits.length);
		for (int i=0; i<14; i++) {
			Pit p = pits[i];
			if (i == 6 || i == 13) {
				assertTrue(p instanceof KalahPit);
				assertEquals(0, p.getNumberOfStones());				
			} else {
				assertTrue(p instanceof RegularPit);
				assertEquals(6, p.getNumberOfStones());
			}
		}
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

}
