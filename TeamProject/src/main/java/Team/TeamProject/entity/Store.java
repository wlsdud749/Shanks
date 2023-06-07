package Team.TeamProject.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity

public class Store extends BaseEntity {
    // 가게 정보 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long store_idx;

    private String opnSfTeamCode; //개방자치단체코드
    private String updateDt; //데이터 갱신일자
    private String opnSvcNm; //개방서비스명
    private String uptaeNm; //업태구분명
    private String bplcNm; //사업장명
    private String siteWhlAddr; //지번주소
    private String rdnPostNo; //도로명 우편번호
    private String rdnWhlAddr; //도로명주소
    private String apvPermYmd; //인허가 일자
    private String apvCancelYmd; //인허가 취소일자
    private String dtlStateNm; //상세영업상태
    private Double x; //x좌표
    private Double y; //y좌표
    private String siteTel; //전화번호

    @ToString.Exclude
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "Interest")
    private Interest interest; //관심

    public void saveInterest(Store store, Interest interest) {
        store.setInterest(interest);
    }
}
