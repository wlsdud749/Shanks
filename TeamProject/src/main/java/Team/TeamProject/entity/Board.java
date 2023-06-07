package Team.TeamProject.entity;

import Team.TeamProject.constant.BoardType;
import Team.TeamProject.dto.BoardDto;
import Team.TeamProject.dto.ImageDto;
import Team.TeamProject.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Board extends BaseEntity {
    // 게시판 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long board_idx;

    private String title; // 글 제목
    @Column(columnDefinition = "LONGTEXT")
    private String contents; // 글 내용
    private String category; // 카테고리
    private int count; // 조회수

    @Enumerated(EnumType.STRING)
    private BoardType boardType; // 게시글 타입 NOTICE or GENERAL

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public static Board toBoard(BoardDto boardDto, Member member){
        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setContents(boardDto.getContents());
        board.setCategory(boardDto.getCategory());
        board.setCount(boardDto.getCount());
        board.setBoardType(boardDto.getBoardType());
        board.setMember(member);

        List<Image> images = new ArrayList<>();
        if (boardDto.getImageDtos() != null) {
            for (ImageDto imageDto : boardDto.getImageDtos()) {
                images.add(Image.toImage(imageDto, board));
            }
        }
        board.setImages(images);

        List<Review> reviews = new ArrayList<>();
        if (boardDto.getReviewDtos() != null) {
            for(ReviewDto reviewDto : boardDto.getReviewDtos()) {
                reviews.add(Review.toReview(reviewDto, member, board));
            }
        }
        board.setReviews(reviews);

        return board;
    }
}
