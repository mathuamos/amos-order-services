package com.payment.gateway.security.config.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        //Authentication failed, send error response.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

      /*  PrintWriter writer = response.getWriter();
        writer.println("HTTP Status 401 : " + authException.getMessage());*/

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        try {
            response.getWriter().write(new JSONObject() //my util class for creating json strings
                    .put("timestamp", System.currentTimeMillis())
                    .put("status", 401)
                    .put("message", "Un-authorized request.")
                    .toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void afterPropertiesSet() {
        setRealmName("API");
        super.afterPropertiesSet();
    }


}
