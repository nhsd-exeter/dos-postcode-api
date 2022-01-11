package uk.nhs.digital.uec.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import uk.nhs.digital.uec.api.filter.LocalAccessTokenFilter;

@Profile("local")
@Configuration
public class LocalConfig extends WebSecurityConfigurerAdapter {

  @Autowired private LocalAccessTokenFilter localAccessTokenFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.addFilterBefore(localAccessTokenFilter, AbstractPreAuthenticatedProcessingFilter.class)
        .cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/**")
        .permitAll();
  }
}
