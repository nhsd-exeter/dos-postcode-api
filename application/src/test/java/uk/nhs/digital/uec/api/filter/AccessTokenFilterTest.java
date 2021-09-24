package uk.nhs.digital.uec.api.filter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.exception.AccessTokenExpiredException;
import uk.nhs.digital.uec.api.util.JwtUtil;

@ExtendWith(SpringExtension.class)
public class AccessTokenFilterTest {

  @Mock private AccessTokenFilter filter;
  @Mock private JwtUtil jwtUtil;

  @Test
  public void testDoFilter() throws ServletException, IOException, AccessTokenExpiredException {

    HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
    FilterChain mockFilterChain = Mockito.mock(FilterChain.class);
    filter.doFilterInternal(mockReq, mockResp, mockFilterChain);
    verify(filter, times(1)).doFilterInternal(mockReq, mockResp, mockFilterChain);
    filter.destroy();
  }
}
