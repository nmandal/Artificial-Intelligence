public class studentAI extends Player {
    private int maxDepth;

    /**
     * This default constructor will create an AI player that can perform moves 
     * based on alpha beta pruning
     * @param maxDepth the maximum depth of the search tree that will be explored
     */
    public studentAI() {
    }
    
    public void setMaxDepth(int depth) {
    	this.maxDepth = depth;
    	
    }
    
    /**
     * This is a wrapper function for Alpha-beta search. 
     * It should use the alpha beta search to update the data member move 
     * (which will be returned by the getMove() method to the class that is 
     * controlling the game environment). There is a 20-second time limit to 
     * calculate the move.
     */
    public void move(BoardState state) {
    	move = alphabetaSearch(state, maxDepth);
    }

    /**
     *This function will start the Alpha-beta search 
     * @param state The board state for the current player (the MAX player). 
     * You can assume the houses for the current player are always in the lower row, 
     * and that the lower row is player 1.
     * @return Return the best move that leads to the state that gives the maximum SBE 
     * value for the current player; return the move with the smallest index 
     * in the case of ties.     
     */
    public int alphabetaSearch(BoardState state, int maxDepth) {
    	int alpha = Integer.MIN_VALUE;
    	int beta = Integer.MAX_VALUE;
    	int v = Integer.MIN_VALUE;
    	int bestMove = 0;
		for(int i = 0; i < 6; i++) {
			if(state.isLegalMove(1, i)) {
				v = Math.max(v, minValue(state.applyMove(1, i), maxDepth, maxDepth-1, alpha, beta));
				if(v > alpha) {
					alpha = v;
					bestMove = i;
				}
				if(beta <= alpha) 
					break;
			}
		}

		return bestMove;
    }

    /**
     * This function will search the best SBE value for the MAX player. 
     * The search should be cut off when the current depth equals to the maximum 
     * allowed depth (which is specified by the data member maxDepth). 
     * It is important to note that we will also call the SBE function to evaluate the 
     * game state when the game is over, i.e., when someone has won the game. 
	 * @param state The game state the MAX player is currently searching 
	 * @param currentDepth The current depth of the search tree
	 * @param alpha The α value
	 * @param beta The β value
	 * @return The best possible SBE value that the MAX player can achieve
     */
    public int maxValue(BoardState state, int maxDepth, int currentDepth, int alpha, int beta) {
    	if(currentDepth == 0) {
    		return sbe(state);
    	}
    	int v = Integer.MIN_VALUE;
		for(int i = 0; i < 6; i++) {
			if(state.isLegalMove(1, i)) {
				v = Math.max(v, minValue(state.applyMove(1, i), maxDepth, currentDepth-1, alpha, beta));
				alpha = Math.max(alpha, v);
				if(beta <= alpha) 
					break;
			}
		}
		
		// no legal moves means this is a leaf node
		if (v == Integer.MIN_VALUE)
			return sbe(state);
		return v;
    }

    /**
     * This function will search the best move for the MIN player. 
     * The search should be cut off when the current depth equals to the maximum 
     * allowed depth (which is specified by the data member maxDepth). 
     * It is important to note that we will also call the SBE function to evaluate the 
     * game state when the game is over, i.e., when someone has won the game. 
	 * @param state The game state the MIN player is currently searching 
	 * @param currentDepth The current depth of the search tree
	 * @param alpha The α value
	 * @param beta The β value
	 * @return The best possible SBE value that the MIN player can achieve
     */
    public int minValue(BoardState state, int maxDepth, int currentDepth, int alpha, int beta) {
    	if(currentDepth == 0) {
    		return sbe(state);
    	}

		int v = Integer.MAX_VALUE;
		for(int i = 0; i < 6; i++) {
			if(state.isLegalMove(2, i)) {
				v = Math.min(v, maxValue(state.applyMove(2, i), maxDepth, currentDepth-1, alpha, beta));
				beta = Math.min(beta, v);
				if(beta <= alpha)
					break;
			}
		}
		
		// no legal moves means this is a leaf node
		if (v == Integer.MAX_VALUE)
			return sbe(state);
		return v;
    }

    /**
     * This function takes a board state as input and returns its SBE value. 
     * Use the following method: Return the number of stones in the storehouse of the 
     * current player minus the number of stones in the opponent’s storehouse.  
     * Always assume that the current player is the player 1, the one in the lower row, 
     * and the opponent's player is player 2, the one in the upper house.  
     * @param state
     * @return the number of stones in the storehouse of the 
     * current player minus the number of stones in the opponent’s storehouse.
     */
    public int sbe(BoardState state){
    	return state.score[0] - state.score[1];
    }


}

