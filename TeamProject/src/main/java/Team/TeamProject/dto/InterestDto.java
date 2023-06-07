package Team.TeamProject.dto;

import Team.TeamProject.entity.Interest;
import Team.TeamProject.entity.Member;
import Team.TeamProject.entity.Store;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class InterestDto {
    private Long interest_idx;
    private boolean status;  // true = 관심, false = 관심x
    private Long member_idx;
    private Long store_idx;
    private LocalDateTime regTime;

    public static InterestDto toInterestDto(Interest interest) {
        InterestDto interestDto = new InterestDto();
        interestDto.setInterest_idx(interest.getInterest_idx());
        interestDto.setStatus(interest.isStatus());
        interestDto.setMember_idx(interest.getMember().getMember_idx());
        interestDto.setStore_idx(interest.getStore().getStore_idx());
        interestDto.setRegTime(interest.getRegTime());

        return interestDto;
    }
}
