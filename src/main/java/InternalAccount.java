/**
 * internal account extends abstract account
 * represents an internal account in the system
 */
class InternalAccount extends Account {

    /**
     * This is the constructor to create an internal account
     * @param username is the username for the internal staff account
     * @param password is the password for the internal staff account
     * @param emailAddress is the email address associated with the internal staff account
     */
    InternalAccount(String username, String password, String emailAddress) {
        //internal account does not have any extra fields that the abstract account class does not have
        //internal account inherits all fields from abstract class
        super(username, password, emailAddress);
    }
}
