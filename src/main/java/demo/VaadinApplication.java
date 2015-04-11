package demo;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.MediaType;

import com.vaadin.ui.UI;

@SpringBootApplication
public class VaadinApplication extends SpringBootServletInitializer {// implements EmbeddedServletContainerCustomizer{
//public class VaadinApplication implements EmbeddedServletContainerCustomizer {	
	
	///// for web work

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VaadinApplication.class);
    }
    
//    @Override
//    public void customize(ConfigurableEmbeddedServletContainer container) {
//      //Enabled UTF-8 as the default character encoding for static HTML resources.
//      //If you would like to disable this comment out the 3 lines below or change
//      //the encoding to whatever you would like.
//      MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
//      mappings.add("html", "text/html;charset=utf-8");
//      container.setMimeMappings(mappings );
//    }
	
	////

    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Bean
    Facebook facebook(@Value("${facebook.appId}") String appId,
                      @Value("${facebook.appSecret}") String appSecret) {
        return new FacebookTemplate(appId + '|' + appSecret);
    }
    
   @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*The only objective of this is to init the database with data*/
    //@Bean
    CommandLineRunner init(RestTemplate restTemplate,
                           Facebook facebook,
                           PlaceRepository placeRepository) {
        return args -> {

            String ip = restTemplate.getForObject("http://icanhazip.com", String.class).trim();
            
        	//Map<?, ?> loc = restTemplate.getForObject("http://ip-api.com/json/{ip}",Map.class, ip);
            LatLon loc = restTemplate.getForObject("http://ip-api.com/json/{ip}",LatLon.class, ip);

            logger.info("the IP of the current machine is: " + ip);
            logger.info("latitude & longitude: " + loc.toString());

            placeRepository.deleteAll();

            logger.info("all records near current IP:");

            //Arrays.asList("Starbucks", "Philz", "Ike's Place", "Bite", "Umami").forEach(q ->
            Arrays.asList("KFC", "Saadeddin", "Romansia","Little Caesars").forEach(q ->
                    facebook.placesOperations()
                            .search(q, loc.getLat(), loc.getLon(), 5000 - 1).stream()
                            .map(p -> placeRepository.save(new Place(p)))
                            .forEach(System.out::println));

            logger.info("zooming in..");
            placeRepository.findByPositionNear(new Point(loc.getLon(), loc.getLat()), new Distance(3, Metrics.MILES)).forEach(System.out::println);
        };
    }

    
//    @Bean
//    EmbeddedServletContainerCustomizer servletContainerCustomizer() {
//        return servletContainer -> (
//        		(EmbeddedServletContainerFactory) servletContainer)
//                .addConnectorCustomizers(connector -> {
//                    AbstractHttp11Protocol httpProtocol = (AbstractHttp11Protocol) connector
//                            .getProtocolHandler();
//                    httpProtocol.setCompression("on");
//                    httpProtocol.setCompressionMinSize(256);
//                    String mimeTypes = httpProtocol.getCompressableMimeTypes();
//                    String mimeTypesWithJson = mimeTypes + ","
//                            + MediaType.APPLICATION_JSON_VALUE
//                            + ",application/javascript";
//                    httpProtocol.setCompressableMimeTypes(mimeTypesWithJson);
//                });
//    }
    
    public static void main(String[] args) {
        
    	SpringApplication.run(VaadinApplication.class, args);
    	
    	
        
    }
    
    
    static class LatLon {
        float lon, lat;

        public LatLon() {
        }

        public float getLon() {
            return lon;
            //return (float)46.713001;
        }

        public float getLat() {
            return lat;
            //return (float)24.672001;
        }

        @Override
        public String toString() {
            return "LatLon{" +
                    "longitude=" + lon +
                    ", latitude=" + lat +
                    '}';
        }

        public LatLon(float longitude, float latitude) {
            this.lon = longitude;
            this.lat = latitude;
        }

    }
    
    

}




@RestController
class GreetingController {

    @RequestMapping("/hello/{name}")
    String hello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }
} 
