package com.br.susreceita.prescription.application.port.in.mapper;

import com.br.susreceita.prescription.application.port.in.MedicineCommand;
import com.br.susreceita.prescription.domain.model.Request;
import com.br.susreceita.prescription.domain.model.RequestItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommandToDomainMapper {

    public List<RequestItem> toRequestItems(List<MedicineCommand> commands, Request request) {
        if (commands == null) {
            return Collections.emptyList();
        }

        return commands.stream()
            .map(command -> toRequestItem(command, request))
            .collect(Collectors.toList());
    }

    public RequestItem toRequestItem(MedicineCommand command, Request request) {
        if (command == null) {
            return null;
        }

        RequestItem item = new RequestItem();
        item.setRequest(request);
        item.setName(command.name());
        item.setDosage(command.dosage());
        
        return item;
    }
}
