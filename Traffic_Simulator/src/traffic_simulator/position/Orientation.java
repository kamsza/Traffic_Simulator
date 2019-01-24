package traffic_simulator.position;

public enum Orientation {
    VERTICAL , HORIZONTAL;

    public String toString() {
        switch(this) {
            case VERTICAL: return "VERTICAL";
            case HORIZONTAL: return "HORIZONTAL";
            default: throw new IllegalArgumentException("Orientation not found in toSting() method");
        }
    }

    public Orientation changeOrientation() {
        switch(this) {
            case VERTICAL: return HORIZONTAL;
            case HORIZONTAL: return VERTICAL;
            default: throw new IllegalArgumentException("Orientation not found in toSting() method");
        }
    }

    public boolean thisOrientacion(Direction d){
        switch(d) {
            case UP:
            case DOWN:
                if(this == Orientation.VERTICAL) return true;
                return false;
            case RIGHT:
            case LEFT:
                if(this == Orientation.HORIZONTAL) return true;
                return false;
            default:
                throw new IllegalArgumentException("Direction not found");
        }
    }

}