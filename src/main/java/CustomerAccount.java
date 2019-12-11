import java.util.ArrayList;
import java.util.UUID;

/**
 * customer account extends abstract accound
 * represents a customer account in the system
 */
public class CustomerAccount extends Account{

    /** This field is the credit card number of the customer */
    private String creditCard;

    /** This field is the membership type that the customer is paying for */
    private Membership membership;

    /** This field is the running total of money customer has spent paying for rides */
    private double balance;

    /** This field is the stack of ride ids for rides the user has taken */
    private ArrayList<UUID> rideIdList;

    /** This field is true is user currently has bike rented, and false otherwise */
    private boolean lastRideIsReturned;

    /** This field is false if user deletes account */
    private boolean enabled;

    /**
     * This is the constructor to create a new user account with balance = 0
     * @param username is the username input by the user
     * @param emailAddress is the emailAddress of the user
     * @param password is the password input by the user
     * @param creditCard is the credit card number input by the user
     * @param membership is the membership type that the customer is paying for
     */
    public CustomerAccount(String username, String password, String emailAddress, String creditCard, Membership membership) {
        //username, password and email address same as super class
        super(username, password, emailAddress);
        this.creditCard = creditCard;
        this.membership = membership;
        this.balance = 0;
        this.lastRideIsReturned = true;
        this.enabled = true;
        this.rideIdList = new ArrayList<>();
    }

    /**
     * This is the constructor to create a new object for an existing user account
     * @param username is the username input by the user
     * @param emailAddress is the emailAddress of the user
     * @param password is the password input by the user
     * @param creditCard is the credit card number input by the user
     * @param membership is the membership type that the customer is paying for
     * @param balance is the money that the customer has available in their account
     */
    public CustomerAccount(String username, String password, String emailAddress, String creditCard, Membership membership, double balance, boolean lastRideIsReturned, boolean enabled, ArrayList<UUID> rideIdList) {
        //username, password and email address same as super class
        super(username, password, emailAddress);
        this.creditCard = creditCard;
        this.membership = membership;
        this.balance = balance;
        this.lastRideIsReturned = lastRideIsReturned;
        this.enabled = enabled;
        this.rideIdList = rideIdList;
    }

    /**
     * This is the setter method to update credit card number
     * @param creditCard is the new credit card number entered by the user
     */
    void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * This is the setter method to update membership
     * @param membership is the new membership type entered by the user
     */
    void setMembership(Membership membership) {
        this.membership = membership;
    }

    /**
     * This is the setter method to update balance
     * @param balance is the new balance of the user
     */
    void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * This is the getter method to access membership type associated with the customer account
     * @return the membership type associated with the customer account
     */
    Membership getMembership() {
        return membership;
    }

    /**
     * This is the getter method to access the balance associated with the customer account
     * @return the balance associated with the customer account
     */
    double getBalance() {
        return balance;
    }

    /**
     * This is the getter method to access the credit card number associated with the customer account
     * @return the credit card number associated with the customer account
     */
    String getCreditCard() {
        return creditCard;
    }

    /**
     * This method adds a new ride to the ride list associated with the customer account
     * @param rideID is the ride id for the new ride taken by the user
     */
    void addNewRide(UUID rideID){
        this.rideIdList.add(rideID);
    }

    /**
     * This is the getter method for whether the user has a bike currently rented
     * @return true if user has bike currently rented and false otherwise
     */
    Boolean getIsReturned(){
        return this.lastRideIsReturned;
    }

    /**
     * This is the getter method for ride id list associated with the customer account
     * @return the ride id list associated with the customer account
     */
    ArrayList<UUID> getRideIdList() {
        return rideIdList;
    }

    /**
     * This is the getter method for the most recent ride taken by the user
     * @return the ride id of the most recent ride taken by the user
     */
    UUID getLastRideId(){
        return rideIdList.get(rideIdList.size()- 1);
    }

    /**
     * This is the getter method to check if customer account is active or deleted
     * Deleted accounts cannot be logged into
     * @return true if active and false if deleted
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * This is the setter method to activative/deactivate account
     * Only used to deactivate account
     * @param enabled is the new status (active, deleted) for the account
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method converts the ride id list to a string for the database
     * @return the ride id string, where all the ride ids in the ride id list are seperated by a comma
     */
    public String getRideIdListToString(){
        return rideIdList.toString().replaceAll("\\[", "").replaceAll("\\]","");
    }

    /**
     * This is the setter method for whether the most recent ride has been returned
     * @param lastRideIsReturned true if ride returned, false otherwise
     */
    public void setLastRideIsReturned(boolean lastRideIsReturned) {
        this.lastRideIsReturned = lastRideIsReturned;
    }
}
