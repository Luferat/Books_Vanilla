package com.books.api.repository;

import com.books.api.model.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactsRepository extends JpaRepository<Contacts, Long> {
}
