public abstract class Account {

    /** This field is the username for the customer */
    private String username;

    /** This field is the password for the customer */
    private String password;

    /** This field is the email address of the customer */
    private String emailAddress;

    /**
     *
     * @param username
     * @param password
     * @param emailAddress
     */
    public Account(String username, String password, String emailAddress) {
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
    }

    /**
     * This is the setter method to update username.
     * @param username is the new username input by the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @param emailAddress
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
