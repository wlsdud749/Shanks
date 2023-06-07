package Team.TeamProject.entity;

import Team.TeamProject.constant.Role;
import Team.TeamProject.dto.MemberDto;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Member extends BaseEntity {
    // 사용자, 관리자 정보 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_idx;

    private String id; // 아이디
    private String password; // 패스워드
    private String name; // 이름
    private String email; // 이메일
    private String nick; // 닉네임
    private String phoneNumber; // 전화번호

    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "MemberAddress")
    private MemberAddress memberAddress; //상세주소

    @Enumerated(EnumType.STRING)
    private Role role; // 관리자 or 사용자

    public static Member toMember(MemberDto memberDto, MemberAddress memberAddress, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setId(memberDto.getId());
        String password = passwordEncoder.encode(memberDto.getPassword());
        member.setPassword(password);
        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setNick(memberDto.getNick());
        member.setPhoneNumber(memberDto.getPhoneNumber());
        member.setMemberAddress(memberAddress);
        member.setRole(Role.USER);
        return member;
    }
}
