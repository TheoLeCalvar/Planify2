package com.planify.server.service;


import com.planify.server.models.Planning;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import com.planify.server.repo.ConstraintSynchroniseWithTAFRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConstraintSynchroniseWithTAFService {

    @Autowired
    private ConstraintSynchroniseWithTAFRepository constraintSynchroniseWithTAFRepository;
    

    List<ConstraintSynchroniseWithTAF> findAll() {
        return constraintSynchroniseWithTAFRepository.findAll();
    }

    Optional<ConstraintSynchroniseWithTAF> findById(Long id) {
        return constraintSynchroniseWithTAFRepository.findById(id);
    }

    ConstraintSynchroniseWithTAF add(Planning planning, Planning otherPlanning, boolean enabled) {
        return  constraintSynchroniseWithTAFRepository.save(new ConstraintSynchroniseWithTAF(planning, otherPlanning, enabled));
    }

    boolean deleteById(Long id) {
        if (constraintSynchroniseWithTAFRepository.existsById(id)) {
            constraintSynchroniseWithTAFRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }
    
    public void createForNewPlanning(List<ConstraintSynchroniseWithTAF> cSyncs, Planning newPlanning){
    	List<ConstraintSynchroniseWithTAF> newCSyncs = cSyncs.stream().map(cSync -> add(newPlanning, cSync.getOtherPlanning(), cSync.isEnabled())).toList();
        for (ConstraintSynchroniseWithTAF cs : newCSyncs) {
            constraintSynchroniseWithTAFRepository.save(cs);
        }
    }
}
