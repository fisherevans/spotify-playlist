package com.fisherevans.spotify.playlists;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthListener extends AbstractHandler {
    private Server server;
    private Importer importer;

    private AuthListener(Importer importer) {
        this.importer = importer;
        server = new Server(importer.settings.jettyPort);
        server.setHandler(this);
    }

    public static void listen(Importer importer) {
        AuthListener listener = new AuthListener(importer);
        try {
            listener.server.start();
        } catch (Exception e) {
            System.err.println("Failed to start auth server!");
            System.exit(1);
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String code = request.getParameter("code");
        if(code != null && code.length() != 0) {
            System.out.println("Received code! " + code);
            (new Thread() {
                @Override
                public void run() {
                    try {
                        // sleep thread so server can give a good response to http client
                        Thread.sleep(100);
                        server.stop();
                        importer.doImport(code);
                    } catch (Exception e ) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        response.setStatus(200);
        response.setContentType("text/plain");
        response.getWriter().println("Got it, thanks!");
        response.getWriter().flush();
        baseRequest.setHandled(true);
    }
}
