package demo;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import demo.VaadinApplication.LatLon;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VaadinApplication.class)
@WebAppConfiguration
public class BootVaadinApplicationTests {


	@Autowired
	private ApplicationContext applicationContext;
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
	
	@Test
	public void contextLoads() {
		
   	
    	logger.info("Sending REST Req....");
    	
    	RestTemplate restTemplate  = (RestTemplate) applicationContext.getBean("restTemplate");
    	PlaceRepository placeRepository  = (PlaceRepository) applicationContext.getBean("placeRepository");
        
        
        String ip = restTemplate.getForObject("http://icanhazip.com", String.class).trim();
        
    	//Map<?, ?> loc = restTemplate.getForObject("http://ip-api.com/json/{ip}",Map.class, ip);
        LatLon loc = restTemplate.getForObject("http://ip-api.com/json/{ip}",LatLon.class, ip);

        logger.info("the IP of the current machine is: " + ip);
        logger.info("latitude & longitude: " + loc.toString());

        logger.info("zooming in..");
        placeRepository.findByPositionNear(new Point(loc.getLon(), loc.getLat()), new Distance(3, Metrics.MILES)).forEach(System.out::println);

	}

}
