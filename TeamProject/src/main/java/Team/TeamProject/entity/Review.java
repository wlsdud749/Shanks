package Team.TeamProject.entity;

import Team.TeamProject.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_idx;

    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    public static Review toReview(ReviewDto reviewDto, Member member, Board board) {
        Review review = new Review();
        review.setContents(reviewDto.getContents());
        review.setMember(member);
        review.setBoard(board);
        return  review;
    }
}
