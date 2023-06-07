package Team.TeamProject.service;

import Team.TeamProject.constant.Role;
import Team.TeamProject.dto.ReviewDto;
import Team.TeamProject.entity.Board;
import Team.TeamProject.entity.Member;
import Team.TeamProject.entity.Review;
import Team.TeamProject.repository.BoardRepository;
import Team.TeamProject.repository.MemberRepository;
import Team.TeamProject.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ReviewRepository reviewRepository;


    /**
     * 댓글 저장
     */
    public void saveReview(String review, Long board_idx, String id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();
        Optional<Board> optionalBoard = boardRepository.findById(board_idx);
        if(!optionalBoard.isPresent()) {
            throw new IllegalArgumentException("게시판을 찾을 수 없습니다.");
        }
        Board board = optionalBoard.get();

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setContents(review);

        Review reviewEntity = Review.toReview(reviewDto, member, board);

        reviewRepository.save(reviewEntity);

        board.getReviews().add(reviewEntity); // 리뷰를 게시판에 추가

        boardRepository.save(board); // 게시판 엔티티 저장
    }

    /**
     * 댓글 보여주기
     */
    public List<ReviewDto> reviewView(Long board_idx) {
        List<ReviewDto> reviewDtoList = new ArrayList<>();
        Optional<Board> optionalBoard = boardRepository.findById(board_idx);
        if(!optionalBoard.isPresent()) {
            throw new IllegalArgumentException("게시판을 찾을 수 없습니다.");
        }
        Board board = optionalBoard.get();
        List<Review> reviews = reviewRepository.findByBoard(board);
        for (Review review : reviews) {
            reviewDtoList.add(ReviewDto.toReviewDto(review));
        }
        return reviewDtoList;
    }

    /**
     * 댓글 수정 하기
     */
    public void updateReview(Long review_idx, String updateContents, String id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Optional<Review> optionalReview = reviewRepository.findById(review_idx);
        if (!optionalReview.isPresent()) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        Review review = optionalReview.get();
        review.setContents(updateContents);
        reviewRepository.save(review);
    }

    /**
     * 댓글 삭제 하기
     */
    public void deleteReview(Long review_idx, String id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Optional<Review> optionalReview = reviewRepository.findById(review_idx);
        if (!optionalReview.isPresent()) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }
        // 글 작성자와 현재 사용자의 ID를 비교하여 권한 검사를 수행
        Member member = optionalMember.get();
        Review review = optionalReview.get();
        if (member.getId().equals(review.getMember().getId()) || member.getRole().equals(Role.ADMIN)) {
            reviewRepository.delete(review);
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

    }
}
