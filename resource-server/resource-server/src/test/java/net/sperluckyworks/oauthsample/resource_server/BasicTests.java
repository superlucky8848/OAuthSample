package net.sperluckyworks.oauthsample.resource_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sperluckyworks.oauthsample.resource_server.util.IOUtils;

public abstract class BasicTests 
{
    protected Logger logger;
    private long startTick;
    
    public BasicTests()
    {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public BasicTests(Logger logger)
    {
        this.logger = logger;
    }

    protected void testStart()
    {
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        testStart(methodName);
    }

    protected void testEnd() 
    {
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        testEnd(methodName);
    }

    protected void testStart(String methodName) 
    {
        logger.info("=== Start {} ===", methodName);
        startTick = System.currentTimeMillis();
    }

    protected void testEnd(String methodName) 
    {
        long duration = System.currentTimeMillis() - startTick;
        logger.info("=== End {} ===", methodName);
        logger.info("Time elapsed: {}", IOUtils.fomartDuration(duration));
    }
}
