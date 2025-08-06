package org.avsytem.repository;

import org.avsytem.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.ativo = true")
    java.util.List<Usuario> findAllActive();

    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.ativo = true")
    Optional<Usuario> findByUsernameAndActive(@Param("username") String username);
}
