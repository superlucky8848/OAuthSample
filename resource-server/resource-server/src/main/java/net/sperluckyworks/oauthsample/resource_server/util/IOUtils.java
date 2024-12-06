package net.sperluckyworks.oauthsample.resource_server.util;

public class IOUtils 
{
    public static String fomartDuration(long duration) 
    {
        long ms = duration % 1000L;
        duration /= 1000L;
        long s = duration % 60L;
        duration /= 60L;
        long m = duration % 60L;
        duration /= 60L;
        return String.format("%02d:%02d:%02d.%03d", duration, m, s, ms);
    }
}
