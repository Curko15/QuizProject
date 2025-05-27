package ppks.projekt.backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ppks.projekt.backend.dto.groupdto.CreateGroupDto;
import ppks.projekt.backend.dto.groupdto.GroupDto;
import ppks.projekt.backend.service.GroupService;

import java.util.List;

@RestController
@AllArgsConstructor
public class GroupController {

    private GroupService groupService;

    @GetMapping("/groups")
    public ResponseEntity<List<GroupDto>> getGroupsForUser(Authentication authentication){
        return ResponseEntity.ok(groupService.getGroupsForUser(authentication.getName()));
    }

    @PostMapping("/group")
    public ResponseEntity<GroupDto> createGroup(@RequestBody CreateGroupDto groupDto){
        return ResponseEntity.ok(groupService.createGroup(groupDto));
    }
}
