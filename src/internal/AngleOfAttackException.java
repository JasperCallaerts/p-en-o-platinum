package internal;

/**
 * Created by Martijn on 18/10/2017.
 * @author Martijn Sauwens
 */
public class AngleOfAttackException extends IllegalStateException {

    /**
     * Constructor for the angle of attack exception
     * @param causeMessage the message to specify the cause
     * @param causeWing the wing that caused the exception
     */
    public AngleOfAttackException(String causeMessage, Wing causeWing){
        this.causeMessage = causeMessage;
        this.causeWing = causeWing;
    }

    /**
     * Constructor for the angle of attack exception
     * @param causeMessage a message to specify the cause
     */
    public AngleOfAttackException(String causeMessage){
        this(causeMessage, null);
    }

    /**
     * Constructor for the angle of attack exception
     * @param causeWing the wing that caused the exception
     */
    public AngleOfAttackException(Wing causeWing){
        this(null, causeWing);
    }

    /**
     * Getter for the cause message
     * @return a string containing the error message
     */
    public String getCauseMessage(){
        return this.causeMessage;
    }

    /**
     * Getter for the wing that caused the exception
     * @return a wing object containing the wing that caused the exception
     */
    public Wing getCauseWing(){
        return this.causeWing;
    }


    /**
     * Variable that stores the wing that caused the exception
     */
    private Wing causeWing;

    /**
     * variable that contains the message of the exception
     */
    private String causeMessage;

}
