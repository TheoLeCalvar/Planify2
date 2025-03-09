package com.planify.server.service;


import com.planify.server.controller.returnsClass.Config;
import com.planify.server.models.Planning;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF;
import com.planify.server.models.constraints.ConstraintSynchroniseWithTAF.ConstraintsSynchroniseWithTAFId;
import com.planify.server.repo.ConstraintSynchroniseWithTAFRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConstraintSynchroniseWithTAFService {

    @Autowired
    private ConstraintSynchroniseWithTAFRepository constraintSynchroniseWithTAFRepository;
    
    public void save(ConstraintSynchroniseWithTAF constraintSynchroniseWithTAF) {
    	constraintSynchroniseWithTAFRepository.save(constraintSynchroniseWithTAF);
    }

    List<ConstraintSynchroniseWithTAF> findAll() {
        return constraintSynchroniseWithTAFRepository.findAll();
    }

    Optional<ConstraintSynchroniseWithTAF> findById(ConstraintsSynchroniseWithTAFId id) {
        return constraintSynchroniseWithTAFRepository.findById(id);
    }

    public ConstraintSynchroniseWithTAF add(Planning planning, Planning otherPlanning, boolean enabled) {
        return  constraintSynchroniseWithTAFRepository.save(new ConstraintSynchroniseWithTAF(planning, otherPlanning, enabled));
    }

    boolean deleteById(ConstraintsSynchroniseWithTAFId id) {
        if (constraintSynchroniseWithTAFRepository.existsById(id)) {
            constraintSynchroniseWithTAFRepository.deleteById(id);
            return true;
        }
        else {
            return false;
        }
    }

    public void updateConfig(ConstraintSynchroniseWithTAF c ,Config.CSyncrho cs) {
        if (cs.isEnabled() != null) c.setEnabled(cs.isEnabled());
        save(c);
    }
    
    public List<ConstraintSynchroniseWithTAF> createForNewPlanning(List<ConstraintSynchroniseWithTAF> cSyncs, Planning newPlanning){
    	List<ConstraintSynchroniseWithTAF> newCSyncs = cSyncs.stream().map(cSync -> add(newPlanning, cSync.getOtherPlanning(), cSync.isEnabled())).toList();
        System.out.println("AAAAAAAAAAAAAAA " + newCSyncs.size());
    	for (ConstraintSynchroniseWithTAF cs : newCSyncs) {
            constraintSynchroniseWithTAFRepository.save(cs);
        }
        return newCSyncs;
    }
    
    public void changeOtherPlanning(ConstraintSynchroniseWithTAF cSync, Planning newOtherPlanning) {
    	cSync.setOtherPlanning(newOtherPlanning);
    	save(cSync);
    }
}
