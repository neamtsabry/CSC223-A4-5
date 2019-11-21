import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class ValleyBikeController {

    private static Scanner input = new Scanner(System.in);

    /**
     * Basic option menu that shows at start of program and when no one is logged in
     */
    static void initialMenu() throws IOException, ParseException {
        System.out.print("\n Welcome to ValleyBike Share! \n"
                + "1. Create User Account\n"
                + "2. Log In\n"
                + "0. Exit program\n");
        System.out.println("Please enter your selection (0-2):");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input");
            initialMenu();
        }

        Integer num = input.nextInt();
        switch(num) {
            case 1:
                createAccount();
                break;
            case 2:
                logIn();
                break;
            case 0:
                input.close();

                // save bike and station data
                ValleyBikeSim.saveBikeList();
                ValleyBikeSim.saveStationList();

                System.exit(0);
                break;
        }
    }

    //Do we want a separate method for creating user vs internal accounts? This one is User
    /**
     * Method for a customer to create an account
     */
    private static void createAccount() throws IOException, ParseException {
        //TODO store account
        //TODO membership types
        //TODO check for unique username

        System.out.println("Enter username (must be between 6-14 characters):");
        String username = input.nextLine();
        if (!isValidUsername(username)){
            System.out.println("Username is not valid.");
            return;
        }

        System.out.println("Enter password (must be between 6-14 characters):");
        String password = input.nextLine();
        if (!isValidPassword(password)){
            System.out.println("Password is not valid.");
            return;
        }

        System.out.println("Enter email address:");
        String emailAddress = input.nextLine();
        if (!isValidEmail(emailAddress)){
            System.out.println("Email address is not valid.");
            return;
        }

        String creditCard = input.nextLine();
        System.out.println("Enter credit card number:");
        if (!isValidCreditCard(creditCard)){
            System.out.println("Credit card is not valid.");
            return;
        }

        String membership = input.nextLine();
        System.out.println("Enter membership type:");

        //create new customer account
        Account account = new CustomerAccount(username, password, emailAddress, creditCard, membership);

        //go to account menu
        String user = account.getUsername();
        userAccountHome(user);
    }

    private static boolean isValidUsername(String username){
        if (username.length() >= 6 && username.length() <= 14){
            return true;
        }
        return false;
    }

    private static boolean isValidPassword(String password){
        if (password.length() >= 6 && password.length() <= 14){
            return true;
        }
        return false;
    }

    private static boolean isValidCreditCard(String creditCard){
        if(Math.random() <= 0.95) {
            return true;
        }
        return false;
    }

    /**
     * This method checks if the email address input by the user is valid
     * @param emailAddress is the email address input by the user
     * @return returns boolean true if email address is valid and false otherwise
     */
    private static boolean isValidEmail(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (emailAddress == null)
            return false;
        return pat.matcher(emailAddress).matches();
    }

    /**
     * Method for both internal and user accounts to log in
     */
    private static void logIn() throws IOException, ParseException {
        //TODO input username, authenticate
        //TODO input password, authenticate
        //TODO figure out if account is customer or internal
        //if account is user, go to user menu:
        System.out.println("Enter username:");
        String username = input.nextLine();

        System.out.println("Enter password:");
        String password = input.nextLine();

        userAccountHome(username);
        //if account is employee, go to employee menu:
        internalAccount();
    }


    /**
     * Standard menu page for a user after logging in
     * @param username: integer representing unique userID of account
     */
    private static void userAccountHome(String username) throws IOException, ParseException {
        System.out.println("Please choose from one of the following menu options: \n"
                + "1. Edit account info\n"
                + "2. View account balance\n"
                + "3. View station list\n"
                + "4. Rent a bike\n"
                + "5. Return a bike\n"
                + "6. Report a problem\n"
                + "7. Log out \n");
        System.out.println("Please enter your selection (1-6):");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input");
            userAccountHome(username);
        }
        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //edit account info- return to create account or have separate method?
                //TODO editAccount(userID);
                break;
            case 2:
                //view account balance
                viewAccountBalance(username);
                break;
            case 3:
                ValleyBikeSim.viewStationList();
                break;
            case 4:
                // user records a ride
                //TODO currently doesnt account for bike id or user id

                // ValleyBikeSim.recordRide();
                break;
            case 5:
                //report a problem
                reportProblem(username);
                break;
            case 6:
                //log out, return to homepage
                initialMenu();
                break;
        }
        //if function call finished and returned to this page, keep calling menu again until log out/exit
        userAccountHome(username);
    }

    /**
     * @param: userID- the unique id associated with the user
     * View the account balance associated with a user's account
     */
    private static void viewAccountBalance(String username) {
        //TODO view user account balance
        //when done, returns to userAccountMenu
    }

    /**
     * user checks out a specific bike from a specific station
     * @param: userID- the unique id associated with the user
     */
    private static void rentBike(String username) throws IOException, ParseException {

        // ValleyBikeSim.recordRide();

        //bike is now checked out
        int bikeID = 0;
        bikeRented(username, bikeID);
    }


    /**
     * User has bike checked out and can either return bike or report a problem with the bike
     * @param: int userID- the unique id associated with the user
     * @param: bikeID- unique ID associated with the bike that the user has checked out
     */
    private static void bikeRented(String username, int bikeID) throws IOException, ParseException {
        //TODO same question- only use this if bike rent/return not RecordRide
        System.out.print("\n Enjoy your bike ride! \n"
                + "1. Return bike"
                + "2. Report a problem");
        System.out.println("Please enter your selection (1-2):");

        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input \n");
            bikeRented(username, bikeID);
        }
        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //return bike
                returnBike(username, bikeID);
                break;
            case 2:
                //report a problem
                reportProblem(username);
                break;
            case 0:
                input.close();
                //save all files before exiting
                System.exit(0);
                break;
        }
    }

    /**
     * user checks back in a rented bike
     * @param: int userID- the unique id associated with the user
     * @param: bikeID- unique ID associated with the bike that the user has checked out
     *
     */
    private static void returnBike(String username, int bikeID) throws IOException, ParseException {
        // why view all stations?
        ValleyBikeSim.viewStationList();
        //TODO input station id
        //TODO bike id from input
        //TODO confirm? Y/N (timestamps the check back in)
        //TODO save ride to file/data structure
        //TODO charge user $$

        //return to user menu
        userAccountHome(username);
    }

    /**
     * reportProblem()
     * param: userID
     * user reports a problem with the bike they checked out
     */
    private static void reportProblem(String username) throws IOException, ParseException {
        ValleyBikeSim.viewStationList();
        // DONT VIEW BIKE IDS LIST

        // but do viewing for everything else

        // get station id from user
        Integer stationId = ValleyBikeSim.getResponse("station id");

        // check station actually exists
        if(ValleyBikeSim.stationsMap.get(stationId) == null){
            System.out.println("Station with this ID doesn't exist. Please reenter ID.\n");
            String response = input.next();
        }

        // get bike id from user
        Integer bikeId = ValleyBikeSim.getResponse("bike id");

        // check station actually exists
        if(ValleyBikeSim.bikesMap.get(bikeId) == null){
            System.out.println("Bike with this ID doesn't exist. Please reenter ID.\n");
            String response = input.next();
        }

        // get user report of the problematic bike
        System.out.println("Please input report of the problem.");
        String report = input.next();

        // get bike object
        Bike bike = ValleyBikeSim.bikesMap.get(bikeId);

        // set the maintenance report and requires maintenance to true
        bike.setMntReport(report);
        bike.setMnt(true);

        // add to list of bikes that require maintenance
        ValleyBikeSim.mntReqs.add(bikeId);
    }

    //should there be option to add bike/station
    /**
     * Homescreen for internal company employees
     *
     */
    static void internalAccount() throws IOException, ParseException {
        System.out.print("\n Choose from the following: \n"
                + "1. View customer balances"
                + "2. View customer activity"
                + "3. Edit/Resolve maintenance requests"
                + "4. Equalize stations"
                + "5. Log out");
        System.out.println("Please enter your selection (1-5):");

        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input \n");
            internalAccount();
        }
        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //TODO view customer balances
                break;
            case 2:
                //TODO view customer activity
                break;
            case 3:
                ValleyBikeSim.resolveMntReqs();

                break;
            case 4:
                //equalize stations
                ValleyBikeSim.equalizeStations();
                break;
            case 5:
                //log out
                initialMenu();
                break;
        }
        //if function call finishes and returns to internal account menu
        //call account menu again
        internalAccount();
    }
}

