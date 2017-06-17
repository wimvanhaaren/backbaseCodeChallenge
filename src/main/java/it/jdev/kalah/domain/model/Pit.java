package it.jdev.kalah.domain.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;

@Data
@JsonTypeInfo(use=Id.MINIMAL_CLASS, include=As.PROPERTY, property="pitType")
public abstract class Pit {
	
	@JsonIgnore
	private UUID id = UUID.randomUUID();
	
	protected int numberOfStones = 0;
	
	public void addStone() {
		numberOfStones++;
	}
	
	public int takeAllStones() {
		int stones = numberOfStones;
		numberOfStones = 0;
		return stones;
	}

	public void addStones(int allStones) {
		this.numberOfStones += allStones;
	}

}
