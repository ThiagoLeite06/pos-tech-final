package com.br.susreceita.prescription.infrastructure.adapter.in.web;

import com.br.susreceita.prescription.application.port.in.BlockDrugUseCase;
import com.br.susreceita.prescription.application.port.in.DeleteBlockedDrugUseCase;
import com.br.susreceita.prescription.application.port.in.ListBlockedDrugsUseCase;
import com.br.susreceita.prescription.domain.model.BlockedDrug;
import com.br.susreceita.prescription.infrastructure.adapter.in.web.dto.BlockedDrugRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/receitas/bloqueios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final BlockDrugUseCase blockDrugUseCase;
    private final ListBlockedDrugsUseCase listBlockedDrugsUseCase;
    private final DeleteBlockedDrugUseCase deleteBlockedDrugUseCase;

    public AdminController(BlockDrugUseCase blockDrugUseCase, ListBlockedDrugsUseCase listBlockedDrugsUseCase, DeleteBlockedDrugUseCase deleteBlockedDrugUseCase) {
        this.blockDrugUseCase = blockDrugUseCase;
        this.listBlockedDrugsUseCase = listBlockedDrugsUseCase;
        this.deleteBlockedDrugUseCase = deleteBlockedDrugUseCase;
    }

    @PostMapping
    public ResponseEntity<String> save (@RequestBody BlockedDrugRequestDto dto){
        BlockedDrug blockedDrug = new BlockedDrug();

        blockedDrug.setActiveIngredient(dto.activeIngredient());
        blockedDrug.setReason(dto.reason());

        blockDrugUseCase.blockDrug(blockedDrug);

        return ResponseEntity.status(HttpStatus.CREATED).body("Droga adicionada na lista de bloqueios");
    }

    @GetMapping
    public ResponseEntity<List<BlockedDrug>> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(listBlockedDrugsUseCase.listBlockedDrugs(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        HttpStatus status = deleteBlockedDrugUseCase.deleteBlockedDrug(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).build();
    }
}
