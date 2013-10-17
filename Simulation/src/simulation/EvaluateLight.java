package simulation;


/**
 * 
 * @author Diana Carolina Barreto Arias (dianaba@student.unimelb.edu.au)
 */
public class EvaluateLight {
    
    /**
     * 
     * @param current
     * @param previous
     * @return true if the state changed, false if its the same
     */
    public boolean evaluateLight(int current, int previous) {

        int difference = current - previous;
        if(difference <= -12){
            if(current <= 11){return true;}
            else{return false;}
        }else{ 
            if(difference <= 10){return false;}
            else{
                if(previous<=11){return true;}
                else return false;
            }
        }
    }
}