package Team.TeamProject.dto;

import Team.TeamProject.entity.MemberAddress;
import lombok.Data;

@Data
public class MemberAddressDto {

    private String address; //주소
    private int zonecode; //우편번호
    private String detailedaddress; //상세주소

    public static MemberAddressDto toMemberAddressDto(MemberAddress memberAddress) {
        MemberAddressDto memberAddressDto = new MemberAddressDto();
        memberAddressDto.setAddress(memberAddress.getAddress());
        memberAddressDto.setZonecode(memberAddress.getZonecode());
        memberAddressDto.setDetailedaddress(memberAddress.getDetailedaddress());
        return memberAddressDto;
    }
}
