public class CustomerAccount extends Account{

    /** This field is the credit card number of the customer */
    private String creditCard;

    /** This field is the membership type that the customer is paying for */
    private String membership;

    /** This field is the balance of the customer in their account */
    private int balance;

    /**
     * This is the constructor to create a new user account with balance = 0
     * @param username is the username input by the user
     * @param emailAddress is the emailAddress of the user
     * @param password is the password input by the user
     * @param creditCard is the credit card number input by the user
     * @param membership is the membership type that the customer is paying for
     */
    public CustomerAccount(String username, String password, String emailAddress, String creditCard, String membership) {
        super(username, password, emailAddress);
        this.creditCard = creditCard;
        this.membership = membership;
        this.balance = 0;
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
    public CustomerAccount(String username, String password, String emailAddress, String creditCard, String membership, int balance) {
        super(username, password, emailAddress);
        this.creditCard = creditCard;
        this.membership = membership;
        this.balance = balance;
    }

    /**
     * This is the setter method to update credit card numner.
     * @param creditCard is the new credit card number entered by the user.
     */
    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getMembership() {
        return membership;
    }

    public int getBalance() {
        return balance;
    }
}
