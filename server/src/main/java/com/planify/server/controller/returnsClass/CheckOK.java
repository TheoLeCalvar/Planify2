package com.planify.server.controller.returnsClass;

import java.util.List;

public class CheckOK {

    private List<TAFSynchronised> SynchronisedTafs;


    public CheckOK(List<TAFSynchronised> synchronisedTafs) {
        SynchronisedTafs = synchronisedTafs;
    }

    public List<TAFSynchronised> getSynchronisedTafs() {
        return SynchronisedTafs;
    }

    public void setSynchronisedTafs(List<TAFSynchronised> synchronisedTafs) {
        SynchronisedTafs = synchronisedTafs;
    }
}
