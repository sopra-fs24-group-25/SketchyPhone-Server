package ch.uzh.ifi.hase.soprafs24.service.Game;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate; 

@Service
public class SessionService {
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public void addUser(User user) {
        users.put(user.getId().toString(), user);
        updateUserList();
    }

    public void removeUser(String userId) {
        users.remove(userId);
        updateUserList();
    }

    private void updateUserList() {
        // Broadcasting user list to clients
        messagingTemplate.convertAndSend("/topic/users", users.values());
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }
}
