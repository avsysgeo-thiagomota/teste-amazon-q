package org.avsytem.repository;

import org.avsytem.entity.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Integer> {

    @Query("SELECT r FROM Receita r WHERE r.usuario.id = :usuarioId")
    List<Receita> findByUsuarioId(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT r FROM Receita r WHERE r.usuario.id = :usuarioId AND r.nome LIKE %:nome%")
    List<Receita> findByUsuarioIdAndNomeContaining(@Param("usuarioId") Integer usuarioId, @Param("nome") String nome);

    @Query("SELECT r FROM Receita r WHERE r.usuario.id = :usuarioId AND r.dificuldade = :dificuldade")
    List<Receita> findByUsuarioIdAndDificuldade(@Param("usuarioId") Integer usuarioId, @Param("dificuldade") String dificuldade);

    // Separate queries to avoid MultipleBagFetchException
    @Query("SELECT r FROM Receita r LEFT JOIN FETCH r.ingredientes WHERE r.id = :id")
    Optional<Receita> findByIdWithIngredientes(@Param("id") Integer id);

    @Query("SELECT r FROM Receita r LEFT JOIN FETCH r.passos WHERE r.id = :id")
    Optional<Receita> findByIdWithPassos(@Param("id") Integer id);

    @Query("SELECT r FROM Receita r LEFT JOIN FETCH r.ingredientes WHERE r.usuario.id = :usuarioId")
    List<Receita> findByUsuarioIdWithIngredientes(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT r FROM Receita r LEFT JOIN FETCH r.passos WHERE r.usuario.id = :usuarioId")
    List<Receita> findByUsuarioIdWithPassos(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT COUNT(r) FROM Receita r WHERE r.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Integer usuarioId);
}
