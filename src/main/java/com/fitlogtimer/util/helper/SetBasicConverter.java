package com.fitlogtimer.util.helper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicWith1RMDTO;

@Component
public class SetBasicConverter {

    public List<SetBasicWith1RMDTO> convertToSetBasicWith1RMDTOList(List<SetBasicDTO> dtos) {
        return dtos.stream()
                .map(SetBasicWith1RMDTO::new) // Appel indirect à StatsService
                .toList();
    }

    public List<SetBasicDTO> convertSetBasicDTOList(List<SetBasicWith1RMDTO> dtos) {
        return dtos.stream()
                .map(dto -> new SetBasicDTO(dto.repNumber(), dto.weight()))
                .toList();
    }
}