package com.br.susreceita.prescription.domain.service;

import com.br.susreceita.prescription.application.port.in.BlockDrugUseCase;
import com.br.susreceita.prescription.application.port.in.DeleteBlockedDrugUseCase;
import com.br.susreceita.prescription.application.port.in.ListBlockedDrugsUseCase;
import com.br.susreceita.prescription.application.port.out.BlockedDrugRepositoryPort;
import com.br.susreceita.prescription.domain.model.BlockedDrug;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements BlockDrugUseCase, ListBlockedDrugsUseCase, DeleteBlockedDrugUseCase {
    private final BlockedDrugRepositoryPort repositoryPort;

    public AdminService(BlockedDrugRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void blockDrug(BlockedDrug blockedDrug) {
        repositoryPort.save(blockedDrug);
    }

    @Override
    public boolean deleteBlockedDrug(UUID id) {
        return repositoryPort.delete(id);
    }

    @Override
    public List<BlockedDrug> listBlockedDrugs(int page, int size) {
        return repositoryPort.findAllBlockedDrugs(page, size);
    }
}
