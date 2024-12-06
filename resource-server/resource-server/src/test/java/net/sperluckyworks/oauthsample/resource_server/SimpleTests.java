package net.sperluckyworks.oauthsample.resource_server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class SimpleTests extends BasicTests
{
    @Test
    public void overloadTest()
    {
        testStart();

        String result = overLoad(new Date());
        logger.info(result);
        assertEquals("OverLoad<T>", result);

        result = overLoad("String");
        logger.info(result);
        assertEquals("OverLoad<String>", result);

        result = overLoad(15);
        logger.info(result);
        assertEquals("OverLoad<Integer>", result);

        result = overLoad(15L);
        logger.info(result);
        assertEquals("OverLoad<Long>", result);

        testEnd();
    }

    private <T> String overLoad(T param)
    {
        return "OverLoad<T>";
    }

    private <T> String overLoad(String param) 
    {
        return "OverLoad<String>";
    }

    private <T> String overLoad(Integer param) 
    {
        return "OverLoad<Integer>";
    }

    private <T> String overLoad(Long param) 
    {
        return "OverLoad<Long>";
    }
}
