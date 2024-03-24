package Checkers;

import java.util.*;
public class CheckersLogic {
    private static Cells[][] board = new Cells[8][8];
    private static short whitePieces = 12;
    private static short blackPieces = 12;

    private static char playerTurn = 'w'; // w or b

    public static Cells[][] getBoard(){
        return board;
    }

    // GAME SETUP
    private static void initialPieces(boolean isWhite) {
        int startRow = isWhite ? 0 : 5; // black : white
        int endRow = isWhite ? 3 : 8;
        int offset;

        // assign black/white
        for (int currRow = startRow; currRow < endRow; currRow++) {
            if (currRow % 2 == 0)
                offset = 1;
            else
                offset = 0;

            for (int curr = offset; curr < 8; curr += 2) {
                Piece pieceColor = isWhite ? new Piece('w') : new Piece('b');
                board[currRow][curr] = pieceColor;
            }
            for (int curr = 1 - offset; curr < 8; curr += 2){
                board[currRow][curr] = null;
            }
        }

        // assign middle lines
        for (int currRow = 3; currRow < 5; currRow++) {
            for (int curr = 0; curr < 8; curr ++) {
                board[currRow][curr] = null;
            }
        }
    }
    public static void initialiseBoard() {
        // Implement this method to fill the board array
        initialPieces(false);
        initialPieces(true);
    }

    // GAME INTERIORS
    private static boolean isKing(int toRow){
        if (playerTurn == 'b' && toRow == 0)
            return true;
        else return playerTurn == 'w' && toRow == 7;
    }
    public static boolean processMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (isValidMove(fromRow,fromCol,toRow,toCol)) {
            // normal move
            if (Math.abs(fromCol-toCol) == 1){
                board[toRow][toCol] = board[fromRow][fromCol];
                board[fromRow][fromCol] = null;

                //king
                if(isKing(toRow)){
                    ((Piece) board[toRow][toCol]).setKing();
                }
                return true;
            }
            // jumps
            else if (Math.abs(fromCol - toCol) == 2){
                int midCol = (fromCol + toCol)/2;
                int midRow = (fromRow + toRow)/2;
                Cells jumpedPiece = board[midRow][midCol];
                Cells piece = board[fromRow][fromCol];

                // king move
                board[toRow][toCol] = board[fromRow][fromCol];
                board[fromRow][fromCol] = null;

                if (piece.getType() == 'w'){
                    if (jumpedPiece.getType() == 'b') {
                        board[midRow][midCol] = null;
                        blackPieces--;
                    }
                }
                else if (piece.getType() == 'b'){
                    if (jumpedPiece.getType() == 'w') {
                        board[midRow][midCol] = null;
                        whitePieces--;
                    }
                }

                if(isKing(toRow)){
                    ((Piece) board[toRow][toCol]).setKing();
                }
                return true;
            }
        }
        else
            System.out.println("Error!\n");
        return false;
    }
    // Handle invalid moves
    public static boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (board[fromRow][fromCol].getType() == playerTurn && board[toRow][toCol] == null){

            // Move king
            // either black or white rows will change by 1
            if (((Piece)board[fromRow][fromCol]).getIsKing()){
                if (Math.abs(fromCol-toCol) == 1){
                    if (Math.abs(fromRow-toRow) == 1){
                        return true;
                    }
                }
                else if (Math.abs(fromCol-toCol) == 2){
                    int midCol = (fromCol + toCol)/2;
                    int midRow = (fromRow + toRow)/2;

                    if (Math.abs(fromRow-toRow) == 2){ // check if valid des
                        if (board[midRow][midCol]!= null) {
                            return (board[midRow][midCol].getType() == 'w'
                                    || board[midRow][midCol].getType() == 'b');
                        }
                        else return false;
                    }
                }
            }
            // normal
            if (Math.abs(fromCol-toCol) == 1){ // move diagonal
                // move down
                if (playerTurn == 'b' && fromRow-toRow == 1){ // move up
                    return true;
                }
                else return playerTurn == 'w' && fromRow - toRow == -1;
            }
            // check valid capture (jump)
            else if (Math.abs(fromCol-toCol) == 2){
                int midCol = (fromCol + toCol)/2;
                int midRow = (fromRow + toRow)/2;

                if (playerTurn == 'b' && fromRow-toRow == 2){ // check if valid des
                    if (board[midRow][midCol] != null) {
                        return (board[midRow][midCol].getType() == 'w'
                                || board[midRow][midCol].getType() == 'b');
                    } else return false;

                }
                if (playerTurn == 'w' && fromRow-toRow == - 2){
                    if (board[midRow][midCol] != null) {
                        return (board[midRow][midCol].getType() == 'b'
                                || board[midRow][midCol].getType() == 'w');
                    } else return false;
                }
            }
        }
        // not jump, not move
        return false;
    }

    public static ArrayList<PossibleCells> possibleMoves(int fromRow, int fromCol){
        int rowStart = (fromRow - 2 > -1) ? fromRow - 2 : 0;
        int rowEnd = Math.min(fromRow + 2, 7);

        int colStart = (fromCol - 2 >-1)? fromCol - 2: 0;
        int colEnd = Math.min(fromCol + 2, 7);
        ArrayList<PossibleCells> possible = new ArrayList<>();

        for (int r = rowStart; r <= rowEnd ; r++){
            for(int c = colStart; c <= colEnd; c++){
                if (isValidMove(fromRow, fromCol, r, c)) {
                    possible.add(new PossibleCells(r, c));
                }
            }
        }
        return possible;
    }

    // GAME MANAGER
    public static char getPlayerTurn(){
        return  playerTurn;
    }
    public static void setPlayerTurn(char turn) {
        if (turn == 'b')
            playerTurn = 'b';
        else if (turn == 'w')
            playerTurn = 'w';
    }
    public static int getNumPieces(char type){
        if (type == 'b'){
            return blackPieces;
        } else if (type == 'w') {
            return whitePieces;
        }
        return -1;
    }
    public static boolean isGameOver() {
        // Implement this method to check for win conditions.
        if (blackPieces == 0 || whitePieces == 0)
            return true;
        return false;
    }
}
