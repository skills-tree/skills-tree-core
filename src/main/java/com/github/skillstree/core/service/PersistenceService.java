package com.github.skillstree.core.service;

import java.util.*;

import com.github.skillstree.core.model.SkillsIdMapping;
import com.github.skillstree.core.model.UserSkill;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The database access layer, which works with skills tree entities.
 */
public class PersistenceService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Create an instance of the service. The following environment variables should be provided:
     * <ul>
     *     <li>RDB_DB_NAME</li>
     *     <li>RDB_USERNAME</li>
     *     <li>RDB_PASSWORD</li>
     *     <li>RDB_HOSTNAME</li>
     *     <li>RDB_PORT</li>
     * </ul>
     */
    public PersistenceService() {
        String dbName = Objects.requireNonNullElse(System.getenv("RDB_DB_NAME"), "se_skills_tree");
        String userName = Objects.requireNonNullElse(System.getenv("RDB_USERNAME"), "postgres");
        String password = Objects.requireNonNullElse(System.getenv("RDB_PASSWORD"), "postgres");
        String hostname = System.getenv("RDB_HOSTNAME");
        String port = Objects.requireNonNullElse(System.getenv("RDB_PORT"), "5432");
        String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName;

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url(jdbcUrl);
        dataSourceBuilder.username(userName);
        dataSourceBuilder.password(password);

        jdbcTemplate = new JdbcTemplate(dataSourceBuilder.build());
    }

    /**
     * Initializes the neccessary tables if the don't exist.
     */
    public void initTables() {
        jdbcTemplate.update("create table if not exists skills " +
                "(id uuid, code varchar constraint skills_pk primary key)");

        jdbcTemplate.update("create table if not exists last_commit (last_commit varchar(128))");

        jdbcTemplate.update("create table if not exists user_skills " +
                "(userId varchar(256), skillId uuid, level int, " +
                "constraint user_skills_pk primary key (userId, skillId))");
    }

    /**
     * Retrieves {@link SkillsIdMapping} by its code.
     * @param code code of {@link SkillsIdMapping}
     * @return {@link SkillsIdMapping}
     */
    public SkillsIdMapping getByCode(String code) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM skills WHERE code=?", SkillsIdMapping.class, code);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves all {@link SkillsIdMapping}.
     * @return skills id mappings as {@link Map} with code as a key and its id as a value.
     */
    public Map<String, UUID> getAllMappings() {
        return jdbcTemplate.query("SELECT id, code FROM skills", rs -> {
            Map<String, UUID> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getString("code"), rs.getObject("id", UUID.class));
            }
            return result;
        });
    }

    /**
     * Saves {@link SkillsIdMapping} entity.
     * @param skillsIdMapping entity to save
     */
    public void save(SkillsIdMapping skillsIdMapping) {
        try {
            SkillsIdMapping simKnown = jdbcTemplate.queryForObject(
                    "SELECT * FROM skills WHERE code=?", new Object[]{skillsIdMapping.getCode()}, (rs, rowNum) ->
                            new SkillsIdMapping(rs.getObject("id", UUID.class), rs.getString("code")));
            if (!simKnown.getId().equals(skillsIdMapping.getId())) {
                jdbcTemplate.update("UPDATE skills SET id=? WHERE code=?",
                        skillsIdMapping.getId(), skillsIdMapping.getCode());
            }

        } catch (Exception e) {
            jdbcTemplate.update("INSERT INTO skills (id, code) VALUES(?,?)",
                    skillsIdMapping.getId(), skillsIdMapping.getCode());
        }
    }

    /**
     * Saves last commit's id.
     * @param lastCommit the id of last commit made in skills repository
     */
    public void save(String lastCommit) {
        String lastKnownCommit = jdbcTemplate.query("SELECT last_commit FROM last_commit LIMIT 1",
                rs -> rs.next() ? rs.getString(1) : null);
        if (lastKnownCommit == null) {
            jdbcTemplate.update("INSERT INTO last_commit (last_commit) VALUES(?)", lastCommit);
        } else {
            jdbcTemplate.update("UPDATE last_commit SET last_commit=?", lastCommit);
        }
    }

    /**
     * Returns the last commit made in skills repository.
     * @return id of the commit
     */
    public String getLastCommit() {
        return jdbcTemplate.query("SELECT last_commit FROM last_commit LIMIT 1",
                rs -> rs.next() ? rs.getString(1) : null);
    }

    /**
     * Retrieves the list of obtained by the user skills by user ID.
     * @param userId user ID
     * @return list of obtained by the user skills
     */
    public List<UserSkill> getUserSkills(String userId) {
        return jdbcTemplate.query("SELECT skillId, level FROM user_skills WHERE userId=?", new Object[]{userId},
                rs -> {
                    List<UserSkill> userSkills = new ArrayList<>();
                    while (rs.next()) {
                        var userSkill = new UserSkill(
                                rs.getObject("skillId", UUID.class), rs.getInt("level"));
                        userSkills.add(userSkill);
                    }
                    return userSkills;
                });
    }

    /**
     * Updates the list of obtained by the user skills.
     * @param userId     user ID
     * @param userSkills list of skills
     */
    public void updateUserSkills(String userId, List<UserSkill> userSkills) {
        userSkills.forEach(us -> updateUserSingleSkill(userId, us));
    }

    /**
     * Inserts a new skills to the list of obtained by the user skills.
     * @param userId    user ID
     * @param userSkill skill
     */
    private void updateUserSingleSkill(String userId, UserSkill userSkill) {
        jdbcTemplate.update("INSERT INTO user_skills (userId, skillId, level) VALUES (?, ?, ?) " +
                        "ON CONFLICT (userid, skillid) DO UPDATE SET level = ?",
                userId, userSkill.getSkillId(), userSkill.getLevel(), userSkill.getLevel());
    }
}
