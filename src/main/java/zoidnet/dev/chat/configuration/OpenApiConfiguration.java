package zoidnet.dev.chat.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfiguration {

    @Autowired
    private BuildProperties buildProperties;


    @Bean()
    public OpenAPI customOpenAPI() {
        String appName = buildProperties.getArtifact();
        String appVersion = buildProperties.getVersion();
        String description = appName + " documentation";
        License license = new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0");

        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description(description)
                        .license(license)
                );
    }

}
