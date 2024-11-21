package com.backend.test.repository;

import com.backend.test.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Integer save(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
    User update(User user);
    Integer deleteById(Long id);
    <S extends User> Iterable<S> saveAll(Iterable<S> entities);
}
