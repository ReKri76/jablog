package io.rekri.jablog.controllers;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.DTO.SimpleResponse;
import io.rekri.jablog.service.UsersService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UsersController {

    private final UsersService usersService;

    @PostMapping(value = "/panel/{boardName}/add")
    public ResponseEntity<Void> addUser(@PathVariable("boardName") String boardName, @Valid @RequestBody Login login) {

        usersService.addUser(boardName, login);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/panel/{boardName}/{nickname}")
    public ResponseEntity<Void> deleteUser(@PathVariable("boardName") String boardName, @PathVariable("nickname") String nickname) {

        usersService.deleteUser(nickname, boardName);

        return ResponseEntity.ok().build();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ViewUsersResponse extends SimpleResponse{
        private List<String> users;
        private String boardName;
    }

    @GetMapping(value = "/panel/{boardName}")
    public ResponseEntity<ViewUsersResponse> viewUsers(@PathVariable("boardName") String boardName) {

        final List<String> usersName = usersService.viewUsers(boardName);

        final ViewUsersResponse res = new ViewUsersResponse();
        res.setBoardName(boardName);
        res.setUsers(usersName);
        res.setMessage("ok");
        res.setStatus(200);

        return ResponseEntity
                .status(200)
                .body(res);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PanelResponse extends SimpleResponse{
        private List<String> boardNames;
    }

    @GetMapping(value = "/panel") //на этой странице есть поле для логина и для управления в залогиненных досках как админ
    public ResponseEntity<PanelResponse> panel(){

        List<String> boardNames = Collections.emptyList();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth!=null)
            boardNames = usersService.getBoardsWhereThisAccountIsAdmin((String) auth.getPrincipal());

        final PanelResponse res = new PanelResponse();
        res.setBoardNames(boardNames);
        res.setMessage("ok");
        res.setStatus(200);

        return ResponseEntity
                .status(200)
                .body(res);
    }
}
