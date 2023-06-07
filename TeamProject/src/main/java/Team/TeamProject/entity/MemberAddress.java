package Team.TeamProject.entity;

import Team.TeamProject.dto.MemberAddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class MemberAddress extends BaseEntity {
    // 사용자, 관리자 상세 주소 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberaddress_idx;

    private String address; //주소
    private int zonecode; //우편번호
    private String detailedaddress; //상세주소

    public static MemberAddress toMemberAddress(MemberAddressDto memberAddressDto) {
        MemberAddress memberAddress = new MemberAddress();
        memberAddress.setAddress(memberAddressDto.getAddress());
        memberAddress.setZonecode(memberAddressDto.getZonecode());
        memberAddress.setDetailedaddress(memberAddressDto.getDetailedaddress());
        return memberAddress;
    }
}
