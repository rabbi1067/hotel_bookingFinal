package bd.hotel_booking.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Injects a small floating "DEMO MODE - changes are temporary" badge into
 * HTML pages for demo visitors. No template is modified.
 * <p>
 * Safety: only {@code text/html} GET page renders, never APIs, downloads,
 * images or POST redirects.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class DemoBannerFilter extends OncePerRequestFilter {

    private static final String BADGE =
            "<div id='demo-mode-badge' style='position:fixed;bottom:14px;right:14px;z-index:9999;"
            + "background:#7c3aed;color:#fff;font:600 12px/1.4 system-ui,sans-serif;"
            + "padding:8px 12px;border-radius:999px;box-shadow:0 6px 20px rgba(0,0,0,.25);"
            + "letter-spacing:.02em'>DEMO MODE &middot; changes are temporary</div>";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod()) || !DemoContext.isDemo()) {
            chain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI() == null ? "" : request.getRequestURI();
        if (uri.startsWith("/api/") || uri.startsWith("/css/") || uri.startsWith("/js/")
                || uri.startsWith("/img/") || uri.startsWith("/images/")
                || uri.startsWith("/libs/") || uri.startsWith("/scss/")
                || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png")
                || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".svg")
                || uri.endsWith(".ico") || uri.endsWith(".woff2")) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, wrapper);

        String contentType = wrapper.getContentType();
        if (contentType == null || !contentType.contains("text/html")) {
            wrapper.copyBodyToResponse();
            return;
        }
        try {
            String html = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            if (!html.isEmpty() && html.contains("</body>") && !html.contains("demo-mode-badge")) {
                html = html.replace("</body>", BADGE + "</body>");
                byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                wrapper.resetBuffer();
                wrapper.setContentLength(bytes.length);
                wrapper.getOutputStream().write(bytes);
            }
        } catch (Exception ignored) {
            // never break a page because of the badge
        }
        wrapper.copyBodyToResponse();
    }
}
