import java.util.Random;

public class Account {
    /** This field is the unique user id for the customer */
    private int userId;

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
        this.userId = getRandomInteger();
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
    }

    /**
     * This method generates a random 6 digit integer
     * @return the random 6 digit integer generated
     */
    private Integer getRandomInteger() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return Integer.parseInt(String.format("%06d", number));
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
}
