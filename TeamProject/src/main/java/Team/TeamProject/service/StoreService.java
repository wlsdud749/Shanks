package Team.TeamProject.service;

import Team.TeamProject.dto.StoreDto;
import Team.TeamProject.entity.Store;
import Team.TeamProject.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {
    @Autowired
    private final StoreRepository storeRepository;

    //모든 상점정보 받아오기
    @Transactional
    public List<StoreDto> findAll(){
        List<Store> storeList = storeRepository.findAll();
        List<StoreDto> storeDtoList = new ArrayList<>();
        for (Store store:storeList){
            StoreDto dto = new StoreDto().StoreEntityToDto(store);
            storeDtoList.add(dto);
        }
        return storeDtoList;
    }

    // 업태에 맞는 가게 검색
    @Transactional
    public List<StoreDto> searchUptae(String uptae) {

        var uptaeInfo = storeRepository.findAllByuptaeNm(uptae);
        List<StoreDto> uptaeList = new ArrayList<>();
        for (Store store:uptaeInfo){
            StoreDto dto = new StoreDto().StoreEntityToDto(store);
            uptaeList.add(dto);
        }
        return uptaeList;
    }

    //해당
    @Transactional
    public List<StoreDto> searchStore(String storeName) {

        var storeInfo = storeRepository.findBybplcNmContaining(storeName);
        List<StoreDto> storeList = new ArrayList<>();
        for (Store store:storeInfo){
            StoreDto dto = new StoreDto().StoreEntityToDto(store);
            storeList.add(dto);
        }
        return storeList;
    }

    public List<StoreDto> sendStoreInfo(String bplcNm) {
        var storeInfo = storeRepository.findBybplcNm(bplcNm);
        List<StoreDto> storeList = new ArrayList<>();
        for (Store store:storeInfo){
            StoreDto dto = new StoreDto().StoreEntityToDto(store);
            storeList.add(dto);
        }
        return storeList;
    }
}
