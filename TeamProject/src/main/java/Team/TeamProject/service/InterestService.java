package Team.TeamProject.service;

import Team.TeamProject.dto.InterestDto;
import Team.TeamProject.entity.Interest;
import Team.TeamProject.entity.Member;
import Team.TeamProject.entity.Store;
import Team.TeamProject.repository.InterestRepository;
import Team.TeamProject.repository.MemberRepository;
import Team.TeamProject.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InterestService {
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final InterestRepository interestRepository;

    public String likeStore(String member_idx, Long store_idx) {
        Optional<Member> optionalMember = memberRepository.findById(member_idx);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Optional<Store> optionalStore = storeRepository.findById(store_idx);
        if (!optionalStore.isPresent()) {
            throw new IllegalArgumentException("상점을 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();
        Store store = optionalStore.get();

        Optional<Interest> optionalInterest = interestRepository.findByMemberAndStore(member, store);
        InterestDto interestDto = new InterestDto();
        if (optionalInterest.isPresent()) {
            Interest interest = optionalInterest.get();
            interest.setStatus(true);
            interestRepository.save(interest);
        } else {
            interestDto.setStatus(true);
            Interest interest = Interest.toInterest(interestDto, member, store);
            interestRepository.save(interest);
            store.saveInterest(store, interest);
            storeRepository.save(store);
        }
        return "관심 등록 완료";
    }

    public String unLikeStore(String member_idx, Long store_idx) {
        Optional<Member> optionalMember = memberRepository.findById(member_idx);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        Optional<Store> optionalStore = storeRepository.findById(store_idx);
        if (!optionalStore.isPresent()) {
            throw new IllegalArgumentException("상점을 찾을 수 없습니다.");
        }
        Member member = optionalMember.get();
        Store store = optionalStore.get();

        Optional<Interest> optionalInterest = interestRepository.findByMemberAndStore(member, store);
        if (!optionalInterest.isPresent()) {
            throw new IllegalArgumentException("관심 상점을 찾을 수 없습니다.");
        }
        Interest interest = optionalInterest.get();
        interest.setStatus(false);
        interestRepository.save(interest);

        return "관심 취소 완료";
    }
}
