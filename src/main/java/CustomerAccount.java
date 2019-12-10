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

    /** This field is the balance of the customer in their account */
    //TODO make balance double
    private int balance;

    // stack of ride ids user has
    private ArrayList<UUID> rideIdList;

    private boolean lastRideIsReturned;

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
    public CustomerAccount(String username, String password, String emailAddress, String creditCard, Membership membership, int balance, boolean lastRideIsReturned, boolean enabled, ArrayList<UUID> rideIdList) {
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
    void setBalance(int balance) {
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
    int getBalance() {
        return balance;
    }

    /**
     * This is the getter method to access the credit card number associated with the customer account
     * @return the credit card number associated with the customer account
     */
    String getCreditCard() {
        return creditCard;
    }

    void addNewRide(UUID rideID){
        this.rideIdList.add(rideID);
    }

    Boolean getIsReturned(){
        return this.lastRideIsReturned;
    }

    ArrayList<UUID> getRideIdList() {
        return rideIdList;
    }

    UUID getLastRideId(){
        return rideIdList.get(rideIdList.size()- 1);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRideIdListToString(){
        return rideIdList.toString().replaceAll("\\[", "").replaceAll("\\]","");
    }

    public void setLastRideIsReturned(boolean lastRideIsReturned) {
        this.lastRideIsReturned = lastRideIsReturned;
    }
}
