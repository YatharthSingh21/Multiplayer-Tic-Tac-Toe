package com.tictac.server.service;

import com.tictac.server.model.GameResult;
import com.tictac.server.model.User;
import com.tictac.server.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(String username, String password) {
        Optional<User> existing = repo.findByUsername(username);
        if (existing.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // ✅ Use your constructor that encodes password
        User u = new User(username, password);
        return repo.save(u);
    }

    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = repo.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.auth(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void updateStats(String username, String result) {
        Optional<User> userOpt = repo.findByUsername(username);
        if (userOpt.isEmpty()) {
            System.out.println("⚠️ User not found: " + username);
            return;
        }

        User user = userOpt.get();

        switch (result) {
            case "WIN":
                user.setWins(user.getWins() + 1);
                break;
            case "LOSS":
                user.setLosses(user.getLosses() + 1);
                break;
            case "DRAW":
                user.setDraws(user.getDraws() + 1);
                break;
            default:
                System.out.println("⚠️ Unknown result type: " + result);
                return;
        }

        repo.save(user);
        System.out.println("✅ Updated stats for " + username + ": " + result);
    }

    public void updateStats(User u, GameResult result) {
        switch (result) {
            case WIN -> u.setWins(u.getWins() + 1);
            case DRAW -> u.setDraws(u.getDraws() + 1);
            case LOSS -> u.setLosses(u.getLosses() + 1);
        }
        repo.save(u);
    }

    public Optional<User> findByUsername(String username) { return repo.findByUsername(username); }
    public Optional<User> findById(Long id) { return repo.findById(id); }

    public List<User> getTopPlayers() { return repo.findTop10ByOrderByWinsDesc(); }
}
