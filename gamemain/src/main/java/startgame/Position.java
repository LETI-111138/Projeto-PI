package startgame;

public class Position {
    float x;
    float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPosition(Position pos) {
        this.x = pos.x;
        this.y = pos.y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void addX(float px){
     x +=px;
    }

    public void addY(float pY){
        y +=pY;
    }

    public void rmX(float px){
        x -=px;
    }

    public void rmY(float pY){
        y -=pY;
    }



    public void addPosition(Position pAdd){
        x+=pAdd.x;
        y+=pAdd.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

}
