package Team.TeamProject.repository;

import Team.TeamProject.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByCategory(String category);
    List<Board> findByCategoryAndTitleContaining(String category, String title);
    List<Board> findByTitleContaining(String title);

    List<Board> findByMemberId(String memberId);
    List<Board> findByMemberIdAndCategory(String id, String category);
    List<Board> findByMemberIdAndCategoryAndTitleContaining(String id, String category, String title);
    List<Board> findByMemberIdAndTitleContaining(String id, String title);
}
