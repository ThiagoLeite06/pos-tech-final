package com.br.susreceita.prescription.infrastructure.adapter.out.persistence;

import com.br.susreceita.prescription.application.port.out.BlockedDrugRepositoryPort;
import com.br.susreceita.prescription.domain.model.BlockedDrug;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BlockedDrugJpaRepositoryAdapter implements BlockedDrugRepositoryPort {
    private final BlockedDrugJpaRespository respository;

    public BlockedDrugJpaRepositoryAdapter(BlockedDrugJpaRespository respository) {
        this.respository = respository;
    }

    @Override
    public List<BlockedDrug> findAllBlockedDrugs(int page, int size) {
        return respository.findAll(PageRequest.of(page, size)).stream().toList();
    }

    @Override
    public BlockedDrug save(BlockedDrug blockedDrug) {
        return respository.save(blockedDrug);
    }

    @Override
    public boolean delete(UUID id) {
        if(respository.existsById(id)){
            respository.deleteById(id);
            return true;
        }

        return false;
    }
}
