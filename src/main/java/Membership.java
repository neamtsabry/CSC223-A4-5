import java.time.LocalDate;

/**
 * abstract class Membership that PAYG, Monthly, and Yearly memberships will extend
 * Allows for less redundant code between membership classes
 */
public abstract class Membership {

    //total rides left that come included with membership
    private int totalRidesLeft;

    //date the last payment was made
    private LocalDate lastPayment;

    //date when customer began current membership subscription
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

    Membership(int totalRidesLeft, LocalDate lastPayment, LocalDate memberSince){
        this.totalRidesLeft = totalRidesLeft;
        this.lastPayment = lastPayment;
        this.memberSince = memberSince;
    }

    /**
     * returns total number of rides left
     */
    int getTotalRidesLeft() {
        return totalRidesLeft;
    }

    /**
     * sets new total rides left amount
     */
    void setTotalRidesLeft(int totalRidesLeft) {
        this.totalRidesLeft = totalRidesLeft;
    }

    /**
     * returns date of last payment
     */
    LocalDate getLastPayment() {
        return lastPayment;
    }

    /**
     * sets new date of last payment
     */
    void setLastPayment(LocalDate lastPayment) {
        this.lastPayment = lastPayment;
    }

    /**
     * returns when user began current membership subscription
     */
    LocalDate getMemberSince() {
        return memberSince;
    }

    /**
     * sets date when user began current membership subscription
     */
    void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }

    /**
     * returns int which represents which membership type user has
     */
    public int getMembershipInt(){
        return 0;
    }

    /**
     * check whether a new payment is due
     * default is false
     */
    public boolean checkPaymentDue(){
        return false;
    }

    /**
     * check whether user has exceeded number of free rides
     * default is false
     */
    public boolean checkMaxRidesExceeded() {
        return false;
    }

    /**
     * returns a string with membership type
     * default is null
     */
    public String getMembershipString(){
        return null;
    }

}
