package it.jdev.kalah.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegularPit extends Pit {
	
	public RegularPit() {}
	
	public RegularPit(int stones) {
		super.numberOfStones = stones;
	}

}
