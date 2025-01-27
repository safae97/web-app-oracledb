
package org.example.oracle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.oracle.service.OracleUserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oracle-users")
public class OracleUserController {

    @Autowired
    private OracleUserService oracleUserService;


    @GetMapping
    public String listUsers(Model model) {
        List<Map<String, Object>> users = oracleUserService.listUsers();
        Map<String, String> settings = oracleUserService.getPasswordAndLockSettings();

        model.addAttribute("users", users);
        model.addAttribute("settings", settings); // Add settings to the model
        return "user-list";
    }

    @GetMapping("/create")
    public String createUserForm() {
        return "user-create";
    }

    @PostMapping("/create")
    public String createUser(@RequestParam String username, @RequestParam String password,
                             @RequestParam String tablespace, @RequestParam int quota) {
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Tablespace: " + tablespace);
        System.out.println("Quota: " + quota); // Ajoutez cette ligne


        oracleUserService.createUser(username, password, tablespace, quota);
        return "redirect:/oracle-users";
    }



    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username) {

        oracleUserService.deleteUser(username);
        return "redirect:/oracle-users";
    }

    @GetMapping("/user-actions")
public String userActions() {
    return "user-actions";
}

    @PostMapping("/change-quota")
    public String changeQuota(@RequestParam String username, @RequestParam int quota,
                              @RequestParam String tablespace) {
        oracleUserService.updateUserQuota(username, quota, tablespace);
        return "redirect:/oracle-users";
    }
    @PostMapping("/change-tablespace")
    public String changeTablespace(@RequestParam String username, @RequestParam String tablespace) {
        oracleUserService.updateUserTablespace(username, tablespace);
        return "redirect:/oracle-users";
    }

    @PostMapping("/grant-role")
    public String grantRole(@RequestParam String username, @RequestParam String role,
                            @RequestParam(required = false, defaultValue = "false") boolean adminOption) {
        oracleUserService.assignRoleToUser(username, role, adminOption);
        return "redirect:/oracle-users";
    }

    @PostMapping("/lock-account")
    public String lockAccount(@RequestParam String username) {
        oracleUserService.lockUserAccount(username);
        return "redirect:/oracle-users";
    }

    @PostMapping("/unlock-account")
    public String unlockAccount(@RequestParam String username) {
        oracleUserService.unlockUserAccount(username);
        return "redirect:/oracle-users";
    }

    @PostMapping("/change-password-expiration")
    public String changePasswordExpiration(@RequestParam int expirationDays) {
        oracleUserService.changePasswordExpiration(expirationDays);
        return "redirect:/oracle-users/user-actions";
    }

    @PostMapping("/change-lock-time")
    public String changeLockTime(@RequestParam int lockTime) {
        oracleUserService.changePasswordLockTime(lockTime);
        return "redirect:/oracle-users/user-actions";
    }

}
