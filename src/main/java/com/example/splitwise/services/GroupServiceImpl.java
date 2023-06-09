package com.example.splitwise.services;

import com.example.splitwise.dto.GroupRequestDto;
import com.example.splitwise.dto.GroupResponseDto;
import com.example.splitwise.dto.UserResponseDto;
import com.example.splitwise.entities.Group;
import com.example.splitwise.entities.User;
import com.example.splitwise.entities.UserGroup;
import com.example.splitwise.exceptions.CustomerNotFoundException;
import com.example.splitwise.exceptions.GroupNotFoundException;
import com.example.splitwise.exceptions.GroupWithoutAdminException;
import com.example.splitwise.repositories.GroupRepository;
import com.example.splitwise.repositories.UserGroupRepository;
import com.example.splitwise.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 28/9/17.
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Transactional
    @Override
    public GroupResponseDto addNewGroup(GroupRequestDto groupRequestDto) throws CustomerNotFoundException {
        if (groupRequestDto.getUserEmails() == null || groupRequestDto.getUserEmails().isEmpty()) {
            throw new GroupWithoutAdminException("User list is empty. Group without admin not possible!!!");
        }
        List<User> members = userRepository.findByEmailIn(groupRequestDto.getUserEmails());
        User admin = members.get(0);

        Group group = Group.builder()
                .name(groupRequestDto.getName())
                .adminUser(admin)
                .build();
        groupRepository.save(group);

        List<UserGroup> userGroupList = new ArrayList<>();
        for (User user : members) {
            UserGroup usrGrp = new UserGroup(user);
            usrGrp.setGang(group);
            userGroupRepository.save(usrGrp);
            userGroupList.add(usrGrp);
        }
        group.setUserGroups(userGroupList);
        GroupResponseDto dto = getGroupResponseDto(group);
        return dto;
    }

    private GroupResponseDto getGroupResponseDto(Group group) {

        UserResponseDto adminDto = UserResponseDto.builder()
                .id(group.getAdminUser().getId())
                .name(group.getAdminUser().getName())
                .email(group.getAdminUser().getEmail())
                .contact(group.getAdminUser().getContact())
                .build();

        List<UserResponseDto> userList = new ArrayList<>();
        for (UserGroup usrgrp : group.getUserGroups()) {
            UserResponseDto responseDto = UserResponseDto.builder()
                    .id(usrgrp.getUser().getId())
                    .name(usrgrp.getUser().getName())
                    .email(usrgrp.getUser().getEmail())
                    .contact(usrgrp.getUser().getContact())
                    .build();
            userList.add(responseDto);
        }

        GroupResponseDto dto = GroupResponseDto.builder()
                .id(group.getId())
                .admin(adminDto)
                .name(group.getName())
                .users(userList)
                .build();
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<GroupResponseDto> getAllGroups() throws CustomerNotFoundException {
        List<Group> groups = (List<Group>) groupRepository.findAll();
        List<GroupResponseDto> groupsList = convertEntityToDto(groups);
        return groupsList;
    }

    private List<GroupResponseDto> convertEntityToDto(List<Group> groups) {
        List<GroupResponseDto> dtos = new ArrayList<>();
        for (Group grp : groups) {
            GroupResponseDto dto = getGroupResponseDto(grp);
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    @Override
    public void addUserToGroup(GroupRequestDto groupRequestDto) throws GroupNotFoundException, CustomerNotFoundException {

        Group grp = groupRepository.findById(groupRequestDto.getId()).get();
        if (grp == null) {
            throw new GroupNotFoundException("Group with id " + groupRequestDto.getId() + " does not exists");
        }
        List<User> userList = userRepository.findByEmailIn(groupRequestDto.getUserEmails());
        if (userList.size() != groupRequestDto.getUserEmails().size()) {
            throw new CustomerNotFoundException("Some customers does not exists");
        }

        // add users to database
        List<UserGroup> userGroupList = new ArrayList<>();
        for (User usr : userList) {
            UserGroup userGroup = UserGroup.builder()
                    .gang(grp)
                    .user(usr)
                    .build();
            userGroupList.add(userGroup);
        }
        userGroupRepository.saveAll(userGroupList);
    }
}
