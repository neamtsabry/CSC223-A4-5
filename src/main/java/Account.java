abstract class Account {

    /** This field is the username for the customer */
    private String username;

    /** This field is the password for the customer */
    private String password;

    /** This field is the email address of the customer */
    private String emailAddress;

    /**
     * This is the constructor to create a new account
     * @param username is the username chosen by the user
     * @param password is the password chosen by the user for login
     * @param emailAddress is the email address associated with the user's account
     */
    Account(String username, String password, String emailAddress) {
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
    }

    /**
     * This is the setter method to update username
     * @param username is the new username input by the user
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * This is the setter method to update password
     * @param password is the new password input by the user
     */
    void setPassword(String password) {
        this.password = password;
    }

    /**
     * This is the setter method to update email address
     * @param emailAddress is the new email address input by the user
     */
    void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * This is the getter method to access the username associated with the account object
     * @return the username associated with the account object
     */
    String getUsername() {
        return username;
    }

    /**
     * This is the getter method to access the password associated with the account object
     * @return the password associated with the account object
     */
    String getPassword() {
        return password;
    }

    /**
     * This is the getter method to access the email address associated with the account object
     * @return the email address associated with the account object
     */
    String getEmailAddress() {
        return emailAddress;
    }
}
