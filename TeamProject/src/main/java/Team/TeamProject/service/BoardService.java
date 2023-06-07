package Team.TeamProject.service;

import Team.TeamProject.constant.BoardType;
import Team.TeamProject.constant.Role;
import Team.TeamProject.dto.BoardDto;
import Team.TeamProject.dto.ImageDto;
import Team.TeamProject.entity.Board;
import Team.TeamProject.entity.Image;
import Team.TeamProject.entity.Member;
import Team.TeamProject.entity.Review;
import Team.TeamProject.repository.BoardRepository;
import Team.TeamProject.repository.ImageRepository;
import Team.TeamProject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final ReviewService reviewService;

    /**
     * 게시글 저장
     */
    public void saveBoard(BoardDto boardDto, String id) throws Exception {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();
        Board board = Board.toBoard(boardDto, member);
        List<ImageDto> imageDtos = boardDto.getImageDtos();
        List<Image> images = new ArrayList<>();
        if(imageDtos != null && !imageDtos.isEmpty()) {
            for(ImageDto imageDto : imageDtos) {
                Optional<Image> optionalImage = imageRepository.findByImgName(imageDto.getImgName());
                if (!optionalImage.isPresent()) {
                    throw new IllegalArgumentException("이미지를 찾을 수 없습니다.");
                }
                Image image = optionalImage.get();
                images.add(image);
            }
            board.setImages(images);
        }
        boardRepository.save(board);

        if(images != null && !images.isEmpty()) {
            for(Image image : images) {
                image.setBoard(board);
            }
        }
        imageService.deleteImgList();
    }


    /**
     * 페이지 번호, 페이지 크기 전달, 선택한 카테고리의 글 목록
     */
    public Page<BoardDto> getBoardPage(Pageable pageable, String categoryId, String search) {
        List<Board> boards;
        if(categoryId.equals("all")) {
            if(search.isBlank()){
                boards = boardRepository.findAll();
            } else {
                boards = boardRepository.findByTitleContaining(search);
            }
        } else {
            if(search.isBlank()){
                boards = boardRepository.findByCategory(categoryId);
            }
            else {
                boards = boardRepository.findByCategoryAndTitleContaining(categoryId, search);
            }
        }

        List<Board> noticeBoards = new ArrayList<>();
        List<Board> generalBoards = new ArrayList<>();

        for (Board board : boards) {
            if (board.getBoardType() == BoardType.NOTICE) {
                noticeBoards.add(board);
            } else {
                generalBoards.add(board);
            }
        }

        Collections.reverse(noticeBoards);
        Collections.reverse(generalBoards);

        noticeBoards.addAll(generalBoards);


        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), noticeBoards.size());
        Page<Board> sortedBoardPage = new PageImpl<>(noticeBoards.subList(start, end), pageable, noticeBoards.size());

        return sortedBoardPage.map(BoardDto::toBoardDto);

    }

    /**
     * 글 수정 로직
     */
    public void modifyBoard(BoardDto boardDto, String id) throws Exception{
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Optional<Board> optionalBoard = boardRepository.findById(boardDto.getBoard_idx());
        if (!optionalBoard.isPresent()) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
        // 원래 가지고 있던 게시판 내용
        Board board = optionalBoard.get();
        // 원래 가지고 있던 이미지
        List<Image> imageList = board.getImages();
        // 수정한 게시판 이미지
        List<ImageDto> imageDtos = boardDto.getImageDtos();
        // 삭제 될 이미지들
        List<Image> deletedImages = new ArrayList<>();
        if(imageList != null && !imageList.isEmpty() && imageDtos != null && !imageDtos.isEmpty()){
            for(Image image : imageList) {
                String imageName = image.getImgName();
                boolean found = false;
                for(ImageDto imageDto : imageDtos) {
                    String imageDtoName = imageDto.getImgName();
                    if(imageName.equals(imageDtoName)) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    deletedImages.add(image);
                }
            }
            // 삭제된 이미지를 제거
            for (Image deletedImage : deletedImages) {
                imageService.deleteFile(deletedImage.getImgName());
                board.getImages().remove(deletedImage);
            }

            for(ImageDto imageDto : imageDtos) {
                String imageDtoName = imageDto.getImgName();
                boolean notFound = true;
                for(Image image : imageList) {
                    String imageName = image.getImgName();
                    if(imageDtoName.equals(imageName)) {
                        log.info("imageList와 중복된 이미지들");
                        notFound = false;
                        break;
                    }
                }
                if(notFound) {
                    Optional<Image> optionalImage = imageRepository.findByImgName(imageDto.getImgName());
                    if(!optionalImage.isPresent()) {
                        throw new IllegalArgumentException("이미지를 찾을 수 없습니다.");
                    }
                    Image image = optionalImage.get();
                    image.setBoard(board);
                    imageList.add(image);
                }
            }
            log.info("마지막: " + String.valueOf(imageList.size()));
        } else if (imageDtos != null && !imageDtos.isEmpty()) {
            // 원래 가지고 있던 이미지가 없었지만 수정된 내용에서 이미지가 있는 경우
            for(ImageDto imageDto : imageDtos) {
                Optional<Image> optionalImage = imageRepository.findByImgName(imageDto.getImgName());
                if(!optionalImage.isPresent()) {
                    throw new IllegalArgumentException("이미지를 찾을 수 없습니다.");
                }
                Image image = optionalImage.get();
                image.setBoard(board);
                imageList.add(image);
            }
            log.info("이미지 추가: " + String.valueOf(imageList.size()));
        } else if (imageList != null && !imageList.isEmpty()) {
            // 원래 가지고 있던 이미지가 있었지만 수정된 내용에서 이미지가 없는 경우
            for(Image image : imageList) {
                imageService.deleteFile(image.getImgName());
            }
            board.getImages().clear();
        }

        // 수정된 게시판 내용 변경
        board.setTitle(boardDto.getTitle());
        board.setContents(boardDto.getContents());
        board.setCategory(boardDto.getCategory());
        boardRepository.save(board);
    }

    /**
     * 글 찾기
     */
    public BoardDto getBoardDetail(Long board_idx) {
        Optional<Board> optionalBoard = boardRepository.findById(board_idx);
        BoardDto boardDto = BoardDto.toBoardDto(optionalBoard.get());
        return boardDto;
    }

    /**
     * 조회수
     */
    public void increaseCount(Long board_idx) {
        Optional<Board> optionalBoard = boardRepository.findById(board_idx);
        if(optionalBoard.isPresent()){
            Board board = optionalBoard.get();
            board.setCount(board.getCount() + 1);
            boardRepository.save(board);
        }
    }

    /**
     * 글 삭제
     */
    public void deleteBoard(Long board_idx, String id) throws Exception{
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Optional<Board> optionalBoard = boardRepository.findById(board_idx);
        if (!optionalBoard.isPresent()) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        // 글 작성자와 현재 사용자의 ID를 비교하여 권한 검사를 수행
        Member member = optionalMember.get();
        Board board = optionalBoard.get();
        if (member.getId().equals(board.getMember().getId()) || member.getRole().equals(Role.ADMIN)) {
            List<Image> imageList = board.getImages();
            if(imageList != null && !imageList.isEmpty()){
                for(Image image : imageList) {
                    imageService.deleteFile(image.getImgName());
                }
                board.getImages().clear();
            }

            // 글 삭제
            boardRepository.delete(board);
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
