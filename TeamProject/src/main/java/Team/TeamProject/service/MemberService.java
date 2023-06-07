package Team.TeamProject.service;

import Team.TeamProject.constant.Role;
import Team.TeamProject.dto.MemberAddressDto;
import Team.TeamProject.dto.MemberDto;
import Team.TeamProject.entity.Board;
import Team.TeamProject.entity.Interest;
import Team.TeamProject.entity.Member;
import Team.TeamProject.entity.MemberAddress;
import Team.TeamProject.repository.BoardRepository;
import Team.TeamProject.repository.InterestRepository;
import Team.TeamProject.repository.MemberAddressRepository;
import Team.TeamProject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final InterestRepository interestRepository;

    /**
     * 회원가입
     */
    public Member createMember(MemberDto memberDto) {
        nullCheckMember(memberDto);
        validateDuplicateMember(memberDto);
        MemberAddressDto memberAddressDto = memberDto.getMemberAddressDto();
        MemberAddress memberAddress = MemberAddress.toMemberAddress(memberAddressDto);
        memberAddress = memberAddressRepository.save(memberAddress);
        Member member = Member.toMember(memberDto, memberAddress, passwordEncoder);
        member = memberRepository.save(member);
        return member;
    }

    /**
     * 유효성 검사
     */
    private void validateDuplicateMember(MemberDto memberDto) throws DuplicateKeyException {
        Optional<Member> findMemberId = memberRepository.findById(memberDto.getId());
        Optional<Member> findMemberEmail = memberRepository.findByEmail(memberDto.getEmail());
        Optional<Member> findMemberPhoneNumber = memberRepository.findByPhoneNumber(memberDto.getPhoneNumber());
        Optional<Member> findMemberNick = memberRepository.findByNick(memberDto.getNick());
        if(findMemberId.isPresent()) {
            throw new DuplicateKeyException("아이디가 이미 존재합니다.");
        }
        if(findMemberEmail.isPresent()) {
            throw new DuplicateKeyException("이메일이 이미 존재합니다.");
        }
        if(findMemberPhoneNumber.isPresent()) {
            throw new DuplicateKeyException("이미 사용 중인 전화번호입니다");
        }
        if(findMemberNick.isPresent()) {
            throw new DuplicateKeyException("이미 사용 중인 닉네임입니다");
        }
    }

    /**
     * 빈 값 확인
     */
    private void nullCheckMember(MemberDto memberDto) throws IllegalArgumentException {
        if(memberDto.getId().isBlank()){
            throw new IllegalArgumentException("아이디를 입력하세요.");
        }
        if(memberDto.getPassword().isBlank()){
            throw new IllegalArgumentException("비밀번호를 입력하세요.");
        }
        if(memberDto.getName().isBlank()){
            throw new IllegalArgumentException("이름을 입력하세요.");
        }
        if(memberDto.getEmail().isBlank()){
            throw new IllegalArgumentException("이메일을 입력하세요.");
        }
        if(memberDto.getNick().isBlank()){
            throw new IllegalArgumentException("닉네임를 입력하세요.");
        }
        if(memberDto.getPhoneNumber().isBlank()){
            throw new IllegalArgumentException("전화번호를 입력하세요.");
        }
    }


    /**
     * 중복되는 아이디 검사
     */
    public boolean isIdExists(String id) {
        Optional<Member> findId = memberRepository.findById(id);
        return findId.isPresent();
    }

    /**
     * 중복되는 닉네임 검사
     */
    public boolean isNickExists(String nick) {
        Optional<Member> findNick = memberRepository.findByNick(nick);
        return findNick.isPresent();
    }

    /**
     * 비밀번호 확인
     */
    public boolean checkPassword(String id, String password) {
        if (password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력하세요");
        }
        Optional<Member> member  = memberRepository.findById(id);
        if (!member .isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 업습니다.");
        }
        MemberDto memberDto = MemberDto.toMemberDto(member.get());
        String memberPassword = memberDto.getPassword();
        boolean matches = passwordEncoder.matches(password, memberPassword);
        return matches;
    }

    /**
     * 비밀번호 변경
     */
    public Member changePassword(String id, String nowPassword, String newPassword) {
        if (newPassword.isBlank()) {
            throw new IllegalArgumentException("새 비밀번호를 입력하세요");
        }
        if (nowPassword.equals(newPassword)) {
            throw new IllegalArgumentException("현재 비밀번호와 새 비밀 번호가 같습니다. 다시 입력해주세요.");
        }
        Optional<Member> optionalMember  = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 업습니다.");
        }
        MemberDto memberDto = MemberDto.toMemberDto(optionalMember.get());
        memberDto.setPassword(newPassword);
        log.info("memberDto = {}", memberDto);
        Member member = optionalMember.get();
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        log.info("member = {}", member);
        memberRepository.save(member);
        return member;
    }

    /**
     * 닉네임 찾기
     */
    public String viewNick(String id) {
        Optional<Member> optionalMember  = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 업습니다.");
        }
        MemberDto memberDto = MemberDto.toMemberDto(optionalMember.get());
        return memberDto.getNick();
    }

    /**
     * 닉네임 변경
     */
    public Member changeNick(String id, String newNick) {
        if (newNick.isBlank()) {
            throw new IllegalArgumentException("새 닉네임을 입력하세요");
        }
        Optional<Member> optionalMember  = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 업습니다.");
        }
        boolean checkNick = isNickExists(newNick);
        if(checkNick) {
            throw new DuplicateKeyException("이미 사용 중인 닉네임입니다.");
        }
        MemberDto memberDto = MemberDto.toMemberDto(optionalMember.get());
        memberDto.setNick(newNick);
        Member member = optionalMember.get();
        member.setNick(memberDto.getNick());
        memberRepository.save(member);
        return member;
    }

    /**
     * Member 권한 가져오기
     */
    public Role getMemberRole(String id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        return optionalMember.get().getRole();
    }

    /**
     * USER 권한을 가진 Member 회원 탈퇴
     */
    public void deleteMember(Long member_idx) throws Exception {
        Optional<Member> optionalMember = memberRepository.findById(member_idx);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();
        List<Board> boardList = boardRepository.findByMemberId(member.getId());

        if(boardList != null && !boardList.isEmpty()) {
            if (member.getRole().equals(Role.USER)) {
                for(Board board : boardList) {
                    boardService.deleteBoard(board.getBoard_idx(), member.getId());
                }
            } else {
                throw new IllegalArgumentException("권한이 없습니다.");
            }
        }

        List<Interest> interestList = interestRepository.findByMember(member);
        if(interestList != null && !interestList.isEmpty()) {
            if (member.getRole().equals(Role.USER)) {
                for(Interest interest : interestList) {
                    interestRepository.delete(interest);
                }
            } else {
                throw new IllegalArgumentException("권한이 없습니다.");
            }
        }

        if (member.getRole().equals(Role.USER)) {
            memberRepository.delete(member);
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

    /**
     * Spring security 사용자, 관리자 권한 설정
     */
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findById(id);
        if(!member.isPresent()) {
            throw new UsernameNotFoundException(id);
        }

        log.info("member: {}", member);

        return User.builder()
                .username(member.get().getId())
                .password(member.get().getPassword())
                .roles(member.get().getRole().toString())
                .build();
    }
}
