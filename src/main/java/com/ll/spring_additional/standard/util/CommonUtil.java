package com.ll.spring_additional.standard.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommonUtil {
	private final Parser parser;
	private final HtmlRenderer renderer;

	public String markdown(String markdown) {
		Node document = parser.parse(markdown);
		String html = renderer.render(document);

		System.out.println("Converted HTML: " + html);

		// Sanitize HTML
		PolicyFactory policy = new HtmlPolicyBuilder()
			.allowElements("h1", "h2", "h3", "p", "b", "i", "em", "strong", "img", "a", "ul", "ol", "li", "table", "thead", "tbody", "tr", "th", "td", "del", "blockquote", "code", "pre", "input", "hr")
			.allowUrlProtocols("https", "http")
			.allowAttributes("href", "target").onElements("a")
			.allowAttributes("src", "alt").onElements("img")
			.allowAttributes("type", "checked", "disabled").onElements("input")
			.allowAttributes("border", "cellspacing", "cellpadding").onElements("table")
			.requireRelNofollowOnLinks()
			.toFactory();

		String safeHtml = policy.sanitize(html);
		System.out.println("After sanitize: " + safeHtml);
		return safeHtml; // Return the sanitized HTML
	}
}