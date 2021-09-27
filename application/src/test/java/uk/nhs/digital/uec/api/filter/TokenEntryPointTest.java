package uk.nhs.digital.uec.api.filter;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
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
  public void testCommence() throws IOException {

    HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
    AuthenticationException mockFilterChain = Mockito.mock(AuthenticationException.class);
    doNothing().when(tep).commence(mockReq, mockResp, mockFilterChain);
    tep.commence(mockReq, mockResp, mockFilterChain);
    verify(tep, times(1)).commence(mockReq, mockResp, mockFilterChain);
  }
}
