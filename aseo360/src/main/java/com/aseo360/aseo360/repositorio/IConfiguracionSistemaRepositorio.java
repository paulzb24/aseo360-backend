package com.aseo360.aseo360.repositorio;

import com.aseo360.aseo360.modelo.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IConfiguracionSistemaRepositorio extends JpaRepository<ConfiguracionSistema, Long> {
    Optional<ConfiguracionSistema> findByClave(String clave);

    boolean existsByClave(String clave);

    boolean existsByClaveAndIdConfiguracionNot(String clave, Long idConfiguracion);
}
