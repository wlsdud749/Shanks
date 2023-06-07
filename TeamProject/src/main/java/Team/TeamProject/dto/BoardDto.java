package Team.TeamProject.dto;

import Team.TeamProject.constant.BoardType;
import Team.TeamProject.entity.Board;
import Team.TeamProject.entity.Image;
import Team.TeamProject.entity.Review;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BoardDto {
    private Long board_idx;
    private String title; // 글 제목
    private String contents; // 글 내용
    private String category; // 카테고리
    private int count; // 조회수
    private BoardType boardType; // 게시물 타입
    private LocalDateTime regTime;

    private MemberDto memberDto;

    private List<ImageDto> imageDtos;
    private List<ReviewDto> reviewDtos;

    public static BoardDto toBoardDto(Board board) {
        BoardDto boardDto = new BoardDto();
        boardDto.setBoard_idx(board.getBoard_idx());
        boardDto.setTitle(board.getTitle());
        boardDto.setContents(board.getContents());
        boardDto.setCategory(board.getCategory());
        boardDto.setCount(board.getCount());
        boardDto.setBoardType(board.getBoardType());
        boardDto.setRegTime(board.getRegTime());
        boardDto.setMemberDto(MemberDto.toMemberDto(board.getMember()));

        List<ImageDto> imageDtos = new ArrayList<>();
        if (board.getImages() != null) {
            for (Image image : board.getImages()) {
                imageDtos.add(ImageDto.toImageDto(image));
            }
        }
        boardDto.setImageDtos(imageDtos);

        List<ReviewDto> reviewDtos = new ArrayList<>();
        if(board.getReviews() != null) {
            for (Review review : board.getReviews()) {
                reviewDtos.add(ReviewDto.toReviewDto(review));
            }
        }
        boardDto.setReviewDtos(reviewDtos);

        return boardDto;
    }
}
