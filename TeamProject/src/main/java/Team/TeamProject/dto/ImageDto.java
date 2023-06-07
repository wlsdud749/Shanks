package Team.TeamProject.dto;

import Team.TeamProject.entity.Image;
import lombok.Data;

@Data
public class ImageDto {
    private String imgName; // 이미지 원본 이름
    private String imgPath; // 이미지 파일 경로

    private Long board_idx;

    public static ImageDto toImageDto(Image image) {
        ImageDto imageDto = new ImageDto();
        imageDto.setImgName(image.getImgName());
        imageDto.setImgPath(image.getImgPath());
        imageDto.setBoard_idx(image.getBoard().getBoard_idx());
        return imageDto;
    }
}
