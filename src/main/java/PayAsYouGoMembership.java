public class PayAsYouGoMembership extends Membership {

    PayAsYouGoMembership() {
        super();
    }

    //returns int which represents PAYG membership
    public int getMembershipInt(){
        return 1;
    }

    //returns string with membership type
    public String getMembershipString(){
        return "Pay Per Ride";
    }
}
