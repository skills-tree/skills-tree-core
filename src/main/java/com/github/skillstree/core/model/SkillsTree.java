package com.github.skillstree.core.model;

import java.util.Map;
import java.util.UUID;

public class SkillsTree {

    private Skill rootSkill;

    private Map<UUID, Skill> skills;

    public Skill getRootSkill() {
        return rootSkill;
    }

    public SkillsTree setRootSkill(Skill rootSkill) {
        this.rootSkill = rootSkill;
        return this;
    }

    public Map<UUID, Skill> getSkills() {
        return skills;
    }

    public SkillsTree setSkills(Map<UUID, Skill> skills) {
        this.skills = skills;
        return this;
    }
}
