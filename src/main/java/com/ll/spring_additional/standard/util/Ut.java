package com.ll.spring_additional.standard.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

public class Ut {
	public static class AjaxUtils {
		public static boolean isAjaxRequest(HttpServletRequest request) {
			String header = request.getHeader("X-Requested-With");
			return "XMLHttpRequest".equals(header);
		}
	}

	public static class hash {
		private static final MessageDigest md;

		static {
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		public static String sha256(String str) {
			// Convert the input string to bytes and update the MessageDigest
			byte[] inputBytes = str.getBytes(StandardCharsets.UTF_8);
			byte[] hashBytes = md.digest(inputBytes);

			// Convert the hashed bytes to a Base64 encoded string
			return Base64.getEncoder().encodeToString(hashBytes);
		}
	}
}