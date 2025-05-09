package com.books.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String varName;
    private String varValue;

    @Lob
    private String description;
}
