// BookManager.aidl
package com.malin.client;
import com.malin.client.Book;

interface BookManager {

    List<Book> getBooks();
    void addBook(inout Book book);
}
