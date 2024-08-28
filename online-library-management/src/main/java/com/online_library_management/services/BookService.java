package com.online_library_management.services;

import com.online_library_management.models.Author;
import com.online_library_management.models.Book;
import com.online_library_management.models.Genre;
import com.online_library_management.repositories.AuthorRepository;
import com.online_library_management.repositories.BookRepository;
import com.online_library_management.repositories.GenreRepository;
import com.online_library_management.repositories.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private GenreRepository genreRepository;


    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book saveBook(Book book) {

        Set<Author> managedAuthors = new HashSet<>();
        for (Author author : book.getAuthors()) {
            Author managedAuthor = authorRepository.findById(author.getId()).orElseThrow(() -> new RuntimeException("Autor nije pronadjen"));
            managedAuthors.add(managedAuthor);
        }
        book.setAuthors(managedAuthors);


        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book book) {

        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Knjiga nije pronadjena"));


        Genre genre = genreRepository.findById(book.getGenre().getId())
                .orElseThrow(() -> new RuntimeException("Žanr nije pronadjen"));
        existingBook.setGenre(genre);


        Set<Author> managedAuthors = new HashSet<>();
        for (Author author : book.getAuthors()) {
            Author managedAuthor = authorRepository.findById(author.getId())
                    .orElseThrow(() -> new RuntimeException("Autor nije pronadjen"));
            managedAuthors.add(managedAuthor);
        }
        existingBook.setAuthors(managedAuthors);


        existingBook.setTitle(book.getTitle());
        existingBook.setIsbn(book.getIsbn());


        return bookRepository.save(existingBook);
    }
    

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Knjiga nije pronadjena"));


        Set<Author> authors = new HashSet<>(book.getAuthors());
        for (Author author : authors) {
            author.getBooks().remove(book);
        }
        book.setAuthors(null);


        reviewRepository.deleteAll(book.getReviews());


        bookRepository.delete(book);
    }

}
