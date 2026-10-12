package com.homescout.config;

import com.homescout.web.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/** The "security guard": runs before the protected pages and checks who is logged in. */
public class AccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        SessionUser user = (session == null) ? null : (SessionUser) session.getAttribute(SessionUser.KEY);

        if (user == null) {                         // not logged in at all
            response.sendRedirect("/login");
            return false;
        }

        String path = request.getRequestURI();
        boolean allowed = true;
        if (path.startsWith("/favourites")) {
            allowed = user.isBuyer();
        } else if (path.startsWith("/manage")) {
            allowed = user.isAgent() || user.isAdmin();
        }

        if (!allowed) {                             // logged in, but the wrong role
            response.sendError(403);
            return false;
        }
        return true;
    }
}
