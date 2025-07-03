package com.books.api.controller.book;

import com.books.api.config.Config; // Importar a classe Config
import com.books.api.model.Account;
import com.books.api.model.Book;
import com.books.api.repository.AccountRepository;
import com.books.api.repository.BookRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class RegisterBookController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;
    private final Config config; // Injetar a classe Config

    @PostMapping("/register")
    @Transactional // Garante que a operação de salvamento seja atômica
    public ResponseEntity<?> registerBook(@RequestBody Map<String, Object> requestBody, HttpServletRequest httpRequest) {

        /** Desabilitado para experimentos
        // 1. Autenticação e Autorização do Usuário
        Account loggedUser = jwtUtil.getLoggedUser(httpRequest, accountRepository);
        if (loggedUser == null || loggedUser.getStatus() != Account.Status.ON) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não autenticado ou inativo."));
        }

        // Verificar a role do usuário (ADMIN ou OPERATOR/EMPLOYE)
        if (loggedUser.getRole() != Account.Role.ADMIN && loggedUser.getRole() != Account.Role.OPERATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("403", "Acesso negado. Requer role de ADMIN ou OPERATOR para cadastrar livros."));
        }
        **/

        // 2. Extrair e Validar Dados da Requisição (Manual)
        String title = (String) requestBody.get("title");
        String author = (String) requestBody.get("author");
        int publicationYear;
        BigDecimal price;
        int stock;
        Book.Status status; // Pode ser fornecido ou default ON
        Boolean ebook = (Boolean) requestBody.getOrDefault("ebook", false); // Default para false

        // Validação de campos obrigatórios
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "O título do livro é obrigatório."));
        }
        if (author == null || author.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "O autor do livro é obrigatório."));
        }

        Object pubYearObj = requestBody.get("publicationYear");
        if (pubYearObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "O ano de publicação é obrigatório."));
        }
        try {
            if (pubYearObj instanceof Integer) {
                publicationYear = (Integer) pubYearObj;
            } else if (pubYearObj instanceof String) {
                publicationYear = Integer.parseInt((String) pubYearObj);
            } else {
                throw new ClassCastException();
            }
        } catch (ClassCastException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Ano de publicação deve ser um número inteiro válido."));
        }

        Object priceObj = requestBody.get("price");
        if (priceObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "O preço do livro é obrigatório."));
        }
        try {
            if (priceObj instanceof Number) {
                price = BigDecimal.valueOf(((Number) priceObj).doubleValue());
            } else if (priceObj instanceof String) {
                price = new BigDecimal((String) priceObj);
            } else {
                throw new ClassCastException();
            }
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "O preço não pode ser negativo."));
            }
        } catch (ClassCastException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Preço deve ser um número válido."));
        }

        Object stockObj = requestBody.get("stock");
        if (stockObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "O estoque do livro é obrigatório."));
        }
        try {
            if (stockObj instanceof Integer) {
                stock = (Integer) stockObj;
            } else if (stockObj instanceof String) {
                stock = Integer.parseInt((String) stockObj);
            } else {
                throw new ClassCastException();
            }
            if (stock < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "O estoque não pode ser negativo."));
            }
        } catch (ClassCastException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Estoque deve ser um número inteiro válido."));
        }

        // Status (opcional, default ON)
        Object statusObj = requestBody.get("status");
        if (statusObj != null) {
            try {
                status = Book.Status.valueOf(((String) statusObj).toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("400", "Status inválido. Use 'ON' ou 'OFF'."));
            }
        } else {
            status = Book.Status.ON; // Default para ON se não fornecido
        }

        // 3. Criar a entidade Book
        Book newBook = Book.builder()
                .title(title)
                .author(author)
                .isbn((String) requestBody.get("isbn"))
                .publisher((String) requestBody.get("publisher"))
                .publicationYear(publicationYear)
                .editionNumber((Integer) requestBody.get("editionNumber"))
                .numberOfPages((Integer) requestBody.get("numberOfPages"))
                .genre((String) requestBody.get("genre"))
                .format((String) requestBody.get("format"))
                .accessUrl((String) requestBody.get("accessUrl"))
                .fileFormat((String) requestBody.get("fileFormat"))
                .hasDrm((Boolean) requestBody.get("hasDrm"))
                .language((String) requestBody.get("language"))
                .synopsis((String) requestBody.get("synopsis"))
                .coverImageUrl(config.getDefaultBookCover()) // Usar o valor padrão da Config
                .status(status)
                .ebook(ebook)
                .price(price)
                .stock(stock)
                .createdAt(LocalDateTime.now()) // Definir createdAt explicitamente aqui
                .build();

        // 4. Salvar o novo livro
        newBook = bookRepository.save(newBook);

        // 5. Retornar Resposta de Sucesso
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", newBook.getId());
        responseData.put("title", newBook.getTitle());
        responseData.put("author", newBook.getAuthor());
        responseData.put("isbn", newBook.getIsbn());
        responseData.put("publisher", newBook.getPublisher());
        responseData.put("publicationYear", newBook.getPublicationYear());
        responseData.put("status", newBook.getStatus().name());
        responseData.put("price", newBook.getPrice());
        responseData.put("stock", newBook.getStock());
        responseData.put("createdAt", newBook.getCreatedAt());
        responseData.put("coverImageUrl", newBook.getCoverImageUrl()); // Incluir no response para confirmação

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Livro registrado com sucesso.", responseData));
    }
}
