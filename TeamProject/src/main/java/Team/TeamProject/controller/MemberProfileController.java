package Team.TeamProject.controller;

import Team.TeamProject.entity.Member;
import Team.TeamProject.service.MemberService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class MemberProfileController {
    private final MemberService memberService;

    /**
     * 비밀번호 확인 페이지
     */
    @GetMapping("/check-password")
    public String checkPwView(HttpSession session){
        session.removeAttribute("changePasswordAllowed");
        return "profile/check-password";
    }

    /**
     * 비밀번호 확인
     */
    @PostMapping("/checkPw")
    public ResponseEntity<?> checkPassword(Principal principal, @RequestParam String password, HttpSession session) {
        try{
            String id = principal.getName();
            boolean passwordMatches = memberService.checkPassword(id, password);
            if(passwordMatches) {
                session.setAttribute("changePasswordAllowed", true);
            }
            return ResponseEntity.ok().body(passwordMatches);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 프로필 변경 페이지
     */
    @GetMapping("/manage-profiles")
    public String changePwView(HttpSession session) {
        Boolean changePasswordAllowed = (Boolean) session.getAttribute("changePasswordAllowed");
        if (changePasswordAllowed != null && changePasswordAllowed) {
            return "profile/manage-profiles";
        } else {
            return "redirect:/profile/check-password";
        }
    }

    /**
     * 닉네임 보여주기
     */

    @GetMapping("/manage-profiles/nick")
    public ResponseEntity<?> changePwView(HttpSession session, Principal principal) {
        try {
            String id = principal.getName();
            String nick = memberService.viewNick(id);
            return ResponseEntity.ok(nick);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(Principal principal, @RequestParam String nowPassword, @RequestParam String newPassword, HttpSession session){
        try {
            String id = principal.getName();
            boolean passwordMatches = memberService.checkPassword(id, nowPassword);
            if(!passwordMatches) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            Member changePassword = memberService.changePassword(id, nowPassword,newPassword);
            session.removeAttribute("changePasswordAllowed");
            return ResponseEntity.ok(changePassword);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 닉네임 변경
     */
    @PostMapping("/changeNick")
    public ResponseEntity<?> changeNick(Principal principal, @RequestParam String newNick, HttpSession session) {
        try{
            String id = principal.getName();
            Member changeNick = memberService.changeNick(id, newNick);
            session.removeAttribute("changePasswordAllowed");
            return ResponseEntity.ok(changeNick);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
