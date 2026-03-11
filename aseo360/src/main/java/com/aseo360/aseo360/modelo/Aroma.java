package com.aseo360.aseo360.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "aromas")
public class Aroma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAroma;

    @Column(unique = true, nullable = false, length = 75)
    private String nombre;
}
