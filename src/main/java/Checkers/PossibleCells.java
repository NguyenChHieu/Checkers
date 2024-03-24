package Checkers;


public class PossibleCells extends Cells{
    private int r;
    private int c;

    public PossibleCells(int X, int Y){
        super('x');
        this.c = X;
        this.r = Y;
    }

    public int getX(){
        return r;
    }

    public int getY(){
        return c;
    }
}
