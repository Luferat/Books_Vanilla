//package com.books.api.controller.books;
//
//import com.books.api.model.Book;
//import com.books.api.repository.BookRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/books")
//public class BookController {
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    // Endpoint para cadastro de livro (Novo livro)
//    @PostMapping("/new")
//    public ResponseEntity<Book> registerBook(@RequestBody Book book) {
//        // Salvando o livro no banco
//        Book savedBook = bookRepository.save(book);
//        // Retornando resposta com o livro criado
//        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
//    }
//
//    // Endpoint para listar todos os livros cadastrados
//    @GetMapping("/list")
//    public ResponseEntity<Iterable<Book>> listBooks() {
//        // Retornando todos os livros
//        Iterable<Book> books = bookRepository.findAllByOrderByTitleAsc();
//        return new ResponseEntity<>(books, HttpStatus.OK);
//    }
//
//    // Endpoint para visualizar um livro específico pelo ID
//    @GetMapping("/view/{id}")
//    public ResponseEntity<Book> viewBook(@PathVariable Long id) {
//        // Procurando o livro pelo ID
//        return bookRepository.findById(id)
//                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    // Endpoint para editar um livro existente pelo ID
//    @PutMapping("/edit/{id}")
//    public ResponseEntity<Book> editBook(@PathVariable Long id, @RequestBody Book updatedBook) {
//        // Verificando se o livro existe
//        return bookRepository.findById(id)
//                .map(book -> {
//                    // Atualizando os dados do livro
//                    book.setTitle(updatedBook.getTitle());
//                    book.setAuthor(updatedBook.getAuthor());
//                    book.setPublicationYear(updatedBook.getPublicationYear());
//                    book.setPhoto(updatedBook.getPhoto());
//                    book.setGenre(updatedBook.getGenre());
//                    book.setSynopsis(updatedBook.getSynopsis());
//                    book.setStatus(updatedBook.getStatus());
//
//                    // Salvando o livro atualizado
//                    Book savedBook = bookRepository.save(book);
//                    return new ResponseEntity<>(savedBook, HttpStatus.OK);
//                })
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    // Endpoint para deletar um livro pelo ID
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
//        // Verificando se o livro existe
//        return bookRepository.findById(id)
//                .map(book -> {
//                    // Deletando o livro
//                    bookRepository.delete(book);
//                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
//                })
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//}