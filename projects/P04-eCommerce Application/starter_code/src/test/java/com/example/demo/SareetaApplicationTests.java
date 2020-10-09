package com.example.demo;

import com.splunk.logging.SplunkCimLogEvent;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SareetaApplicationTests {

    @Test
    @Ignore
    public void createExampleLogSplunkEvents() throws InterruptedException {
        Logger logger = LoggerFactory.getLogger("splunk.logger");
        logger.info("This is a test");
        final String[] event_names = {"event_success", "event_failure"};
        final String[] event_ids = {"0", "1"};
        Random random = new Random();
        int max = 1;
        int min = 0;
        for (int i = 0; i < 50; i++) {
            Thread.sleep(300);
            int x = random.nextInt(max - min + 1) + min;
            SplunkCimLogEvent event = new SplunkCimLogEvent(event_names[x], event_ids[x]);
            logger.info(event.toString());
        }
    }

}
