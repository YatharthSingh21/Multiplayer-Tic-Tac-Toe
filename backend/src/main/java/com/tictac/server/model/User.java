package com.tictac.server.model;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String hashedPswrd;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int wins = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int losses = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int draws = 0;

    public User() {}

    public User(String username, String pswrd) {
        this.username = username;
        this.hashedPswrd = new BCryptPasswordEncoder().encode(pswrd);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public String getPassword() {
        return hashedPswrd;
    }

    public boolean auth(String plainPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(plainPassword, this.hashedPswrd);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", wins=" + wins +
                ", losses=" + losses +
                ", draws=" + draws +
                '}';
    }
}
