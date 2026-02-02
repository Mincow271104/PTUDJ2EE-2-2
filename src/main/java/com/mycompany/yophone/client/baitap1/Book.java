/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.yophone.client.baitap1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author HP
 */
public class Book {

    private int id;
    private String title;
    private String author;
    private long price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void input() {
        Scanner x = new Scanner(System.in);
        System.out.println("Nhap ma sach");
        this.id = Integer.parseInt(x.nextLine());
        System.out.println("Nhap ten sach");
        this.title = x.nextLine();
        System.out.println("Nhap tac gia");
        this.author = x.nextLine();
        System.out.println("Nhap don gia");
        this.price = x.nextLong();
    }

    public void output() {
        String msg = """
               BOOK: id = %d, title = %s, author = %s, price = %d""".formatted(id, title, author, price);
        System.out.println(msg);
    }

    public static void main(String[] args) {
        List<Book> listBook = new ArrayList<>();
        Scanner x = new Scanner(System.in);
        String msg = """
                     Chuong trinh quan ly sach
                     1. Them 1 cuon sach
                     2. Xoa 1 cuon sach
                     3. Thay doi sach
                     4. Xuat thong tin
                     5. Tim sach Lap Trinh
                     6. Lay sach toi da theo gia
                     7. Tim kiem theo tac gia
                     0. Thoat
                     Chon chuc nang
                     """;
        int chon = 0;
        do {
            System.out.printf(msg);
            chon = x.nextInt();
            switch (chon) {
                case 1 -> {
                    Book newBook = new Book();
                    newBook.input();
                    listBook.add(newBook);
                }
                case 2 -> {
                    System.out.println("Nhap vao ma sach can xoa: ");
                    int bookid = x.nextInt();
                    Book find = listBook.stream().filter(p -> p.getId() == bookid).findFirst().orElseThrow();
                    listBook.remove(find);
                    System.out.println("Xoa sach thanh cong");
                }
                case 3 -> {
                    System.out.print("Nhap vao ma sach can dieu chinh: ");
                    int bookid = x.nextInt();
                    Book find = listBook.stream().filter(p -> p.getId() == bookid).findFirst().orElseThrow();
                    // Chưa có phần sửa thông tin → bạn có thể bổ sung sau
                }
                case 4 -> {
                    System.out.print("\n Xuat thong tin danh sach: ");
                    listBook.forEach(p -> p.output());
                }
                case 5 -> {
                    List<Book> list5 = listBook.stream()
                            .filter(u -> u.getTitle().toLowerCase().contains("lap trinh"))
                            .toList();
                    list5.forEach(Book::output);
                }
                case 6 -> {
                    if (listBook.isEmpty()) {
                        System.out.println("Danh sach sach rong");
                    } else {
                        long maxPrice = listBook.stream()
                                .mapToLong(Book::getPrice)
                                .max()
                                .orElse(0);

                        List<Book> maxBooks = listBook.stream()
                                .filter(book -> book.getPrice() == maxPrice)
                                .toList();

                        System.out.println("Sach co gia cao nhat (" + maxPrice + "):");
                        maxBooks.forEach(Book::output);
                    }
                }
                case 7 -> {
                    System.out.print("Nhap vao ten tac gia can tim sach: ");
                    x.nextLine();  // Ăn sạch newline thừa
                    String bookau = x.nextLine().trim();
                    List<Book> list7 = listBook.stream()
                            .filter(u -> u.getAuthor().toLowerCase().contains(bookau.toLowerCase()))
                            .toList();
                    if (list7.isEmpty()) {
                        System.out.println("Khong tim thay sach cua tac gia nay. Thu lai di!");
                    } else {
                        list7.forEach(Book::output);
                    }
                }
            }
        } while (chon != 0);
    }
}