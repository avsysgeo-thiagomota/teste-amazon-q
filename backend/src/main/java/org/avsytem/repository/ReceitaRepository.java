package org.avsytem.repository;

import org.avsytem.entity.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    List<Receita> findByUsuarioId(Long usuarioId);

    @Query("SELECT r FROM Receita r WHERE r.usuario.id = :usuarioId AND r.nome LIKE %:nome%")
    List<Receita> findByUsuarioIdAndNomeContaining(@Param("usuarioId") Long usuarioId, @Param("nome") String nome);

    @Query("SELECT r FROM Receita r WHERE r.usuario.id = :usuarioId AND r.dificuldade = :dificuldade")
    List<Receita> findByUsuarioIdAndDificuldade(@Param("usuarioId") Long usuarioId, @Param("dificuldade") String dificuldade);

    @Query("SELECT r FROM Receita r LEFT JOIN FETCH r.ingredientes LEFT JOIN FETCH r.passos WHERE r.id = :id")
    Optional<Receita> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT r FROM Receita r LEFT JOIN FETCH r.ingredientes LEFT JOIN FETCH r.passos WHERE r.usuario.id = :usuarioId")
    List<Receita> findByUsuarioIdWithDetails(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(r) FROM Receita r WHERE r.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
}
