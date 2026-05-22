package ru.kpfu.itis.snetkov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.snetkov.entity.Group;
import ru.kpfu.itis.snetkov.entity.Post;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.repository.GroupRepository;
import ru.kpfu.itis.snetkov.repository.PostRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Integer id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Группа не найдена"));
    }

    public Group createGroup(Group group, User owner) {
        group.setOwner(owner);

        return groupRepository.save(group);
    }

    public Group updateGroup(Integer id, Group updatedGroup) {
        Group group = getGroupById(id);
        if (group == null) {throw new IllegalArgumentException("Группа не найдена");}

        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        group.setLogoUrl(updatedGroup.getLogoUrl());

        return groupRepository.save(group);
    }

    public void deleteGroup(Integer id) {
        groupRepository.deleteById(id);
    }

    public boolean isOwner(Integer groupId, User user) {
        Group group = getGroupById(groupId);
        return group.getOwner().getId().equals(user.getId());
    }

    public List<Post> getGroupPosts(Integer groupId) {
        return postRepository.findByGroupIdAndStatus(groupId, Post.PostStatus.PUBLISHED);
    }
}