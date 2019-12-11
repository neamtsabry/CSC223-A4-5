import org.junit.jupiter.api.Test;
import org.junit.Assert;

import java.time.Instant;


class jUnitTest {

    /**
     * We use assertEquals in this class to compare expected out put and
     * predicted output. Test class should throw an error when running if
     * the two outputs don't match.
     *
     * NOTE
     * The three isValid methods were meant to be private since they're only used
     * in ValleyBikeController but we've changed them to public just so
     * the tests would be able to access them from here.
     */

    /**
     * This test would test is24Hours() but we've commented it out because
     * most of its work now is automatic and no manual inputs and parameters.
     */
//    @Test
//    public void testIs24Hours() throws Exception {
//       Assert.assertEquals(Ride.is24hours(), false);
//    }

    /**
     * Test to see what an instant is formatted
     */
    @Test
    public void testInstant(){
        System.out.println(Instant.now());
    }

    /**
     * tests isValidEmailAddress to see if it catches the following invalid
     * email addresses
     */
    @Test
    public void testIsValidEmailAdress(){
        Assert.assertEquals(ValleyBikeController.isValidEmail("1"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail("abcd"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail("hello@"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail("a@.com"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail(null), false);
    }

    /**
     * tests isValidPassword to see if it catches the following invalid
     * passwords
     */
    @Test
    public void testIsValidPassword(){
        Assert.assertEquals(ValleyBikeController.isValidPassword("1234567"), true);
        Assert.assertEquals(ValleyBikeController.isValidPassword("123"), false);
        Assert.assertEquals(ValleyBikeController.isValidPassword(null), false);
        Assert.assertEquals(ValleyBikeController.isValidPassword(""), false);
    }

    /**
     * tests isValidUsername to see if it catches the following invalid
     * usernames
     */
    @Test
    public void testIsValidUsername(){
        Assert.assertEquals(ValleyBikeController.isValidUsername("asdfgh", 1),true);
        Assert.assertEquals(ValleyBikeController.isValidUsername("123", 2),false);
        Assert.assertEquals(ValleyBikeController.isValidUsername("", 1), false);
        Assert.assertEquals(ValleyBikeController.isValidUsername(null, 2), false);
    }

}