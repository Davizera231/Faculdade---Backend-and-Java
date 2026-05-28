package com.esteira.repository;
import com.esteira.model.Proposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface PropostaRepository extends JpaRepository<Proposta, Integer> {
    List<Proposta> findByStatusOrderByDataCriacaoDesc(String status);
    List<Proposta> findAllByOrderByDataCriacaoDesc();
    boolean existsByCodigo(String codigo);
}
