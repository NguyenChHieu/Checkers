package Checkers;

import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.ArrayList;

public class App extends PApplet {

    public static final int CELLSIZE = 96;
    public static final int SIDEBAR = 0;
    public static final int BOARD_WIDTH = 8;
    public static final int[] BLACK_RGB = {181, 136, 99};
    public static final int[] WHITE_RGB = {240, 217, 181};
    public static final float[][][] coloursRGB = new float[][][] {
        //default - white & black
        {
                {WHITE_RGB[0], WHITE_RGB[1], WHITE_RGB[2]},
                {BLACK_RGB[0], BLACK_RGB[1], BLACK_RGB[2]}
        },
        //green
        {
                {105, 138, 76}, //when on white cell
                {105, 138, 76} //when on black cell
        },
        //blue
        {
                {196,224,232},
                {170,210,221}
        }
	};
    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static final int TILE_SIZE = WIDTH / BOARD_WIDTH;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE;
    public static final int FPS = 60;
    public static Cells [][] board;

    public static ArrayList<PossibleCells> possibleMoves;
    public App() {

    }

	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

	@Override
    public void setup() {
        frameRate(FPS);

        //Set up the data structures used for storing data in the game

        // setting up the board and pieces
        CheckersLogic.initialiseBoard();
        board = CheckersLogic.getBoard();
    }

    /**
     * Receive key pressed signal from the keyboard.
    */
	@Override
    public void keyPressed(){

    }

    /**
     * Receive key released signal from the keyboard.
    */
	@Override
    public void keyReleased(){

    }

    int selectedR = -1, selectedC = -1;
    boolean pieceSelected = false;
    boolean win = false;
    @Override
    public void mousePressed(MouseEvent e) {
        // stop receiving mouse actions after winning
        if (win) {
            return;
        }
        int mouseX = e.getX();
        int mouseY = e.getY();

        // mouse -> board idx
        int col = mouseX / TILE_SIZE;
        int row = mouseY / TILE_SIZE;

        if (col >= 0 && col < BOARD_WIDTH && row >= 0 && row < BOARD_WIDTH) {
            if (!pieceSelected) {
                if (correctPiece(row, col)) {
                    selectedR = row;
                    selectedC = col;
                    pieceSelected = true;
                    possibleMoves = CheckersLogic.possibleMoves(selectedR, selectedC);
                }
            } else {
                // check if user reselect the cell
                if (row == selectedR && col == selectedC) {
                    deselect();
                }
                // user select another r
                else if (correctPiece(row, col)) {
                    selectedR = row;
                    selectedC = col;
                    pieceSelected = true;
                    possibleMoves = CheckersLogic.possibleMoves(selectedR, selectedC);
                }
                // valid move
                else if (CheckersLogic.isValidMove(selectedR, selectedC, row, col)) {
                    CheckersLogic.processMove(selectedR, selectedC, row, col);
                    if (CheckersLogic.getPlayerTurn() == 'b')
                        CheckersLogic.setPlayerTurn('w');
                    else if (CheckersLogic.getPlayerTurn() == 'w')
                        CheckersLogic.setPlayerTurn('b');
                    deselect();
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Draw all elements in the game by current frame.
    */
	@Override
    public void draw() {
        this.noStroke();
        background(180);
        //draw the board
        drawBoard();
        //draw highlighted cells
        highlightSelect();
        drawPieces();
        if(CheckersLogic.isGameOver()) {
            String winMessage = CheckersLogic.getNumPieces('w') == 0 ? "Black wins!" : "White wins!";
            float rectX = (float) App.WIDTH / 2 - textWidth(winMessage) / 2 - 100;
            float rectY = (float) App.HEIGHT / 2 - 25;
            float rectWidth = textWidth(winMessage) + 200;
            float rectHeight = 65;

            fill(255);
            stroke(0);
            strokeWeight(4.0f);
            rect(rectX, rectY, rectWidth, rectHeight);
            fill(0);
            textSize(36.0f);
            textAlign(CENTER, CENTER);
            text(winMessage, (float) App.WIDTH / 2, (float) App.HEIGHT / 2);
            win = true;
        }
    }
    // TOOLS
    private void deselect(){
        pieceSelected = false;
        selectedR = -1;
        selectedC = -1;
    }
    private boolean correctPiece(int row, int col){
        char pT = CheckersLogic.getPlayerTurn();
        if (board[row][col] == null){
            return false;
        }
        return Character.toLowerCase(board[row][col].getType()) == pT;
    }

    // DRAW FUNCTIONS
    public void setFill(int colourCode, int blackOrWhite) {
        this.fill(coloursRGB[colourCode][blackOrWhite][0], coloursRGB[colourCode][blackOrWhite][1], coloursRGB[colourCode][blackOrWhite][2]);
    }
    private void drawBoard(){
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                // Calculate the position of the current cell
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;

                // Set the fill color based on the cell's position (alternating black and white)
                if ((row + col) % 2 == 0) {
                    setFill(0, 0); // White cell
                } else {
                    setFill(0, 1); // Black cell
                }

                // Draw the cell
                rect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }
    }
    private void drawPieces() {
        float borderSize = 0.15f * TILE_SIZE;
        float pieceSize = 0.7f * TILE_SIZE;

        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {

                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;

                // get piece at the cell
                Piece piece = (Piece) board[row][col];

                if (piece != null) {
                    if (piece.getType() == 'b' && piece.getIsKing()){
                        //border
                        fill(255);
                        ellipse(x + (float) TILE_SIZE / 2, y + (float) TILE_SIZE / 2,
                                pieceSize + borderSize, pieceSize + borderSize);
                        //inner1
                        fill(0);
                        ellipse(x + (float) TILE_SIZE / 2, y +(float) TILE_SIZE / 2,
                                pieceSize, pieceSize);
                        //inner2
                        fill(255);
                        ellipse(x + (float) TILE_SIZE / 2, y +(float) TILE_SIZE / 2,
                                (float) (pieceSize * 0.8), (float) (pieceSize * 0.8));
                        // inner3
                        fill(0);
                        ellipse(x + (float) TILE_SIZE / 2, y +(float) TILE_SIZE / 2,
                                (float) (pieceSize * 0.5), (float) (pieceSize * 0.5));
                    }
                    else if (piece.getType() == 'w' && piece.getIsKing()){
                        //border
                        fill(0);
                        ellipse(x + (float) TILE_SIZE / 2, y + (float) TILE_SIZE / 2,
                                pieceSize + borderSize, pieceSize + borderSize);
                        //inner1
                        fill(255);
                        ellipse(x + (float) TILE_SIZE / 2, y +(float) TILE_SIZE / 2,
                                pieceSize, pieceSize);
                        //inner2
                        fill(0);
                        ellipse(x + (float) TILE_SIZE / 2, y +(float) TILE_SIZE / 2,
                                (float) (pieceSize * 0.8), (float) (pieceSize * 0.8));
                        // inner3
                        fill(255);
                        ellipse(x + (float) TILE_SIZE / 2, y +(float) TILE_SIZE / 2,
                                (float) (pieceSize * 0.5), (float) (pieceSize * 0.5));
                    }
                    else if (piece.getType() == 'b') {
                        //border
                        fill(255);
                        ellipse(x + (float) TILE_SIZE / 2, y + (float) TILE_SIZE / 2,
                                pieceSize + borderSize, pieceSize + borderSize);
                        //inner
                        fill(0); // Black
                        ellipse(x + (float) TILE_SIZE / 2, y +(float) TILE_SIZE / 2,
                                pieceSize, pieceSize);
                    }
                    else if (piece.getType() == 'w') {
                        // border
                        fill(0);
                        ellipse(x + (float) TILE_SIZE / 2, y + (float) TILE_SIZE / 2,
                                pieceSize + borderSize, pieceSize + borderSize);
                        // inner
                        fill(255); // White
                        ellipse(x + (float) TILE_SIZE / 2, y+ (float) TILE_SIZE / 2,
                                pieceSize, pieceSize);
                    }
                }
            }
        }
    }
    private void highlightSelect(){
        if (selectedC != -1 && selectedR != -1){
            int x = selectedC * TILE_SIZE;
            int y = selectedR * TILE_SIZE;

            fill(105, 138, 76);

            rect(x,y,TILE_SIZE,TILE_SIZE);

            for (PossibleCells move: possibleMoves){
                int x1 = move.getX() * TILE_SIZE;
                int y1 = move.getY() * TILE_SIZE;

                fill(170,210,221);
                rect(x1,y1,TILE_SIZE,TILE_SIZE);
            }

        }
    }

    public static void main(String[] args) {
        PApplet.main("Checkers.App");
    }


}
