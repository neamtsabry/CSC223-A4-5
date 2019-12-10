import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.*;
import java.util.regex.Pattern;

public abstract class ValleyBikeController {

    /** initialize input of type scanner */
    private static Scanner input = new Scanner(System.in);

    /** initialize stack that remembers menu path to assist in back-tracking */
    private static Deque<Integer> menuPath = new ArrayDeque<>();

    /**
     * Checks where user was last by checking our menuPath stack
     * and returns them to this point
     *
     * @throws IOException create account, log in, save bike list and save station list methods throw IOException
     * @throws ParseException create account, log in, save bike list and save station list methods throw ParseException
     */
    private static void returnToLastMenu(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
    /* We add to the menuPath stack whenever we may need to
    remember this landing page in order to return to it.

    menuPath INDEX
    ------------------------------------------------
    The #s in menuPath refer to the following pages:
    1 - initial menu
        11 - create account
        12 - log in menu

    2 - customer account home
        21 - create new internal account
        edit account info
        22 - view account balance
        23 - view station list
        241 - rent bike
        242 - return bike
        25 - report problem

    3 - internal account home
        31 -
        32 -
        33 -

     */

        if (menuPath.isEmpty()){ initialMenu(); }

        switch(menuPath.pop()) {
            case 1:
                initialMenu();
            case 11:
                createCustomerAccount();
//                rentBike("asmitagautam");
            case 12:
                logIn();
            case 2:
                internalAccountHome(username);
            case 21:
                editCustomerAccount(username);
            case 3:
                internalAccountHome(username);
            default:
                initialMenu();
        }
    }

    /**
     * Basic option menu that shows at start of program and when no one is logged in
     * Allows user to create a new account or log in or exit
     *
     * @throws IOException create account, log in, save bike list and save station list methods throw IOException
     * @throws ParseException create account, log in, save bike list and save station list methods throw ParseException
     */
    static void initialMenu() throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //check whether it's time to renew customer's memberships
        //ValleyBikeSim.checkMembershipRenewalTime();

        System.out.print("\nPlease choose from one of the following menu options: \n"
                + "1: Create Customer Account "
                + "2: Log In "
                + "0: Exit program \n");
        //prompt the user to pick an int option
        int num = getResponse("Please enter your selection (0-2):");
        input.nextLine();

        switch(num) {
            case 1:
                //create a new customer account
                createCustomerAccount();
//                rentBike("cptnemo");
                break;
            case 2:
                //log in to existing customer or internal account
                logIn();
                break;
            case 0:
                //exit program
                input.close();
                System.exit(0);
                break;
            default:
                System.out.println("That is an invalid option. Please try again.");
                initialMenu();
        }
        //if function call finished and returned to this page, keep calling menu
        initialMenu();
    }

    /**
     * Gets all the required field information from the user to create a new customer account object
     * Calls method to add the new customer account to customer account map
     * Leads to customer account home after successful log in
     *
     * @throws IOException add customer account and user account home methods throw IOException
     * @throws ParseException add customer account and user account home methods throw ParseException
     */
    private static void createCustomerAccount() throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //Assumption: a new internal account cannot be created by a user who is not logged into an internal account
        //i.e. only internal staff can create new internal accounts

        //remember this menu in case we need to return
        menuPath.push(1);

        //each field has its own method which calls itself until a valid input is entered
        String username = enterUsername(null);
        String password = enterPassword(null);
        String emailAddress = enterEmail(null);
        String creditCard = enterCreditCard();
        int membership = enterMembership();

        //once all the required fields have been input by the user, create new customer account
        //Assumption: initially the balance in customer account is always 0
        ValleyBikeSim.createCustomerAccount(username, password, emailAddress, creditCard, membership);

        //Let the user know the account has been successfully created
        System.out.println("Customer account successfully created!");

        if (membership == 2) {
            System.out.println("You have been charged $20 for your monthly membership. Your membership will auto-renew each month, \n" +
                    " and you will get an email notification when your card is charged. \n" +
                    " If your credit card ever expires or becomes invalid, you will be switched to a Pay-As-You-Go member " +
                    "and notified via email. ");
        } else if (membership == 3) {
            System.out.println("You have been charged $90 for your monthly membership. Your membership will auto-renew each month,\n" +
                    " and you will get an email notification when your card is charged. \n" +
                    "If your credit card ever expires or becomes invalid, you will be switched to a Pay-As-You-Go member " +
                    "and notified via email. ");
        }

        menuPath.pop();// we no longer need to remember this menu

        //instead of returning to previous menu,
        // we move these new customers to their home menu
        customerAccountHome(username);
    }

    /**
     * This is the log in menu that allows the user to log in to either customer or internal account
     *
     * @throws IOException customer log in and internal log in methods in model throw IOException
     * @throws ParseException customer log in and internal log in methods in model throw ParseException
     */
    private static void logIn() throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //prompt the user to choose which kind of account they want to log into
        //TODO GB - make menu lines shorter vertically
        System.out.println("\nPlease choose from one of the following menu options:");
        int logIn = getResponse("1: Log in to customer account. " +
                "2: Log in to internal account. " +
                "0: Return to menu.  \n" +
                "Please enter your selection (0-2):");
        input.nextLine();

        //if user wants to log out take them back to initial menu
        if (logIn == 0){
            return;
        }

        //this is not inside the switch case because if it is in the switch case,
        //it informs the user that they picked an invalid option after asking for username and password
        if (logIn != 1 && logIn != 2){
            //if they did not choose either 1 or 2
            //make them choose again by recursively calling log in
            System.out.println("That is not a valid input. Please try again.");
            logIn();
        }
//TODO COME BACK TO HERE!!!
        //prompt the user to input their username
        System.out.println("Please enter your username or '0' to cancel:");
        String username = input.nextLine();

        //if user wants to log out take them back to initial menu
        if (username.contentEquals("0")){
            return;
        }

        while ((logIn == 1) && (!ValleyBikeSim.accountMapsContain(username, 1)) ||
                (logIn == 2) && (!ValleyBikeSim.accountMapsContain(username, 2))){
            System.out.println("Username does not exist. Please try again.");
            System.out.println("Please enter your username or '0' to cancel:");
            username = input.nextLine();

            //if user wants to log out take them back to initial menu
            if (username.contentEquals("0")){
                return;
            }
        }

        //prompt the user to input their password
        System.out.println("Please enter your password or '0' to cancel:");
        String password = input.nextLine();

        //if user wants to log out take them back to initial menu
        if (password.contentEquals("0")){
            return;
        }

        switch (logIn){
            case 1:
                //if they want to log in to customer account
                //validate their username and password in the customer account map
                ValleyBikeSim.customerLogIn(username, password);
                break;
            case 2:
                //if they want to log in to internal account
                //validate their username and password in the internal account map
                ValleyBikeSim.internalLogIn(username, password);
                break;
        }
        //if function call finished and returned to this page, keep calling menu again until 'return to menu' called
        logIn();
    }


    /**
     * Standard menu page for a customer account after logging in
     *
     * @param username unique username associated with the customer account
     *
     * @throws IOException editCustomerAccount, viewStationList, recordRide, reportProblem, initialMenu, viewBikeList throw IOException
     * @throws ParseException editCustomerAccount, viewStationList, recordRide, reportProblem, initialMenu, viewBikeList throw ParseException
     */
    static void customerAccountHome(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {

        //checks whether user has a rental, and if so whether it exceeds 24 hours
        ValleyBikeSim.checkBikeRented(username);

        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
        //menu option for customer account home
        System.out.print("\nPlease choose from one of the following menu options:\n"
                + "1: View and edit account info. "
                + "2: View account balance. "
                + "3: View station list. ");

        //if customer does not have a bike rented, allow option to rent
        //otherwise, give option to return
        if (customer.getIsReturned()) { System.out.print("4: Rent a bike. "); }
        else { System.out.print("4: Return bike. "); }

        System.out.print("6: Report a problem. "
                + "5: View total number of rides. "
                + "6: View average ride time. "
                + "7: View your most popular ride time. "
                + "8: Delete account. "
                + "9: Log out. \n" +
                "Please enter your selection (0-5): \n");

        // if input is not a integer
        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("That is not a valid input. Please try again.");
            customerAccountHome(username);
        }

        int num = input.nextInt();
        switch(num) {
            case 1:
                //print current account info
                viewCustomerAccount(username);
                //edit account info
                editCustomerAccount(username);
                break;
            case 2:
                //view account balance
                System.out.println("Your account balance is "+ ValleyBikeSim.viewAccountBalance(username));
                break;
            case 3:
                //view station list
                ValleyBikeSim.viewStationList();
                break;
            case 4:
                // if customer has no ongoing rentals, help user rent a bike
                if (customer.getIsReturned()) { rentBike(username); }
                else { //else, user can return a bike
                    UUID lastRideId = customer.getLastRideId();
                    returnBike(username, lastRideId);
                } // if customer has ongoing rental, help user return bike
                break;
            case 5:
                reportProblem(username);
                break;
            case 6:
                System.out.println("The total number of rides you've taken is " + ValleyBikeSim.viewTotalRides(username));
                break;
            case 7:
                System.out.println("Your average ride time is " + ValleyBikeSim.viewAverageRideTime(username));
                break;
            case 8:
                Ride ride = ValleyBikeSim.viewLongestRide(username);
                System.out.println("Your longest ride was " + ride.getRideLength() + " hours long.");
                System.out.print("It was from " + ride.getStartTimeStamp() + " to " + ride.getEndTimeStamp() + ".");
                break;
            case 9:
                ValleyBikeSim.disableCustomerAccount(username);
                break;
            case 0:
                //return to homepage to log out
                initialMenu();
                break;
            default:
                customerAccountHome(username);
                break;
        }
        //if function call finished and returned to this page, keep calling menu again until log out/exit
        customerAccountHome(username);
    }

    /**
     * prompts user to enter inputs that will allow creation of new internal account
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    private static void createInternalAccount(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //Assumption: a new internal account cannot be created by a user who is not logged into an internal account
        //i.e. only internal staff can create new internal accounts
        //TODO Grace

        //add the internal home menu to our stack in case we need to return to it
        menuPath.push(2);

        //each field has its own method which calls itself until a valid input is entered
        String newUsername = enterUsername(username);
        String password = enterPassword(username);
        String emailAddress = enterEmail(username);

        //create new internal account object from inputs
        InternalAccount internalAccount = new InternalAccount(newUsername, password, emailAddress);
        ValleyBikeSim.addInternalAccount(internalAccount, newUsername);

        //Let the user know the account has been successfully created
        System.out.println("Internal account successfully created!");

        menuPath.pop();// we no longer need to remember this menu
        return;
    }

    /**
     * Displays customer account information
     *
     * @param username is the unique username associated with the customer account
     */
    private static void viewCustomerAccount(String username) {
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
        //print current customer info
        System.out.println("\nCUSTOMER ACCOUNT INFORMATION:" +
                "\nUsername: " + customer.getUsername());
        System.out.print("Password: ");
        for (int i=0; i<customer.getPassword().length(); i++){
            //prints password represented as asterisks
            System.out.print("*");
        }
        System.out.println("\nEmail Address: " + customer.getEmailAddress());
        System.out.println("Credit Card: " + customer.getCreditCard().substring(11));
        //TODO returns a null pointer exception AG
        //System.out.println("Membership: " + customer.getMembership().getMembershipString());
    }


    /**
     * Menu page for editing customer account information
     *
     * @param username is the unique username associated with the customer account
     */
    private static void editCustomerAccount(String username) throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //TODO save edited fields
        //TODO add a return to customer home option
        //TODO recursively call itself to edit multiple fields
        //TODO handle edge case of not entering int

        //prompt user to choose which field they want to edit
        System.out.println("\nPlease choose from one of the following menu options:\n" +
                "1: Edit username. " +
                "2: Edit password. " +
                "3: Edit email address. " +
                "4: Edit credit card number. " +
                "5: Edit membership. " +
                "0: Return to account home. \n" +
                "Please enter your selection (0-5):");
        int edit = input.nextInt();
        input.nextLine();
        switch (edit){
            case 1:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit username
                String newUsername = enterUsername(null);
                ValleyBikeSim.updateCustomerUsername(username, newUsername);
                break;
            case 2:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit password
                String newPassword = enterPassword(null);
                ValleyBikeSim.updateCustomerPassword(username, newPassword);
                break;
            case 3:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit email address
                String newEmail = enterEmail(null);
                ValleyBikeSim.updateCustomerEmailAddress(username, newEmail);
                break;
            case 4:
                //remember this menu so we can return later
                menuPath.push(21);

                //edit credit card number
                String newCreditCard = enterCreditCard();
                ValleyBikeSim.updateCustomerCreditCard(username, newCreditCard);
                break;
            case 5:
                //remember this menu so we can return later
                menuPath.push(21);
                //edit membership type
                int newMembership = enterMembership();
                //validate credit card if customer wants to switch to paid membership
                if (newMembership == 2 || newMembership == 3) {
                    String creditcard = ValleyBikeSim.getCustomerObj(username).getCreditCard();
                    if (!isValidCreditCard(creditcard)) {
                        //if cc is not valid, do not allow them to switch memberships.
                        System.out.println("Sorry, your credit card is not valid. You cannot switch to a paying membership at this time. \n" +
                                "Please ensure your credit card information is updated and try again. ");
                    } else {
                        //if credit card is valid, switch memberships
                        ValleyBikeSim.updateCustomerMembership(username, newMembership);
                    }
                }
                break;
            case 0:
                return;
            default:
                //if none of the other options, must not be valid
                System.out.println("That is not a valid input. Please try again.");
        }
        //if function call finished and returned to this page, keep calling edit account again until 'return to menu' is chosen
        editCustomerAccount(username);
    }

    /**
     * Edit information of an internal account
     * @param username username of the account that will be edited
     * @throws ParseException
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    private static void editInternalAccount(String username) throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //TODO add a return to customer home option
        //TODO handle edge case of not entering int
        //TODO Grace

        //prompt user to choose which field they want to edit
        System.out.println("\nPlease choose from one of the following menu options:\n" +
                "1: Edit username. " +
                "2: Edit password. " +
                "3: Edit email address. " +
                "0: Return to account home.\n" +
                "Please enter your selection (0-5):");
        int edit = input.nextInt();
        input.nextLine();
        switch (edit){
            case 1:
                //edit username
                String newUsername = enterUsername(null);
                ValleyBikeSim.updateInternalUsername(username, newUsername);
                break;
            case 2:
                //edit password
                String newPassword = enterPassword(null);
                ValleyBikeSim.updateInternalPassword(username, newPassword);
                break;
            case 3:
                //edit email address
                String newEmail = enterEmail(null);
                ValleyBikeSim.updateInternalEmailAddress(username, newEmail);
                break;
            case 0:
                return;
            default:
                //if none of of other options, must not be valid
                System.out.println("That is not a valid input. Please try again.");
        }
        //if function call finished and returned to this page, keep calling home again until 'return to menu' is chosen
        editInternalAccount(username);
    }

    /**
     * Allows a customer to rent a bike by asking for necessary inputs
     *
     * @throws IOException
     * @throws ParseException
     */
    private static void rentBike(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //validate credit card before allowing rental- to make sure they can pay
        String creditCard = ValleyBikeSim.viewCreditCard(username);
        //check validity of credit card, send them back to home menu if not valid
        if (!isValidCreditCard(creditCard)) {
            System.out.println("Sorry, your credit card is not valid. You cannot rent a bike without a valid credit card. \n" +
                    "Please make sure the credit card saved in your account is correct, then try again.");
            return; // return to customerAccountHome
        }

        // View stations
        System.out.println("STATION LIST:");
        ValleyBikeSim.viewStationList(); // view station list

        // choose station to rent from or go back
        int statId = getResponse("Please pick a station from the above list to rent a bike from.\n" +
                "Enter the station ID ('11') or '0' to return to menu: ");
        Station stationFrom = ValleyBikeSim.getStationObj(statId); // get station obj (or null) from input

        //keep running while loop until input valid station with available bikes
        while ((stationFrom == null || Objects.equals(stationFrom.getBikes(), 0)) && statId != 0) {
            //if station doesnt exist, inform user
            if (stationFrom == null) System.out.println("The station ID entered does not exist in our system.");

            //if station doesn't have bikes, equalize stations and have user re-select station
            else if (Objects.equals(stationFrom.getBikes(), 0)) {
                System.out.println("The station entered does not have any bikes.\n" +
                        "We are notifying maintenance worker to resolve this, but in the meantime please " +
                        "choose another station");
                //mock notification to maintenance worker who immediately goes and equalizes stations
                ValleyBikeSim.equalizeStations();
            }
            //because station not valid, have user re-input and then validate
            statId = getResponse("Please pick a station to rent a bike from.\n" +
                    "Enter the station ID ('11') or '0' to return to menu: ");
            stationFrom = ValleyBikeSim.getStationObj(statId);
        }

        // if user entered 0, return to menu
        if (Objects.equals(statId, 0)){ return; }

        /*// Validate user input for station ID
        // keep prompting user until input matches the ID of an available station
        Station stationFrom = ValleyBikeSim.getStationObj(statId); // get station obj (or null) from input

        // if station doesn't exist or doesn't have any bikes
        while((stationFrom == null)||(Objects.equals(stationFrom.getBikes(), 0))) {
            if (stationFrom == null) System.out.println("The station ID entered does not exist in our system.");

            //if station doesn't have bikes, equalize stations and have user re-select station
            else if (Objects.equals(stationFrom.getBikes(), 0)) {
                System.out.println("The station entered does not have any bikes.\n" +
                        "We are notifying maintenance worker to resolve this, but in the meantime please " +
                        "choose another station");
                //mocking notification to maintenance worker who immediately goes and equalizes stations
                ValleyBikeSim.equalizeStations();
            }

            ValleyBikeSim.viewStationList();
            statId = getResponse("Please pick a station to rent a bike from.\n" +
                    "Enter the station ID ('11') or '0' to return to menu: ");
            stationFrom = ValleyBikeSim.getStationObj(statId);

            // if user entered 0, return to menu
            if (Objects.equals(stationFrom, 0)){ return; }
        }*/

        // View available bike ids at station
        System.out.println("Here's a list of bike IDs at Station #" + statId);
        System.out.format("%-10s\n", "Bike ID");

        // Get list iterator of bikes at station
        LinkedList<Integer> bikeList = stationFrom.getBikeList();

        ListIterator<Integer> bikesAtStation = bikeList.listIterator();

        // Print bikes at station
        while(bikesAtStation.hasNext()){
            int bikeInt = bikesAtStation.next();
            System.out.format("%-10s\n", bikeInt);
        }

        // Choose bike to rent
        int bikeID = getResponse("Please enter the ID number of the bike you" +
                " would like to rent ('11') or '0' to return to menu: ");

        // if user entered 0, return to menu
        if (Objects.equals(bikeID, 0)){ return; }

        Bike someBike = ValleyBikeSim.getBikeObj(bikeID); // get bike object or null from bike ID

        while (!bikeList.contains(bikeID)){
            System.out.println("The bike ID entered is not at this station.");
            bikeID = getResponse("Please enter the ID number of the bike you" +
                    " would like to rent ('11') or '0' to return to menu: ");

            // if user entered 0, return to menu
            if (Objects.equals(bikeID, 0)){ return; }

            someBike = ValleyBikeSim.getBikeObj(bikeID);
        }

        ValleyBikeSim.moveStation(someBike, 0); // move bike to station '0' <- our "non-station" ID

        UUID rideId = UUID.randomUUID();

        // create new ride object
        Ride ride = new Ride(rideId,
                bikeID,
                username,
                false,
                Instant.now(),
                null,
                statId,
                null);

        // add ride to map as well as database
        ValleyBikeSim.addRide(ride);

        // Add ride to customer account
        // assume username is always valid
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username); // get customer account object
        ValleyBikeSim.updateRideIdList(username, rideId);
        ValleyBikeSim.updateLastRideisReturned(username, false);

        // now bike is fully rented
        // bikeRented(username, b, ride.getRideId());
        System.out.println("You have rented bike #" + bikeID + " from station #" + statId + ". Enjoy your ride!");
        System.out.println();

        // equalize stations if there's one bike or less left at station after bike is rented
        // notify maintenance worker to redistribute bikes
        // right now this work is automated by the equalizeStations() function
        if (stationFrom.getBikes() <= 1){
            System.out.println("This station is almost empty!");
            System.out.println("Notifying maintenance worker to resolve this...");
            ValleyBikeSim.equalizeStations();
            System.out.println("The bikes have now been redistributed between the stations.");
            System.out.println();
        }
    }

    /**
     * Allows a user to return a bike by prompting for necessary information
     * @param username username of account returning bike
     * @param lastRideId ongoing ride object that will get updated
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    private static void returnBike(String username, UUID lastRideId) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //get ongoing ride object
        Ride rideObj = ValleyBikeSim.getRideObj(lastRideId);

        // View stations
        System.out.println("STATION LIST:");
        ValleyBikeSim.viewStationList(); // view station list

        // choose station to return to
        int statId = getResponse("Please enter station to which you're returning the bike " +
                "or '0' to return to the menu");

        // if user entered 0, return to menu
        if (Objects.equals(statId, 0)){ return; }

        // designated station, whether bike returned to or bike rented from
        Station stationTo = ValleyBikeSim.getStationObj(statId);

        // keep prompting user until the station obj is not null
        while(stationTo == null) {
            System.out.println("The station entered does not exist in our system.");
            statId = getResponse("Please enter station to which you're returning the bike " +
                    "or '0' to return to the menu");

            // if user entered 0, return to menu
            if (Objects.equals(statId, 0)){ return; }

            stationTo = ValleyBikeSim.getStationObj(statId);
        }

        rideObj.setStationTo(statId);

        // get rented bike
        int bikeId = rideObj.getBikeId(); //get bike ID from ride
        Bike someBike = ValleyBikeSim.getBikeObj(bikeId); //get bike object from ID

        // move bike to new station
        ValleyBikeSim.moveStation(someBike, statId);

        // update ride to be returned and set its end time stamp
        ValleyBikeSim.updateRideIsReturned(lastRideId, true);
        ValleyBikeSim.updateRideEndTimeStamp(lastRideId, Instant.now());

        // set the same in customer account
        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);
        ValleyBikeSim.updateLastRideisReturned(username, true);

        System.out.println("Bike #" + bikeId + " has been returned to station #" + statId + ". Thank you!");
        System.out.println();

        if (stationTo.getAvailableDocks() <= 1){
            // if there's 1 available docks or less at station after bike is returned
            // notify maintenance worker to redistribute bikes
            // right now this work is automated by the equalizeStations() function
            System.out.println("We see that the station is now almost full!");
            System.out.println("We are notifying maintenance worker to resolve this.");
            //mock call to maintenance worker who immediately equalizes stations
            ValleyBikeSim.equalizeStations();
            System.out.println();
        }

        calculateRentalCharge(username, rideObj, lastRideId);

        System.out.println("You're all done! Thank you for returning this bike.");
        // take user back to their account home
        customerAccountHome(username);
    }

    /**
     * helper method for bike return that calculates cost of rental and charges customer
     * The credit card was validated when rental was made, so it does not need to be validated again
     * @param username username of account that made the rental
     * @param rideObj ride object that represents the ride information
     * @param lastRideId id of the ride object
     * @throws ClassNotFoundException
     */
    private static void calculateRentalCharge(String username, Ride rideObj, UUID lastRideId) throws ClassNotFoundException {
        //check how many included rides remain in account to determine how to charge for rental
        int ridesLeft = ValleyBikeSim.viewMembershipType(username).getTotalRidesLeft();
        long rideLength = rideObj.getRideLength();
        long paymentDue;
        //if pay-as-you-go or no free rides remaining on membership, charge by time
        if (ridesLeft == 0) {
            //card was already validated before bike rented to ensure they can pay for the rental
            //ride cost is 15c per minute
            paymentDue = rideLength * (long) .15;
            //update balance to add new ride payment
            double balance = ValleyBikeSim.getCustomerObj(username).getBalance();
            ValleyBikeSim.getCustomerObj(username).setBalance(balance + paymentDue);
            //update ride payment in ride object
            ValleyBikeSim.updateRidePayment(lastRideId, paymentDue);

        } else {
            //otherwise decrement rides remaining in membership
            ValleyBikeSim.viewMembershipType(username).setTotalRidesLeft(ridesLeft - 1);
            //calculate whether there is an overtime charge (for a ride longer than 1hr)

            if (rideLength > 60L) {
                long paymentLength = rideLength - 60L;
                //ride cost is 15c per minute after 1st hour
                paymentDue = paymentLength * (long) .15;
                //update balance to add new ride payment
                double balance = ValleyBikeSim.getCustomerObj(username).getBalance();
                ValleyBikeSim.getCustomerObj(username).setBalance(balance + paymentDue);
            } else {
                //ride is free if under 1hr
                paymentDue = 0L;
                ValleyBikeSim.updateRidePayment(lastRideId, 0.00);
            }
        }
        //inform customer of the charge
        System.out.println("You have been charged " + paymentDue + "for your ride." );
    }


    /**
     * Report problem for regular user by adding bike's id to
     * maintenance request list and setting its fields to requiring
     * maintenance.
     * @param username for user
     * user reports a problem with the bike they checked out
     */
    private static void reportProblem(String username) throws IOException, ParseException, ClassNotFoundException {
        int bikeId = getResponse("Please enter the ID of the bike you" +
                " are experiencing problems with ('11') or '0' to return to the menu:");

        // if user entered 0, return to menu
        if (Objects.equals(bikeId, 0)){ return; }

        Bike someBike = ValleyBikeSim.getBikeObj(bikeId); // get bike object or null from bike ID

        while (someBike != null){ //while input is not a bike ID, keep asking
            System.out.println("The bike ID entered does not exist in our system. Please try again.");
            bikeId = getResponse("Please enter the ID of the bike you" +
                    " are experiencing problems with ('11') or '0' to cancel:");

            someBike = ValleyBikeSim.getBikeObj(bikeId);

            // if user entered 0, return to menu
            if (Objects.equals(bikeId, 0)){
                System.out.println("Report problem has been canceled.");
                return;
            }
        }

        // prompt user for report detailing what's wrong
        System.out.println("Please tell us what is wrong with this bike, or '0' to cancel:");
        input.nextLine();
        String mntReport = input.nextLine();

        // if user entered 0, return to menu
        if (mntReport.contentEquals("0")){
            System.out.println("Report problem has been canceled.");
            return;
        }
        // add to maintenance requests
        ValleyBikeSim.addToMntRqs(bikeId, mntReport);

        // get bike object
        Bike bike = ValleyBikeSim.getBikeObj(bikeId);

        // set bike's maintenance report and maintenance to true
        ValleyBikeSim.updateBikeRqMnt(bikeId, true);
        ValleyBikeSim.updateBikeMntReport(bikeId, mntReport);

        // bike is now out of commission until fixed
        ValleyBikeSim.updateBikeLocation(bikeId, 1);

        // increase maintenance requests for the station
        Station statObj = ValleyBikeSim.getStationObj(bike.getStation());
        ValleyBikeSim.updateStationMntRqsts(bike.getStation(), statObj.getMaintenanceRequest()+1);

        // let user know the process is done
        System.out.println("Maintenance report has been successfully filed!");
    }

    /**
     * Main menu for internal accounts
     * @param username username of internal account
     * @throws IOException addStation, addBike, equalizeStations and initialMenu throw IOException
     * @throws ParseException addStation, addBike, equalizeStations and initialMenu throw ParseException
     */
    static void internalAccountHome(String username) throws IOException, ParseException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //prompt user to pick option from main internal menu
        System.out.print("\n Choose from the following: \n"
                + "1: Create new internal account. "
                + "2: Edit account information. "
                + "3: View customer balances. "
                + "4: View customer activity. "
                + "5: Add new station. "
                + "6: Add new bike. "
                + "7: View station list. "
                + "8: View bike list. "
                + "9: Edit/Resolve maintenance requests. "
                + "10: Equalize stations. "
                + "11: View total number of users. "
                + "12: View total number of maintenance requests. "
                + "13: View most popular station. "
                + "0: Log out. \n");
        System.out.println("Please enter your selection (1-9): ");

        if (!input.hasNextInt()){
            //keep asking for input until valid
            System.out.println("Not a valid input \n");
            internalAccountHome(username);
        }

        int num = input.nextInt();
        switch(num) {
            case 1:
                //create new internal account (e.g. for a different employee)
                createInternalAccount(username);
                break;
            case 2:
                //edit this internal account
                editInternalAccount(username);
                break;
            case 3:
                //TODO view customer balances
                break;
            case 4:
                //view customer activity
                viewCustomerActivity();
                break;
            case 5:
                //add station to station list
                addStation();
                break;
            case 6:
                //add bike to bike list
                addBike();
                break;
            case 7:
                //view station list
                ValleyBikeSim.viewStationList();
                break;
            case 8:
                //view bike list
                ValleyBikeSim.viewBikeList();
                break;
            case 9:
                // resolve maintenance requests
                ValleyBikeSim.resolveMntReqs();
                break;
            case 10:
                //equalize stations
                ValleyBikeSim.equalizeStations();
                break;
            case 11:
                //TODO
                break;
            case 12:
                //TODO
                break;
            case 13:
                //TODO
                break;
            case 0:
                //go to initial menu to log out
                initialMenu();
                break;
        }
        //if function call finishes and returns to internal account menu
        //call account menu again
        internalAccountHome(username);
    }

    /**
     * internal employee can view customer activity
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     * @throws ParseException
     * @throws IOException
     */
    private static void viewCustomerActivity() throws InterruptedException, ClassNotFoundException, NoSuchAlgorithmException, ParseException, IOException {
        // view all customers' usernames
        ValleyBikeSim.viewAllCustomers();

        // ask user to input customer username
        System.out.println("Please input a customer's username to view their activity");
        String username = input.nextLine();

        // check for '0' input and return to previous menu
        if (username.contentEquals("0")) { returnToLastMenu(null); }

        CustomerAccount customer = ValleyBikeSim.getCustomerObj(username);

        while( customer == null){
            // ask user to input customer username
            System.out.println("Username entered does not exist.");
            System.out.println("Please input a customer's username to view their activity");
            username = input.nextLine();
            customer = ValleyBikeSim.getCustomerObj(username);
        }

        viewCustomerActivity(customer);
    }

    private static void viewCustomerActivity(CustomerAccount customer){
        ArrayList<UUID> rideList = customer.getRideIdList();

        //display rides the selected user has taken
        if(rideList.size() > 0){
            System.out.format("%-10s%-10s%-10s%-10s%-10s%-10s%-10s\n", "Bike ID", "Is returned",
                    "Start Timestamp", "End Timestamp", "RideLength", "Station from", "Station to");

            for(UUID rideId : rideList){
                Ride rideObj = ValleyBikeSim.getRideObj(rideId);

                System.out.format("%-10d%-10b%-10s%-10s%-10d%-10d%-10d\n",
                        rideObj.getBikeId(),
                        rideObj.getIsReturned(),
                        rideObj.getStartTimeStamp(),
                        rideObj.getEndTimeStamp(),
                        rideObj.getRideLength(),
                        rideObj.getStationFrom(),
                        rideObj.getStationTo());
            }
        } else{
            System.out.println("This customer has not started any rides yet.");
        }
    }

    /**
     * Prompts user for all station data and then creates a new station
     * object which is added to the stationMap
     * @throws IOException
     * @throws ParseException
     */
    private static void addStation() throws IOException, ParseException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
        // use helper function to check input is valid and save it
        int id = getResponse("Please enter the ID for this station:");

        // handle if the station already exists
        while(ValleyBikeSim.stationsMapContains(id)){
            // let user know
            System.out.println("Station with this ID already exists.");

            // re-prompt user for station id
            id = getResponse("Please re-enter the ID for this station:");
        }

        // prompt user for station name
        System.out.println("Please enter station name: ");
        input.nextLine();
        String name = input.nextLine();

        // assume new station starts off with no maintenance requests
        Integer maintenanceRequest = 0;

        // prompt capacity for station
        Integer capacity = getResponseBetween(5, 37, "What is the station's capacity?");

        // number of kiosks
        Integer kiosk = getResponse("How many kiosks?");

        // prompt for the station's address
        System.out.println("Please enter station address: ");
        input.nextLine();
        String address = input.nextLine();

        // confirmation
        System.out.println("Are you sure you want to add a station with the info entered?(y/n)");
        String confirm = input.nextLine();

        switch(confirm.toLowerCase()){
            case "y":
                // create new station object with received data from user
                Station stationOb = new Station(
                        name,
                        maintenanceRequest,
                        capacity,
                        kiosk,
                        address);

                // add new station to database and tree
                ValleyBikeSim.addStation(stationOb, id);

                System.out.println("Station has been added!");
                break;
            case"n":
                System.out.println("The station was not added, taking you back to your account home...");
                break;
            default:
                System.out.println("You have to answer with y (yes) or n (no)");
                break;
        }
    }

    /**
     * This method enables internal employees to add new bikes to the system
     * @throws IOException
     * @throws ParseException
     */
    static void addBike() throws IOException, ParseException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
        // get new bike's id
        //TODO GRACE validate length of bike id
        int id = getResponse("Please enter the bike's ID");


        // if the bike already exists
        while(ValleyBikeSim.getBikeObj(id) != null){
            // ask if user wants to overwrite bike
            System.out.println("Bike with this ID already exists.");

            // prompt user to re-enter bike id
            id = getResponse("Please re-enter the bike's ID");
        }

        // View stations
        System.out.println("Here's a list of stations and their info.");

        // view station list
        ValleyBikeSim.viewStationList();

        // prompt for the station id bike will be located in
        int stationId = getResponse("Please enter the ID for the station the bike is located at:");

        // check if station doesn't exist
        while(ValleyBikeSim.getStationObj(stationId) == null){
            // let user know and prompt them to reenter the id
            System.out.println("Station with this ID doesn't exist");
            stationId = getResponse("Please re-enter the ID for this station:");
        }

        // assume bike starts off as not needing maintenance
        String mnt = " ";

        // initiate maintenance report string
        String mntReport = "n";

        // assume bike starts off as available
        int bikeLocation = 0;

        // create new bike object based on user's inputs
        Bike bikeOb = new Bike(
                id,
                bikeLocation,
                stationId,
                mnt,
                mntReport
        );

        // add to bike tree structure
        ValleyBikeSim.addBike(bikeOb);

        // move bike to the corresponding station
        ValleyBikeSim.moveStation(bikeOb, stationId);

        // update database and station object with bike list
        ValleyBikeSim.addBikeToStation(stationId, id);

        // update station's number of bikes in database
        ValleyBikeSim.updateStationBikesNum(stationId, ValleyBikeSim.getStationObj(stationId).getBikes());
    }

    /**
     * Helper method to validate user integer input
     *
     * @param request - the input being requested
     * @return the validated integer inputted by user
     */
    private static Integer getResponse(String request){
        System.out.println(request);
        while (!input.hasNextInt()){ //keep requesting new input until valid int
            System.out.println("That is not a valid number. Please try again.");
            System.out.println(request);
            input.next();
        }
        return input.nextInt();
    }

    /**
     * Helper method to ensure input is between two values
     *
     * @param a first value
     * @param b second value
     * @param request what should be asked of the user
     * @return return the validated input
     */
    public static Integer getResponseBetween(int a, int b, String request){
        int num = getResponse(request);
        while(num < a || num > b){ //keep requesting new input until one in specified range is entered
            System.out.println("You have to enter an option between " + a + " and " + b);
            num = getResponse(request);
        }
        return num;
    }






    /**
     * Prompts user to input username
     * Validates if username is between 6-14 characters
     * Loops until valid username input by user
     * @param creator - internal username for person creating the internal account
     *               this string is null for customer account creation
     * @return valid username input by user
     */
    private static String enterUsername(String creator) throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        String username;
        do {//loops until user inputs 0 or valid username
            //prompts user to input username
            System.out.println("Enter username (must be between 6-14 characters) or '0' to cancel:");
            username = input.nextLine();

            // check for '0' input and return to previous menu
            if (username.contentEquals("0")) { returnToLastMenu(creator); }

        } while (!isValidUsername(username)); //validates that username is between 6-14 characters and unique in our system

        //return valid username input by user
        return username;
    }

    /**
     * Prompts user to input password
     * Validates if password is between 6-14 characters
     * Recursively calls itself until valid password input by user
     * @param creator - internal username for person creating the internal account
     *                this string is null for customer account creation
     * @return valid password input by user
     */
    private static String enterPassword(String creator) throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        String password;
        do {//loops until user inputs 0 or valid password
            //prompts user to input password
            System.out.println("Enter password (must be between 6-14 characters) or '0' to cancel:");
            password = input.nextLine();

            // check for '0' input and return to previous menu
            // creator string is null for customer account creation
            if (password.contentEquals("0")) { returnToLastMenu(creator); }

        } while (!isValidPassword(password)); //validates that password is between 6-14 characters

        //return valid password input by user
        return password;
    }

    /**
     * Prompts user to input email address
     * Validates if email address is in correct format
     * Recursively calls itself until valid email address input by user
     * @param creator - internal username for person creating the internal account
     *                this string is null for customer account creation
     * @return valid email address input by user
     */
    private static String enterEmail(String creator) throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        String emailAddress;
        do {//loops until user inputs 0 or valid password
            //prompts user to input email address
            System.out.println("Enter email address (name@site.com) or '0' to cancel:");
            emailAddress = input.nextLine();

            // check for '0' input and return to previous menu
            if (emailAddress.contentEquals("0")) { returnToLastMenu(creator); }

        } while (!isValidEmail(emailAddress)); //validates that email address is in correct format

        //return valid email address input by user
        return emailAddress;
    }

    /**
     * Prompts user to input credit card
     * Validates if credit card is correct
     * Recursively calls itself until valid email address input by user
     * @return valid credit card input by user
     */
    private static String enterCreditCard() throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        String creditCard;
        do {//loops until user inputs 0 or valid password
            //prompts user to input credit card
            System.out.println("Enter credit card number" +
                    " (must be a 16 digit number with no spaces or dashes)" +
                    " or '0' to cancel:");
            creditCard = input.nextLine();

            // check for '0' input and return to previous menu
            if (creditCard.contentEquals("0")) { returnToLastMenu(null); }

        } while (!isValidCreditCard(creditCard)); //validates the credit card is correct

        //return valid credit card input by user
        return creditCard;
    }

    /**
     * Prompts user to input membership
     * @return membership string input by user
     */
    private static int enterMembership() throws ParseException, InterruptedException, IOException, ClassNotFoundException, NoSuchAlgorithmException {
        System.out.println("Choose membership type: \n" +
                "1: Pay-as-you-go Membership. " +
                "2: Monthly Membership. " +
                "3: Yearly Membership ");

        //prompt the user to pick an int option
        int num = getResponseBetween(0,3,"Please enter your selection (1-3) or '0' to cancel:");

        // check for '0' input and return to previous menu
        if (Objects.equals(num, 0)) { returnToLastMenu(null); }

        //return membership input by user
        return num;
    }

    /**
     * Validates if username is between 6 and 14 characters
     * @param username is the username input by the user
     * @return true if username is valid and false otherwise
     */
    public static boolean isValidUsername(String username){


        if((username==null)||(username.length()<6)||(username.length()>14)){
            System.out.println("Username is not the correct length. " +
                    "Make sure you are entering a username between 6 and 14 characters long. " +
                    "Please try again.");
            return false;
        }
        else if(ValleyBikeSim.accountMapsContain(username, 3)){
            // we chose to demand unique usernames within our system as a whole;
            // no usernames can match, even between customer and internal accounts
            System.out.println("This username already exists within our system. Please try again.");
            return false;
        }
        return true;
    }

    /**
     * Validates if password is between 6 and 14 characters
     * @param password is the password input by the user
     * @return true if password is valid and false otherwise
     */
    public static boolean isValidPassword(String password){
        if ((password == null)||(password.length()<6)||(password.length()>14)) {
            System.out.println("Password is not the correct length. Please try again.");
            return false;
        }
        return true;
    }

    /**
     * Validates credit card number input by user
     * @param ccNumber is the credit card input by the user
     * @return true if credit card is valid and false otherwise
     */
    static boolean isValidCreditCard(String ccNumber){
        // checks that input is 16 chars/digits long
        if ((ccNumber == null)||(ccNumber.length() != 16)) {
            System.out.println("This is not the correct number of digits." +
                    " Please make sure you are entering 16 digits exactly.");
            return false;
        }
        try { // checks that input is only ints
            Double num = Double.parseDouble(ccNumber); // attempt to convert to a double
        } catch (NumberFormatException nfe) { // this exception is thrown if there are non-int chars
            System.out.println("This is not a valid credit card number. " +
                    "Please make sure you are entering 16 digits without spaces, dashes, or characters. ");
            return false;
        }
        //90% of the time the method accepts the credit card
        //10% of the time the method rejects the credit card
        //This method makes a random decision and is not a real credit card validator
        return Math.random() <= 0.95;
    }

    /**
     * Checks if the email address input by the user is valid
     *
     * @param emailAddress is the email address input by the user
     *
     * @return true if email address is valid and false otherwise
     *
     * Note: made this public for the sake of testing
     */
    public static boolean isValidEmail(String emailAddress) {
        //regular expression to check format of the email address string input by user
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);

        //if the string is null or too long, return false
        if ((emailAddress == null)||(emailAddress.length()>50)){
            System.out.println("This email address is not the correct length. " +
                    "Make sure you are entering an email address that is between 5 and 50 characters long. " +
                    "Please try again.");
            return false;
        }

        //if the string does not match the pattern from the regex, return false
        else if (! pat.matcher(emailAddress).matches()){
            System.out.println("This is not a valid email address. " +
                    "Make sure you are entering an email that follows " +
                    "the pattern ssss@ssss.ssss (where ssss is a string of characters). " +
                    "Please try again.");
            return false;
        }
        //if string passes all these tests, return true
        return true;
    }

    //TODO should we make an isValidStation and isValidBike too?

    /**
     * Helper method to check if input is between two values
     *
     * @param num - the number to be validated
     * @param a - the smallest value accepted for num
     * @param b - the largest value accepted for num
     * @return return true if a <= num <= b
     */
    static boolean isIntBetween(int num, int a, int b){
        while(num < a || num > b){
            System.out.println("That is not a valid response. Please try again.");
            return false;
        }
        return true;
    }

    /**
     * We are not currently using this method.
     * Calls ValleyBikeSim's resolveData method to resolve ride data
     *
     * @throws IOException
     * @throws ParseException
     */
    //    public void resolveData() throws IOException, ParseException {
    //        String dataFile = input.next();
    //        ValleyBikeSim.resolveData(dataFile);
    //    }
}
