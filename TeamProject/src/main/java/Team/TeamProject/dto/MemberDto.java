package Team.TeamProject.dto;

import Team.TeamProject.constant.Role;
import Team.TeamProject.entity.Member;
import Team.TeamProject.entity.MemberAddress;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberDto {
    private Long member_idx;
    private String id; // 아이디
    private String password; // 패스워드
    private String name; // 이름
    private String email; // 이메일
    private String nick; // 닉네임
    private String phoneNumber; // 전화번호
    private Role role; // 관리자 or 사용자
    private LocalDateTime regTime;
    private MemberAddressDto memberAddressDto;

    public static MemberDto toMemberDto(Member member) {
        MemberDto memberDto = new MemberDto();
        memberDto.setMember_idx(member.getMember_idx());
        memberDto.setId(member.getId());
        memberDto.setPassword(member.getPassword());
        memberDto.setName(member.getName());
        memberDto.setEmail(member.getEmail());
        memberDto.setNick(member.getNick());
        memberDto.setPhoneNumber(member.getPhoneNumber());
        memberDto.setMemberAddressDto(MemberAddressDto.toMemberAddressDto(member.getMemberAddress()));
        memberDto.setRole(member.getRole());
        memberDto.setRegTime(member.getRegTime());
        return memberDto;
    }
}
