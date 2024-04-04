package com.ys.authentication.domain.core.fixture;

import java.time.LocalDateTime;
import java.util.Date;

public class SupportAuthenticationInfoFixture {
    protected static final Long AUTHENTICATION_INFO_ID = 99999999999L;
    protected static final Long USER_ID = 99999999999L;
    protected static final String REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwidXNlcklkIjoxMjMsIm5hbWUiOiJBTllfTkFNRSIsInJvbGVzIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJpYXQiOjE2OTcwMzQwNTAsImV4cCI6MTcyODU3MDA1MH0.BLrV10sirnz4wcdv9-MY3rkwa1qehj4pYT8rJbOdtAY";
    protected static final Date NOW_DATE = new Date();
    protected static final Date EXPIRED_AT = new Date(NOW_DATE.getTime() + 31536000000L);
    protected static final String CLIENT_IP = "127.0.0.1";
    protected static final LocalDateTime NOW = LocalDateTime.now();
}
