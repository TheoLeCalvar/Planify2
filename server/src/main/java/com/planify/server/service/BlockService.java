package com.planify.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planify.server.models.Antecedence;
import com.planify.server.models.Block;
import com.planify.server.models.Lesson;
import com.planify.server.repo.BlockRepository;

@Service
public class BlockService {

    @Autowired
    private BlockRepository blockRepository;

    public Block addBlock(String title, Lesson lesson, String description) {
        Block block = new Block(lesson, title, description);
        blockRepository.save(block);
        return block;
    }


    public void save(Block block) {
        blockRepository.save(block);
    }

    public Optional<Block> findById(Long id) {
        Optional<Block> block = blockRepository.findById(id);
        return block;
    }

    public List<Block> findByFirstLesson(Lesson lesson) {
        return blockRepository.findByFirstLesson(lesson);
    }

    public List<Block> findAll() {
        return blockRepository.findAll();
    }

    public boolean deleteBlock(Long id) {
        if (blockRepository.existsById(id)) {
            blockRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
