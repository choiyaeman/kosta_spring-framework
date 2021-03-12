package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

//@WebFilter(initParams = ~~~utf-8)
public class CharacterEncodingFilter implements Filter {
	private String encoding = "";
	public void init(FilterConfig fConfig) throws ServletException {
		encoding = fConfig.getInitParameter("encoding");
	}

	//모든 요청시에 자동호출됨
	public void doFilter(ServletRequest request, 
			             ServletResponse response, 
			             FilterChain chain) throws IOException, ServletException {
		
		request.setCharacterEncoding(encoding);
		chain.doFilter(request, response);
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}
	
}
