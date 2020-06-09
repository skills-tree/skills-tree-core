package com.github.skillstree.core.model;

import java.util.Objects;
import java.util.UUID;

public class UserSkill {

    private UUID skillId;

    private int level;

    public UserSkill() {
    }

    public UserSkill(UUID skillId, int level) {
        this.skillId = skillId;
        this.level = level;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public UserSkill setSkillId(UUID skillId) {
        this.skillId = skillId;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public UserSkill setLevel(int level) {
        this.level = level;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSkill userSkill = (UserSkill) o;
        return level == userSkill.level &&
                Objects.equals(skillId, userSkill.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId, level);
    }

    @Override
    public String toString() {
        return "UserSkill{" +
                "skillId='" + skillId + '\'' +
                ", level=" + level +
                '}';
    }
}
