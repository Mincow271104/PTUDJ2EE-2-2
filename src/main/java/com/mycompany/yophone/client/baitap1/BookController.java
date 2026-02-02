package com.mycompany.yophone.client.baitap1;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller xử lý các request liên quan đến Book
 */
public class BookController {
    
    /**
     * GET /books - Hiển thị danh sách tất cả sách
     */
    public Map<String, Object> listBooks() {
        Map<String, Object> model = new HashMap<>();
        model.put("books", BookRepository.findAll());
        model.put("pageTitle", "Danh Sách Sách");
        return model;
    }
    
    /**
     * GET /books/add - Hiển thị form thêm sách mới
     */
    public Map<String, Object> showAddForm() {
        Map<String, Object> model = new HashMap<>();
        model.put("book", new Book());
        model.put("pageTitle", "Thêm Sách Mới");
        return model;
    }
    
    /**
     * POST /books/add - Xử lý thêm sách mới
     */
    public String addBook(String title, String author, long price) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        BookRepository.save(book);
        return "redirect:/books";
    }
    
    /**
     * GET /books/edit/{id} - Hiển thị form sửa sách
     */
    public Map<String, Object> showEditForm(int id) {
        Map<String, Object> model = new HashMap<>();
        Optional<Book> book = BookRepository.findById(id);
        if (book.isPresent()) {
            model.put("book", book.get());
            model.put("pageTitle", "Sửa Thông Tin Sách");
        } else {
            model.put("error", "Không tìm thấy sách với ID: " + id);
        }
        return model;
    }
    
    /**
     * POST /books/edit/{id} - Xử lý cập nhật sách
     */
    public String updateBook(int id, String title, String author, long price) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        BookRepository.save(book);
        return "redirect:/books";
    }
    
    /**
     * GET /books/delete/{id} - Xử lý xóa sách
     */
    public String deleteBook(int id) {
        BookRepository.deleteById(id);
        return "redirect:/books";
    }
}
