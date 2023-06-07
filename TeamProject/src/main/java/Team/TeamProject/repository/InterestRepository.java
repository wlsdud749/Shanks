package Team.TeamProject.repository;

import Team.TeamProject.entity.Interest;
import Team.TeamProject.entity.Member;
import Team.TeamProject.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByMemberAndStore(Member member, Store store);

    List<Interest> findByMember(Member member);
    Page<Interest> findByMemberId(Pageable pageable, String id);
    Page<Interest> findByMemberIdAndStoreBplcNmContaining(Pageable pageable, String id, String bplcNm);
}
