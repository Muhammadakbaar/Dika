package com.backend.test.service;

import com.backend.test.entity.User;
import com.backend.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @CacheEvict(value = "users", allEntries = true)
    @CachePut(value = "users", key = "#user.id")
    @Override
    public Integer save(User user) {
    logger.info("Saving user: {}", user);
    try {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO users(firstname, lastname, email, address) VALUES (?,?,?,?) RETURNING id",
                        new String[] { "id" });
                ps.setString(1, user.getFirstname());
                ps.setString(2, user.getLastname());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getAddress());
                return ps;
            }
        }, keyHolder);
        Integer generatedId = keyHolder.getKey().intValue();
        user.setId(generatedId);
        logger.info("User saved with id: {}", generatedId);
        return generatedId;
    } catch (Exception e) {
        logger.error("Error saving user: {}", e.getMessage());
        throw new RuntimeException("Error saving user", e);
    }
}
    @Cacheable(value = "users")
    @Override
    public List<User> findAll() {
        logger.info("Finding all users");
        return jdbcTemplate.query("SELECT * FROM users", BeanPropertyRowMapper.newInstance(User.class))
                .stream()
                .collect(Collectors.toList());
    }


    @Cacheable(value = "users", key = "#id")
    @Override
    public Optional<User> findById(Long id) {
        logger.info("Finding user by id: {}", id);
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?",
                    BeanPropertyRowMapper.newInstance(User.class), id);
            return Optional.ofNullable(user);
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.warn("User not found with id: {}", id);
            return Optional.empty();
        }
    }
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#user.id"),
            @CacheEvict(value = "users", allEntries = true)
    }, put = {
            @CachePut(value = "users", key = "#user.id")
    })
    @Override
    public User update(User user) {
        logger.info("Updating user: {}", user);
         jdbcTemplate.update("UPDATE users SET firstname=?, lastname=?, email=?, address=? WHERE id=?",
                user.getFirstname(), user.getLastname(), user.getEmail(), user.getAddress(), user.getId());
        return user;
    }

    @CacheEvict(value = "users", key = "#id", allEntries = true)
    @Override
    public Integer deleteById(Long id) {
        logger.info("Deleting user by id: {}", id);
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
    }

    @CachePut(value = "users", key = "#user.id")
    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        for (S user : entities) {
            save(user);
        }
        return entities;
    }
}




