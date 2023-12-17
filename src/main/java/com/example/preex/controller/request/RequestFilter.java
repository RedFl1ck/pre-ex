package com.example.preex.controller.request;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Фильтр, добавляющий тело в ответ.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@Component
public class RequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest wrappedRequest = new RequestWrapper((HttpServletRequest) request);

        chain.doFilter(wrappedRequest, response);
    }
}
