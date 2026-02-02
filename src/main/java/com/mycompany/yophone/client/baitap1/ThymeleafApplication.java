package com.mycompany.yophone.client.baitap1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Ung dung web don gian su dung Thymeleaf va Java HTTP Server
 */
public class ThymeleafApplication {

    private static final int PORT = 8080;
    private static TemplateEngine templateEngine;
    private static BookController bookController;

    public static void main(String[] args) {
        try {
            // Khoi tao Thymeleaf Template Engine
            templateEngine = createTemplateEngine();
            bookController = new BookController();

            // Tao HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Dang ky cac routes
            server.createContext("/", new RootHandler());
            server.createContext("/books", new BooksHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("========================================");
            System.out.println("  Ung dung Quan Ly Sach da khoi dong!");
            System.out.println("  Truy cap: http://localhost:" + PORT + "/books");
            System.out.println("========================================");

            // Giu server chay
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Loi khoi dong server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    // Handler cho root path
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                redirect(exchange, "/books");
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        }
    }

    // Handler cho tat ca cac duong dan /books/*
    static class BooksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            System.out.println("Request: " + method + " " + path);

            try {
                if (path.equals("/books") || path.equals("/books/")) {
                    handleListBooks(exchange);
                } else if (path.equals("/books/add")) {
                    handleAddBook(exchange, method);
                } else if (path.startsWith("/books/edit/")) {
                    handleEditBook(exchange, method, path);
                } else if (path.startsWith("/books/delete/")) {
                    handleDeleteBook(exchange, path);
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            } catch (Exception e) {
                System.err.println("Loi xu ly request: " + e.getMessage());
                e.printStackTrace();
                String errorHtml = "<html><body><h1>Loi: " + e.getMessage() + "</h1></body></html>";
                sendResponse(exchange, errorHtml, 500);
            }
        }
    }

    private static void handleListBooks(HttpExchange exchange) throws IOException {
        Map<String, Object> model = bookController.listBooks();
        Context context = new Context();
        model.forEach(context::setVariable);

        String html = templateEngine.process("books", context);
        sendResponse(exchange, html, 200);
    }

    private static void handleAddBook(HttpExchange exchange, String method) throws IOException {
        if ("GET".equalsIgnoreCase(method)) {
            Map<String, Object> model = bookController.showAddForm();
            Context context = new Context();
            model.forEach(context::setVariable);

            String html = templateEngine.process("add-book", context);
            sendResponse(exchange, html, 200);
        } else if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> params = parseFormData(exchange);
            String title = params.getOrDefault("title", "");
            String author = params.getOrDefault("author", "");
            long price = Long.parseLong(params.getOrDefault("price", "0"));

            bookController.addBook(title, author, price);
            redirect(exchange, "/books");
        }
    }

    private static void handleEditBook(HttpExchange exchange, String method, String path) throws IOException {
        // Lay ID tu URL: /books/edit/1
        String[] parts = path.split("/");
        if (parts.length < 4) {
            redirect(exchange, "/books");
            return;
        }
        int id = Integer.parseInt(parts[3]);

        if ("GET".equalsIgnoreCase(method)) {
            Map<String, Object> model = bookController.showEditForm(id);
            Context context = new Context();
            model.forEach(context::setVariable);

            String html = templateEngine.process("edit-book", context);
            sendResponse(exchange, html, 200);
        } else if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> params = parseFormData(exchange);
            String title = params.getOrDefault("title", "");
            String author = params.getOrDefault("author", "");
            long price = Long.parseLong(params.getOrDefault("price", "0"));

            bookController.updateBook(id, title, author, price);
            redirect(exchange, "/books");
        }
    }

    private static void handleDeleteBook(HttpExchange exchange, String path) throws IOException {
        // Lay ID tu URL: /books/delete/1
        String[] parts = path.split("/");
        if (parts.length < 4) {
            redirect(exchange, "/books");
            return;
        }
        int id = Integer.parseInt(parts[3]);

        bookController.deleteBook(id);
        redirect(exchange, "/books");
    }

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> params = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String formData = reader.lines().reduce("", (a, b) -> a + b);
            for (String pair : formData.split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(
                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    );
                }
            }
        }
        return params;
    }

    private static void sendResponse(HttpExchange exchange, String html, int statusCode) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
}
