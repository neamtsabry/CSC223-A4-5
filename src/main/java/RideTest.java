import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.Assert.assertEquals;
import org.junit.Assert;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;


class RideTest {
//    @Test
//    public void testIs24Hours() throws Exception {
//       Assert.assertEquals(Ride.is24hours(), false);
//    }

//    @Test
//    public void testInstant(){
//        System.out.println(ZonedDateTime.now());
//    }

    @Test
    public void testIsValidEmailAdress(){
        Assert.assertEquals(ValleyBikeController.isValidEmail("1"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail("abcd"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail("hello@"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail("a@.com"), false);
        Assert.assertEquals(ValleyBikeController.isValidEmail(null), false);
    }

    @Test
    public void testIsValidPassword(){
        Assert.assertEquals(ValleyBikeController.isValidPassword("1234567"), true);
        Assert.assertEquals(ValleyBikeController.isValidPassword("123"), false);
        Assert.assertEquals(ValleyBikeController.isValidPassword(null), false);
        Assert.assertEquals(ValleyBikeController.isValidPassword(""), false);
    }

    @Test
    public void testIsValidUsername(){
        Assert.assertEquals(ValleyBikeController.isValidUsername("asdfgh"),true);
        Assert.assertEquals(ValleyBikeController.isValidUsername("123"),false);
        Assert.assertEquals(ValleyBikeController.isValidUsername(""), false);
        Assert.assertEquals(ValleyBikeController.isValidUsername(null), false);
    }

    @Test
    public void testMapContains(){
//        Assert.assertEquals(ValleyBikeSim.bikesMapContains(123456789), false);
//        Assert.assertEquals(ValleyBikeSim.stationsMapContains(123456789), false);
    }

}