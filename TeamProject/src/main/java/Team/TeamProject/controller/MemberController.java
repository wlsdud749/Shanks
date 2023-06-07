package Team.TeamProject.controller;

import Team.TeamProject.constant.Role;
import Team.TeamProject.dto.MemberDto;
import Team.TeamProject.entity.Member;
import Team.TeamProject.repository.MemberRepository;
import Team.TeamProject.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/sign")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 로그인 페이지
     */
    @GetMapping("/sign-in")
    public String signInView(Principal principal) {
        if (principal != null) {
            // 로그인 한 경우 게시판 페이지로 이동
            return "redirect:/board/list";
        }
        // 인증되지 않은 경우 로그인 페이지로 이동
        return "sign/sign-in";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/sign-up")
    public String signUpView() {
        return "sign/sign-up";
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> saveMember(@RequestBody MemberDto memberDto, HttpServletResponse response) {
        try {
            Member saveMember = memberService.createMember(memberDto);
            response.sendRedirect("/sign/sign-in");
            return ResponseEntity.ok(saveMember);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * 로그인 에러 알림
     */
    @GetMapping("/sign-in/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        log.info("model: {}", model);
        return "sign/sign-in";
    }

    /**
     * 실시간 아이디 체크
     */
    @PostMapping("/id_check")
    public ResponseEntity<Map<String, Object>> checkId(@RequestParam String id) {
        Map<String, Object> response = new HashMap<>();
        boolean isExists = memberService.isIdExists(id);
        if (id.isBlank()) {
            response.put("message", "빈 값입니다.");
        } else if (isExists) {
            response.put("message", "아이디가 이미 존재합니다.");
        } else {
            response.put("message", "사용 가능한 아이디입니다.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 실시간 닉네임 체크
     */
    @PostMapping("/nick_check")
    public ResponseEntity<Map<String, Object>> checkNick(@RequestParam String nick) {
        Map<String, Object> response = new HashMap<>();
        boolean isExists = memberService.isNickExists(nick);
        if(nick.isBlank()) {
            response.put("message", "빈 값입니다.");
        } else if (isExists) {
            response.put("message", "닉네임이 이미 존재합니다.");
        } else {
            response.put("message", "사용 가능한 닉네임입니다.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 체크
     */
    @GetMapping("/check-login")
    public ResponseEntity<?> checkLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 사용자가 로그인한 상태
            return ResponseEntity.ok(true);
        } else {
            // 사용자가 로그인하지 않은 상태
            return ResponseEntity.ok(false);
        }
    }

    /**
     *  로그인 아이디 받아오기
     */
    @GetMapping("/username")
    public ResponseEntity<?> currentUserName(Principal principal) {
        if(principal != null) {
            return ResponseEntity.ok(principal.getName());
        } else {
            return ResponseEntity.ok(null);
        }
    }

    /**
     * 로그인 권한 받아오기
     */
    @GetMapping("/userRole")
    public ResponseEntity<?> currentUserRole(Principal principal) {
        if(principal != null) {
            Role role = memberService.getMemberRole(principal.getName());
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.ok("USER");
        }
    }
}
