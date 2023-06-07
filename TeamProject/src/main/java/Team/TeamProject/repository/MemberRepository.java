package Team.TeamProject.repository;

import Team.TeamProject.constant.Role;
import Team.TeamProject.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(String id);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhoneNumber(String phoneNumber);
    Optional<Member> findByNick(String nick);

    Page<Member> findByRole(Pageable pageable, Role role);
    Page<Member> findByRoleAndIdContaining(Pageable pageable, Role role, String id);
}
