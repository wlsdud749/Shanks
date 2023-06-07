package Team.TeamProject.entity;

import Team.TeamProject.dto.ImageDto;
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
public class Image extends BaseEntity {
    // 이미지 정보 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_idx;

    private String imgName; // 이미지 원본 이름
    private String imgPath; // 이미지 파일 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_idx")
    private Board board;

    public static Image toImage(ImageDto imageDto, Board board) {
        Image image = new Image();
        image.setImgName(imageDto.getImgName());
        image.setImgPath(imageDto.getImgPath());
        image.setBoard(board);
        return image;
    }
}
