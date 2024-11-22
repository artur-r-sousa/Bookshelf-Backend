package com.bookshelf.bookshelf.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.Collectors;
import com.bookshelf.bookshelf.model.Category;

@Service
public class BookService {

    private final WebClient webClient;

    public BookService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com/books/v1").build();
    }

    public Mono<String> searchBooks(String query, int startIndex, int maxResults) {
        String uri = UriComponentsBuilder.fromUriString("/volumes")
                .queryParam("q", query)
                .queryParam("startIndex", startIndex)
                .queryParam("maxResults", maxResults)
                .queryParam("key", "AIzaSyBKm7Dhf1rKc-Iba8z2UmNqK42Gx5L71fI") 
                .toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> searchBestsellers(int maxResults) {
        String uri = UriComponentsBuilder.fromUriString("/volumes")
                .queryParam("q", "bestseller+books+most+popular")
                .queryParam("orderBy", "relevance")
                .queryParam("maxResults", maxResults)
                .toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> searchBooksByCategory(List<Category> categories, int maxResults) {
        List<Category> filteredCategories = categories.stream()
                .filter(c -> c.getId().equals(1L))
                .collect(Collectors.toList());

        if (filteredCategories.isEmpty()) {
            return Mono.just("Categoria não encontrada.");
        }

        String categoriesQuery = filteredCategories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(" OR "));

        String queryWithAdditionalTerms = categoriesQuery + " bestseller";
        String uri = UriComponentsBuilder.fromUriString("/volumes")
                .queryParam("q", queryWithAdditionalTerms)
                .queryParam("maxResults", maxResults)
                .queryParam("orderBy", "relevance")
                .toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> getBookDetails(String bookId) {
        String uri = UriComponentsBuilder.fromUriString("/volumes/{bookId}")
                .buildAndExpand(bookId)
                .toUriString();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
