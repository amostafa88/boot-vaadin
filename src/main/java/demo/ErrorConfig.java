package demo;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;;

@Configuration
public class ErrorConfig implements EmbeddedServletContainerCustomizer {
    @Override
    public void customize(ConfigurableEmbeddedServletContainer factory) {
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
        factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500"));
        
      MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
      mappings.add("html", "text/html;charset=utf-8");
      factory.setMimeMappings(mappings );
      System.out.println("------------------ EmbeddedServletContainerCustomizer customization -------------------");
    }

}
