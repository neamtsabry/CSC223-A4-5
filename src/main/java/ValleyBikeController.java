import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Scanner;

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
        //TODO get username, authenticate, store
        //TODO get password, authenticate, store
        //TODO get email, authenticate, store
        //TODO get credit card, authenticate, store

        //go to account menu
        int userID = 0;
        userAccount(userID);
    }

    /**
     * Method for both internal and user accounts to log in
     */
    private static void logIn() throws IOException, ParseException {
        //TODO input username, authenticate
        //TODO input password, authenticate
        //TODO figure out if account is customer or internal
        //if account is user, go to user menu:
        int userID = 0;
        userAccount(userID);
        //if account is employee, go to employee menu:
        internalAccount();
    }


    /**
     * Standard menu page for a user after logging in
     * @param userID: integer representing unique userID of account
     */
    private static void userAccount(int userID) throws IOException, ParseException {
        System.out.print("\n Please choose from one of the following menu options: \n"
                + "1. Edit account info"
                + "2. View account balance"
                + "3. View station list"
                + "4. Record a ride"
                + "5. Report a problem"
                + "6. Log out \n");
        System.out.println("Please enter your selection (1-6):");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input");
            userAccount(userID);
        }
        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //edit account info- return to create account or have separate method?
                //TODO editAccount(userID);
                break;
            case 2:
                //view account balance
                viewAccountBalance(userID);
                break;
            case 3:
                ValleyBikeSim.viewStationList();
                break;
            case 4:
                //user records a ride
                //TODO currently doesnt account for bike id or user id
                ValleyBikeSim.recordRide();
                break;
            case 5:
                //report a problem
                reportProblem(userID);
                break;
            case 6:
                //log out, return to homepage
                initialMenu();
                break;
        }
        //if function call finished and returned to this page, keep calling menu again until log out/exit
        userAccount(userID);
    }

    /**
     * @param: userID- the unique id associated with the user
     * View the account balance associated with a user's account
     */
    private static void viewAccountBalance(int userID) {
        //TODO view user account balance
        //when done, returns to userAccountMenu
    }

    /**
     * user checks out a specific bike from a specific station
     * @param: userID- the unique id associated with the user
     */
    private static void rentBike(int userID) throws IOException, ParseException {
        //TODO do we want to use rent bike-return bike or preexisting RecordRide function?
        ValleyBikeSim.viewStationList();
        //input station id
        //view available bikes at station
        //input bike id
        //confirm? Y/N (timestamps the rent out)

        //bike is now checked out
        int bikeID = 0;
        bikeRented(userID, bikeID);
    }


    /**
     * User has bike checked out and can either return bike or report a problem with the bike
     * @param: int userID- the unique id associated with the user
     * @param: bikeID- unique ID associated with the bike that the user has checked out
     */
    private static void bikeRented(int userID, int bikeID) throws IOException, ParseException {
        //TODO same question- only use this if bike rent/return not RecordRide
        System.out.print("\n Enjoy your bike ride! \n"
                + "1. Return bike"
                + "2. Report a problem");
        System.out.println("Please enter your selection (1-2):");

        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input \n");
            bikeRented(userID, bikeID);
        }
        Integer num = input.nextInt();
        switch(num) {
            case 1:
                //return bike
                returnBike(userID, bikeID);
                break;
            case 2:
                //report a problem
                reportProblem(userID);
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
    private static void returnBike(int userID, int bikeID) throws IOException, ParseException {
        // why view all stations?
        ValleyBikeSim.viewStationList();
        //TODO input station id
        //TODO bike id from input
        //TODO confirm? Y/N (timestamps the check back in)
        //TODO save ride to file/data structure
        //TODO charge user $$



        //return to user menu
        userAccount(userID);
    }

    /**
     * reportProblem()
     * param: userID
     * user reports a problem with the bike they checked out
     */
    private static void reportProblem(int userID) throws IOException, ParseException {
        ValleyBikeSim.viewStationList();
        //TODO view list of bike ids at station (?)

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

