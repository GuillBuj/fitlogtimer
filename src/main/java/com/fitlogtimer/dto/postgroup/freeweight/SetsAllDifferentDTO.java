package com.fitlogtimer.dto.postgroup.freeweight;

import java.util.List;
import java.util.stream.Collectors;

import com.fitlogtimer.dto.base.SetBasicDTO;

public record SetsAllDifferentDTO(
        List<Object> blocks
) {
    @Override
    public final String toString() {
        return blocks.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" , "));
    }
}
