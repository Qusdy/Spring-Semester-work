package ru.kpfu.itis.snetkov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.snetkov.dto.TagDto;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.Tag;
import ru.kpfu.itis.snetkov.mapper.TagMapper;
import ru.kpfu.itis.snetkov.repository.TagRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public Tag findOrCreate(String tagName) {
        String cleanName = tagName.trim().toLowerCase();
        return tagRepository.findByName(cleanName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(cleanName);
                    return tagRepository.save(newTag);
                });
    }

    public Tag getTagById(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Тег не найден"));
    }

    public List<TagDto> getTagsByPost(Post post) {
        return tagMapper.toDtoList(post.getTags().stream().toList());
    }

    public List<TagDto> searchTags(String query) {
        if (query == null || query.length() < 2) {
            return List.of();
        }
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(query);
        return tagMapper.toDtoList(tags);
    }
}