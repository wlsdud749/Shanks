package Team.TeamProject.repository;

import Team.TeamProject.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    List<Store> findAllByuptaeNm(String uptae);
    List<Store> findBybplcNmContaining(String storeName);
    List<Store> findBybplcNm(String bplcNm);
}
