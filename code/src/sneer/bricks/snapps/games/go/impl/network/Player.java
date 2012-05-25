package sneer.bricks.snapps.games.go.impl.network;

import sneer.bricks.snapps.games.go.impl.logic.Move;


public interface Player {

	void receivePlay(Move move);
	void setAdversary(Player player);
 
}
