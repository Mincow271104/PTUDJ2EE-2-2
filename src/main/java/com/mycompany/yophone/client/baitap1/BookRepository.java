package com.mycompany.yophone.client.baitap1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository lưu trữ và quản lý danh sách sách (giả lập database)
 */
public class BookRepository {
    
    private static final List<Book> books = new ArrayList<>();
    private static int nextId = 1;
    
    static {
        // Thêm một số sách mẫu
        Book book1 = new Book();
        book1.setId(nextId++);
        book1.setTitle("Lập Trình Java");
        book1.setAuthor("Nguyễn Văn A");
        book1.setPrice(150000);
        books.add(book1);
        
        Book book2 = new Book();
        book2.setId(nextId++);
        book2.setTitle("Học Python Cơ Bản");
        book2.setAuthor("Trần Văn B");
        book2.setPrice(120000);
        books.add(book2);
        
        Book book3 = new Book();
        book3.setId(nextId++);
        book3.setTitle("Thuật Toán và Cấu Trúc Dữ Liệu");
        book3.setAuthor("Lê Thị C");
        book3.setPrice(200000);
        books.add(book3);
    }
    
    public static List<Book> findAll() {
        return new ArrayList<>(books);
    }
    
    public static Optional<Book> findById(int id) {
        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }
    
    public static void save(Book book) {
        if (book.getId() == 0) {
            // Thêm mới
            book.setId(nextId++);
            books.add(book);
        } else {
            // Cập nhật
            findById(book.getId()).ifPresent(existingBook -> {
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPrice(book.getPrice());
            });
        }
    }
    
    public static void deleteById(int id) {
        books.removeIf(book -> book.getId() == id);
    }
}
