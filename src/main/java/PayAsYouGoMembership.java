import java.time.LocalDate;

/**
 * Class representing a pay as you go ValleyBike membership
 */
public class PayAsYouGoMembership extends Membership {

    /**
     * constructor uses abstract class constructor
     */
    PayAsYouGoMembership() {
        super();
    }

    /**
     * this is the constructor for an existing pay as you go membership
     * @param totalRidesLeft is the totalRidesLeft, which cannot be exceeded for pay per ride
     * @param lastPayment is the last time a payment was made by the user
     * @param memberSince is the date since which the user has had a pay per ride membership
     */
    PayAsYouGoMembership(int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
        super(totalRidesLeft, lastPayment, memberSince);
    }

    /**
     * This is a getter method for the int which represents PAYG membership
     * @return 1 since that is the int for pay as you go
     */
    public int getMembershipInt(){
        return 1;
    }

    /**
     * This is a getter method for the string associated with PAYG membership
     * @return the string name for this membership type
     */
    public String getMembershipString(){
        return "Pay Per Ride";
    }
}
