package Team.TeamProject.controller;

import Team.TeamProject.constant.Role;
import Team.TeamProject.dto.BoardDto;
import Team.TeamProject.dto.ReviewDto;
import Team.TeamProject.service.BoardService;
import Team.TeamProject.service.ImageService;
import Team.TeamProject.service.MemberService;
import Team.TeamProject.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final MemberService memberService;
    private final BoardService boardService;
    private final ImageService imageService;
    private final ReviewService reviewService;

    /**
     * 게시글 목록 페이지
     */
    @GetMapping("/list")
    public String listView() {
        return "board/list";
    }

    /**
     * 게시글 목록 보여주기, 페이지네이션 기능
     */
    @GetMapping("/list/update")
    @ResponseBody
    public ResponseEntity<?> getUpdatedBoardList(@PageableDefault(size = 10) Pageable pageable,
                                                 @RequestParam String categoryId, @RequestParam String search) {
        try {
            Page<BoardDto> boardPage = boardService.getBoardPage(pageable, categoryId, search);
            return ResponseEntity.ok(boardPage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 글작성 페이지
     */
    @GetMapping("/writing")
    public String writingView(Model model, Principal principal) {
        try {
            String id = principal.getName();
            String nick = memberService.viewNick(id);
            model.addAttribute("nick", nick);
            return "board/writing";
        } catch (Exception e) {
            return "redirect:/board/list";
        }
    }

    /**
     * 공지글 작성 페이지
     */
    @GetMapping("/writing/notice")
    public String writingNoticeView(Model model, Principal principal) {
        try{
            String id = principal.getName();
            String nick = memberService.viewNick(id);
            Role role = memberService.getMemberRole(id);

            if(role == Role.ADMIN) {
                model.addAttribute("nick", nick);
                return "board/writing";
            } else {
                return "redirect:/board/writing";
            }
        } catch (Exception e) {
            return "redirect:/board/list";
        }
    }

    /**
     * 글 상세 페이지
     */
    @GetMapping("/detail")
    public String detailView(@RequestParam Long board_idx, Model model) {
        BoardDto boardDto = boardService.getBoardDetail(board_idx);
        boardService.increaseCount(board_idx);
        model.addAttribute("board", boardDto);
        return "board/detail";
    }

    /**
     * 글 수정 페이지
     */
    @GetMapping("/modify")
    public String modifyView(@RequestParam Long board_idx, Principal principal, Model model) {
        BoardDto boardDto = boardService.getBoardDetail(board_idx);
        String id = principal.getName();
        String boardId = boardDto.getMemberDto().getId();
        if(id.equals(boardId)){
            model.addAttribute("board", boardDto);
            return "board/modify";
        } else {
            return "redirect:/board/detail?board_idx=" + board_idx;
        }
    }

    /**
     * 글 수정 내용
     */
    @GetMapping("/modify/detail")
    public ResponseEntity<?> modifyDetailView(@RequestParam Long board_idx) {
        BoardDto boardDto = boardService.getBoardDetail(board_idx);
        return ResponseEntity.ok(boardDto);
    }

    /**
     * 글 수정
     */
    @PostMapping("/modifyBoard")
    public ResponseEntity<String> modifyBoard(@RequestBody BoardDto boardDto, Principal principal) {
        try {
            String id = principal.getName();
            boardService.modifyBoard(boardDto, id);
            return ResponseEntity.ok("게시글이 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 게시글 이미지 업로드
     */
    @PostMapping("/uploadSummernoteImageFiles")
    @ResponseBody
    public ResponseEntity<List<String>> uploadSummernoteImageFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                byte[] fileData = file.getBytes();
                String savedFileName = imageService.uploadFile(file.getOriginalFilename(), fileData);
                String imageUrl = "/summernote_image/" + savedFileName;
                imageUrls.add(imageUrl);
            }
            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonList(e.getMessage()));
        }
    }

    /**
     * 게시글 이미지 삭제
     */
    @PostMapping("/deleteSummernoteImageFile")
    @ResponseBody
    public ResponseEntity<String> deleteSummernoteImageFile(@RequestParam("imageUrl") String imageUrl) {
        try {
            // 이미지 파일명 추출
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            // 이미지 파일 삭제
            imageService.deleteFile(fileName);

            return ResponseEntity.ok("이미지를 삭제하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이미지 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * 게시글 저장
     */
    @PostMapping("/saveBoard")
    public ResponseEntity<String> saveBoard(@RequestBody BoardDto boardDto, Principal principal) {
        try {
            String id = principal.getName();
            boardService.saveBoard(boardDto, id);
            return ResponseEntity.ok("게시글이 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 게시글 쓰기 벗어날때 저장되지 않은 이미지들 로컬에서 삭제
     */
    @PostMapping("/deleteimage")
    public ResponseEntity<String> deleteImage() {
        try {
            imageService.deleteImgList();
            return ResponseEntity.ok("로컬 이미지 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로컬 이미지 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * 조회수
     */
    @GetMapping("/detail/count")
    public ResponseEntity<?> countView(@RequestParam Long board_idx) {
        try {
            BoardDto boardDto = boardService.getBoardDetail(board_idx);
            return ResponseEntity.ok(boardDto.getCount());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 글 삭제
     */
    @GetMapping("/delete")
    public ResponseEntity<?> deleteBoard(@RequestParam Long board_idx, Principal principal) {
        try {
            boardService.deleteBoard(board_idx, principal.getName());
            return ResponseEntity.ok("글 삭제가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 댓글 저장
     */
    @PostMapping("/review")
    public ResponseEntity<?> saveReview(@RequestParam String review, @RequestParam Long board_idx, Principal principal) {
        try {
            String id = principal.getName();
            reviewService.saveReview(review, board_idx,id);
            return ResponseEntity.ok("댓글이 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 댓글 보여주기
     */
    @GetMapping("/review/view")
    public ResponseEntity<?> reviewView(@RequestParam Long board_idx){
        try {
            List<ReviewDto> reviewDtos = reviewService.reviewView(board_idx);
            return ResponseEntity.ok(reviewDtos);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 댓글 수정 하기
     */
    @PostMapping("/review/update")
    public ResponseEntity<?> updateReview(@RequestParam Long review_idx, @RequestParam String updateContents, Principal principal) {
        try {
            String id = principal.getName();
            reviewService.updateReview(review_idx, updateContents, id);
            return ResponseEntity.ok("댓글 수정이 완료되었습니다.");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 댓글 삭제 하기
     */
    @PostMapping("/review/delete")
    public ResponseEntity<?> deleteReview(@RequestParam Long review_idx, Principal principal) {
        try {
            String id = principal.getName();
            reviewService.deleteReview(review_idx, id);
            return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
