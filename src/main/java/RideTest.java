import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.Assert.assertEquals;
import org.junit.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;


class RideTest {
    @Test
    public void testIs24Hours() throws Exception {
//       Assert.assertEquals(Ride.is24hours(), false);
    }

    @Test
    public void testInstant(){
        System.out.println(ZonedDateTime.now());
    }

}