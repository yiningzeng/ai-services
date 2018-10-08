package com.galileoai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


/**
 * Created by baymin on 17-8-4.
 */
@Component
@Slf4j
public class TokenAuthorFilter implements Filter {



    //@Autowired
    //private WebSecurityConfig webSecurityConfig;
    //@Value("${server.filter.login-url}")
    //private String loginUrl;

    /**
     * kuayu-origin: japi.waterever.cn
     * kuayu-methods: japi.waterever.cn
     * kuayu-max-age: 3600
     * kuayu-headers: Origin, X-Requested-With, Content-Type, Accept
     */
    @Value("${kuayu-origin}")
    private String origin;
    @Value("${kuayu-methods}")
    private String methods;
    @Value("${kuayu-max-age}")
    private String maxAge;
    @Value("${kuayu-headers}")
    private String headers;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String authHeader = request.getHeader("authorization");



        //region 允许跨域
        response.setHeader("Access-Control-Allow-Origin", origin);
        //允许的访问方法
        response.setHeader("Access-Control-Allow-Methods", methods);
        //Access-Control-Max-Age 用于 CORS 相关配置的缓存
        response.setHeader("Access-Control-Max-Age", maxAge);
        response.setHeader("Access-Control-Allow-Headers", headers);

        log.info("~\n");

        log.info("########TokenFilterFirst########");

        String uri = request.getRequestURI(); //uri就是获取到的连接地址!
        log.info("now url:" + uri);
        filterChain.doFilter(servletRequest, response);

    }

    @Override
    public void destroy() {
    }
}

