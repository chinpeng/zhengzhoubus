package com.jpsycn.wggl.mobile.androidpn;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Scope("singleton")
@Component("wgglApplicationContext")
public class MyApplicationContext implements ApplicationContextAware ,ServletContextAware{

	private static ApplicationContext context;
	private static ServletContext servletContext;
	
	@Override
	public void setServletContext(ServletContext arg0) {
		servletContext=arg0;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		context=arg0;
	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}
	

}
