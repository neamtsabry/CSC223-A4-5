import java.time.LocalDate;

/**
 * abstract class Membership that PAYG, Monthly, and Yearly memberships will extend
 * Allows for less redundant code between membership classes
 */
public abstract class Membership {

    /** total rides left that come included with membership */
    private int totalRidesLeft;

    /** date the last payment was made */
    private LocalDate lastPayment;

    /** date when customer began current membership subscription */
    private LocalDate memberSince;

    /**
     * constructor for membership
     * default number of rides left is 0
     */
    Membership() {
        this.totalRidesLeft = 0;
        this.lastPayment = LocalDate.now();
        this.memberSince = LocalDate.now();
    }

    /**
     * This constructor is used for existing memberships associated with existing customer accounts
     * @param totalRidesLeft is the rides left for the membership associated with a customer
     * @param lastPayment is the date for the last time the customer made a payment for their membership
     * @param memberSince is the date for when the customer signed up for a membership of a given type
     */
    Membership(int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
        this.totalRidesLeft = totalRidesLeft;
        this.lastPayment = lastPayment;
        this.memberSince = memberSince;
    }

    /**
     * This is the getter method for the total number of rides left for a membership
     * @return the total rides left
     */
    int getTotalRidesLeft() {
        return totalRidesLeft;
    }

    /**
     * sets new total rides left amount for a membership
     * @param totalRidesLeft is the new total rides left
     */
    void setTotalRidesLeft(int totalRidesLeft) {
        this.totalRidesLeft = totalRidesLeft;
    }

    /**
     * This is the getter method for the last time a user made a payment
     * @return the last payment date
     */
    LocalDate getLastPayment() {
        return lastPayment;
    }

    /**
     * sets new date of last payment for a membership
     * @param lastPayment is the new last payment date
     */
    void setLastPayment(LocalDate lastPayment) {
        this.lastPayment = lastPayment;
    }

    /**
     * This is the getter method for the date since which the user had a membership of this type
     * @return date since which the user had a membership of this type
     */
    LocalDate getMemberSince() {
        return memberSince;
    }

    /**
     * sets date when user began current membership subscription
     * @param memberSince is the new date when user began current membership subscription
     */
    void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }

    /**
     * This is the getter method for which membership type user has
     * @return int which represents which membership type user has
     */
    public int getMembershipInt(){
        return 0;
    }

    /**
     * check whether a new payment is due
     * @return the default which is false
     */
    public boolean checkPaymentDue(){
        return false;
    }

    /**
     * check whether user has exceeded number of free rides
     * @return the default which is false
     */
    public boolean checkMaxRidesExceeded() {
        return false;
    }

    /**
     * This method returns the string that is equivalent to membership type, eg. monthly for int 2
     * @return the default which is null
     */
    public String getMembershipString(){
        return null;
    }

}
