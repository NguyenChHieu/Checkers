package Checkers;

public class Piece extends Cells {
    private char type;
    private boolean isKing = false;

    public Piece(char type) {
        super(type);
    }

    public boolean getIsKing(){
        return isKing;
    }
    public void setKing() {
        isKing = true;
    }
}
