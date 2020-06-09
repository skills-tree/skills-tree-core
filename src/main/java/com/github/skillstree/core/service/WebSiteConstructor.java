package com.github.skillstree.core.service;

/**
 * Constructor of a single-page website representation of the skills tree.
 */
public interface WebSiteConstructor {

    /**
     * Updates skills tree used by the website.
     * @param skillsTreeJson skills tree in JSON string
     */
    void updateSkillsTree(String skillsTreeJson);
}
