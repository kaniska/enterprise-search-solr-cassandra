package com.enterprise.app.mediscene.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Cross origin request filter, to allow 
 * secured access to the rest requests from corss domain ajax calls.
 * 
 * @author santhoshgandhe
 *
 */
public class CORSFilter extends OncePerRequestFilter {
		 
	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	                                    throws ServletException, IOException {
	 
	        response.addHeader("Access-Control-Allow-Origin", "*");
	 
	        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
	            // CORS "pre-flight" request
	            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
	            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
	            response.addHeader("Access-Control-Max-Age", "1");// 30 min
	        }
	        filterChain.doFilter(request, response);
	    }
	}

