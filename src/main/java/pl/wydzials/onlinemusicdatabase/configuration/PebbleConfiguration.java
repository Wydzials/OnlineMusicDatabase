package pl.wydzials.onlinemusicdatabase.configuration;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

@Configuration
public class PebbleConfiguration {

  public static class MyPebbleExtension extends AbstractExtension {

    private static class DurationFormat implements Filter {

      @Override
      public List<String> getArgumentNames() {
        return null;
      }

      @Override
      public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
          int lineNumber) {
        if (!(input instanceof Duration))
          Validation.throwIllegalArgumentException();

        final Duration duration = (Duration) input;
        final long minutes = duration.getSeconds() / 60;
        final long seconds = duration.getSeconds() % 60;

        return String.format("%02d:%02d", minutes, seconds);
      }
    }

    @Override
    public Map<String, Filter> getFilters() {
      return Map.of("durationFormat", new DurationFormat());
    }
  }

  @Bean
  public Extension myPebbleExtension() {
    return new MyPebbleExtension();
  }
}
