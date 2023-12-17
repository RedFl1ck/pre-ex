package com.example.preex.controller.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

/**
 * Класс-обертка для запроса.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RequestWrapper.class);

    /**
     * Переданные данные в запросе.
     */
    private String requestData = null;

    /**
     * Конструктор.
     *
     * @param request объект запроса
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);

        try (Scanner s = new Scanner(request.getInputStream()).useDelimiter("\\A")) {
            requestData = s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public ServletInputStream getInputStream() {

        StringReader reader = new StringReader(requestData);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return reader.read();
            }

            @Override
            public void setReadListener(ReadListener listener) {

                try {
                    if (!isFinished()) {
                        listener.onDataAvailable();
                    } else {
                        listener.onAllDataRead();
                    }
                } catch (IOException io) {
                    LOG.error(io.getMessage());
                }
            }

            @Override
            public boolean isReady() {
                return isFinished();
            }

            @Override
            public boolean isFinished() {
                try {
                    return reader.read() < 0;
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
                return false;
            }
        };
    }
}
