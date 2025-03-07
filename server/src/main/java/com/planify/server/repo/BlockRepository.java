package com.planify.server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planify.server.models.Block;
import com.planify.server.models.Lesson;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    List<Block> findAll();

    Optional<Block> findById(Long id);

    List<Block> findByFirstLesson(Lesson lesson);

    List<Block> findByTitle(String title);

    Block save(Block block);

    void deleteById(Long id);

}
