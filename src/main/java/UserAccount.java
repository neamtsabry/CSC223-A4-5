public class UserAccount {
    /** This field is the username for the customer */
    private String username;

    /** This field is the password for the customer */
    private String password;

    /** This field is the credit card number of the customer */
    private String creditcard;

    /**
     * This is the constructor to create a user account
     * @param username is the username input by the user
     * @param password is the password input by the user
     * @param creditcard is the credit card number input by the user
     */
    public UserAccount(String username, String password, String creditcard) {
        this.username = username;
        this.password = password;
        this.creditcard = creditcard;
    }

    /**
     * This is the setter method to update username.
     * @param username is the new username input by the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This is the setter method to update password.
     * Password cannot be updated without entering the oldPassword correctly.
     * @param newPassword is the new password the user wants to set.
     * @param oldPassword is the old password entered by the user for verification.
     */
    public void setPassword(String newPassword, String oldPassword) {
        if (password == oldPassword){
            password = newPassword;
        } else {
            System.out.println("Incorrect password. Cannot change password.");
        }
    }

    /**
     * This is the setter method to update credit card numner.
     * @param creditcard is the new credit card number entered by the user.
     */
    public void setCreditcard(String creditcard) {
        this.creditcard = creditcard;
    }


}
