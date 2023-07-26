package com.ll.spring_additional.standard.util;

import jakarta.servlet.http.HttpServletRequest;

public class Ut {
	public static class AjaxUtils {
		public static boolean isAjaxRequest(HttpServletRequest request) {
			String header = request.getHeader("X-Requested-With");
			return "XMLHttpRequest".equals(header);
		}
	}
}