package ru.kpfu.itis.snetkov.mapper;

import org.springframework.stereotype.Component;
import ru.kpfu.itis.snetkov.dto.TagDto;
import ru.kpfu.itis.snetkov.entity.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TagMapper {

    public TagDto toDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return new TagDto(tag.getId(), tag.getName());
    }

    public List<TagDto> toDtoList(List<Tag> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Tag toEntity(TagDto dto) {
        if (dto == null) {
            return null;
        }
        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        return tag;
    }
}