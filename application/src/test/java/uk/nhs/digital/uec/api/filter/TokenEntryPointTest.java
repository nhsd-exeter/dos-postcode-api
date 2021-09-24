package uk.nhs.digital.uec.api.filter;

import static org.mockito.Mockito.doNothing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TokenEntryPointTest {

  @Mock private TokenEntryPoint tep;

  @Test
  public void testDoFilter() throws Exception {

    HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
    AuthenticationException mockFilterChain = Mockito.mock(AuthenticationException.class);
    doNothing().when(tep).commence(mockReq, mockResp, mockFilterChain);
  }
}
