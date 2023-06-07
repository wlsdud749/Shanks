package Team.TeamProject.dto;

import Team.TeamProject.entity.Review;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Long review_idx;
    private String contents;
    private MemberDto memberDto;
    private Long board_idx;
    private LocalDateTime regTime;

    public static ReviewDto toReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReview_idx(review.getReview_idx());
        reviewDto.setContents(review.getContents());
        reviewDto.setMemberDto(MemberDto.toMemberDto(review.getMember()));
        reviewDto.setBoard_idx(review.getBoard().getBoard_idx());
        reviewDto.setRegTime(review.getRegTime());
        return reviewDto;
    }
}
