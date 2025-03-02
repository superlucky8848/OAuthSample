package net.superluckyworks.oauthsample.resource_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ResourceServerApplicationTests extends BasicTests
{
	@Test
	void contextLoads() 
	{
		testStart();

		logger.info("contextLoads");

		testEnd();
	}
}
