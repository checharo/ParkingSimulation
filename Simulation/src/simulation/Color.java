
package simulation;

/**
 * Needed our own Color class to avoid problems with J2ME
 * @author César Martínez D'Granda (cesarm@student.unimelb.edu.au)
 */
public class Color {
    private int R;
    private int G;
    private int B;
    
    public final static Color RED = new Color(255, 0, 0);
    public final static Color GREEN = new Color(0, 255, 0);

    public Color (int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }
    /**
     * @return the R
     */
    public int getR() {
        return R;
    }

    /**
     * @param R the R to set
     */
    public void setR(int R) {
        this.R = R;
    }

    /**
     * @return the G
     */
    public int getG() {
        return G;
    }

    /**
     * @param G the G to set
     */
    public void setG(int G) {
        this.G = G;
    }

    /**
     * @return the B
     */
    public int getB() {
        return B;
    }

    /**
     * @param B the B to set
     */
    public void setB(int B) {
        this.B = B;
    }
    
    
}
