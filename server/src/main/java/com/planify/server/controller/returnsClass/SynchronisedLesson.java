package com.planify.server.controller.returnsClass;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class SynchronisedLesson {

    @JsonManagedReference
    private Tuple taf;

    @JsonManagedReference
    private Tuple ue;

    @JsonManagedReference
    private Tuple lesson;

    private class Tuple {
        private Long id;
        private String name;

        public Tuple() {
        }

        public Tuple(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

}
