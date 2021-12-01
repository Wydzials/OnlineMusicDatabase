package pl.wydzials.onlinemusicdatabase.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

  public static String PLACEHOLDER_IMAGE_ID = "placeholder";
  public static String PLACEHOLDER_ALBUM_IMAGE_ID = "vinyl";

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**")
        .addResourceLocations("file:images/");
  }
}
