package com.example.yunyao.lab3;

/**
 * Created by yunyao on 2016/10/12.
 */
public class Book {
    private String bookName;
    private String bookPrice;

    public Book(String bookName, String bookPrice) {
        this.bookName = bookName;
        this.bookPrice = bookPrice;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookPrice() {
        return bookPrice;
    }
}
