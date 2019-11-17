import java.util.Random;

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
     *
     * @param emailAddress
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }
}
